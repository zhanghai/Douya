/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.account.app

import android.accounts.AbstractAccountAuthenticator
import android.accounts.Account
import android.accounts.AccountAuthenticatorResponse
import android.accounts.AccountManager
import android.accounts.NetworkErrorException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import me.zhanghai.android.douya.account.info.AccountContract
import me.zhanghai.android.douya.account.ui.AuthenticationActivity
import me.zhanghai.android.douya.account.ui.AuthenticationFragmentArgs
import me.zhanghai.android.douya.account.info.AuthenticationMode
import me.zhanghai.android.douya.network.api.ApiContract.Response.Error.Codes
import me.zhanghai.android.douya.network.api.ApiError
import me.zhanghai.android.douya.network.api.ApiService
import me.zhanghai.android.douya.network.api.info.AuthenticationResponse
import timber.log.Timber

class Authenticator(private val context: Context) : AbstractAccountAuthenticator(context) {

    private val accountManager by lazy { AccountManager.get(context) }

    override fun editProperties(response: AccountAuthenticatorResponse, accountType: String) =
        throw UnsupportedOperationException()

    @Throws(NetworkErrorException::class)
    override fun addAccount(
        response: AccountAuthenticatorResponse,
        accountType: String,
        authTokenType: String,
        requiredFeatures: Array<String>,
        options: Bundle
    ) = createAuthenticationBundle(
        AuthenticationMode.ADD, null, response)

    @Throws(NetworkErrorException::class)
    override fun confirmCredentials(
        response: AccountAuthenticatorResponse,
        account: Account,
        options: Bundle
    ) = createAuthenticationBundle(
        AuthenticationMode.CONFIRM, account, response)

    @Throws(NetworkErrorException::class)
    override fun getAuthToken(response: AccountAuthenticatorResponse, account: Account,
                              authTokenType: String, options: Bundle): Bundle {
        if (account.type != AccountContract.ACCOUNT_TYPE) {
            return createErrorBundle(AccountManager.ERROR_CODE_BAD_ARGUMENTS,
                "Invalid account type: ${account.type}")
        }
        if (authTokenType != AccountContract.AUTH_TOKEN_TYPE) {
            return createErrorBundle(AccountManager.ERROR_CODE_BAD_ARGUMENTS,
                "Invalid authTokenType: $authTokenType")
        }
        // http://stackoverflow.com/questions/11434621/login-in-twice-when-using-syncadapters
        var authToken = accountManager.peekAuthToken(account, authTokenType)
        if (authToken.isNullOrEmpty()) {
            val refreshToken: String? = accountManager.getRefreshToken(account)
            if (!refreshToken.isNullOrEmpty()) {
                try {
                    val authenticationResponse: AuthenticationResponse = ApiService.getInstance()
                            .authenticate(authTokenType, refreshToken).execute()
                    authToken = authenticationResponse.accessToken
                    with(accountManager) {
                        setRefreshToken(account, authenticationResponse.refreshToken)
                        setUserId(account, authenticationResponse.userId)
                        setUserName(account, authenticationResponse.userName)
                    }
                } catch (e: ApiError) {
                    Timber.e(e.toString())
                    // Try again with XAuth afterwards.
                }
            }
        }
        if (authToken.isNullOrEmpty()) {
            val password: String? = accountManager.getPassword(account)
            if (password.isNullOrEmpty()) {
                return createErrorBundle(AccountManager.ERROR_CODE_BAD_AUTHENTICATION,
                    "AccountManager.getPassword() returned null or empty")
            }
            val apiService: ApiService = ApiService.getInstance()
            try {
                val authenticationResponse: AuthenticationResponse = apiService.authenticate(
                        authTokenType, account.name, password).execute()
                authToken = authenticationResponse.accessToken
                with(accountManager) {
                    setRefreshToken(account, authenticationResponse.refreshToken)
                    setUserId(account, authenticationResponse.userId)
                    setUserName(account, authenticationResponse.userName)
                }
            } catch (e: ApiError) {
                Timber.e(e.toString())
                return if (e.bodyJson != null && e.code !== Codes.Custom.INVALID_ERROR_RESPONSE) {
                    val message: String = ApiError.getErrorString(e, context)
                    when (e.code) {
                        Codes.Token.USERNAME_PASSWORD_MISMATCH -> {
                            ApiService.getInstance().cancelApiRequests()
                            return createAuthenticationBundle(
                                AuthenticationMode.UPDATE, account, response
                            ).also { addAuthenticationFailedMessage(it, message) }
                        }
                        Codes.Token.INVALID_USER, Codes.Token.USER_HAS_BLOCKED,
                        Codes.Token.USER_LOCKED -> {
                            ApiService.getInstance().cancelApiRequests()
                            return createIntentBundle(AuthenticatorUtils.makeWebsiteIntent(context))
                                .also { addAuthenticationFailedMessage(it, message) }
                        }
                    }
                    createErrorBundle(AccountManager.ERROR_CODE_BAD_AUTHENTICATION, message)
                } else if (e.response != null) {
                    createErrorBundle(AccountManager.ERROR_CODE_INVALID_RESPONSE, e)
                } else {
                    createErrorBundle(AccountManager.ERROR_CODE_NETWORK_ERROR, e)
                }
            }
        }
        if (authToken.isNullOrEmpty()) {
            return createErrorBundle(AccountManager.ERROR_CODE_INVALID_RESPONSE,
                    "Server-returned auth token is null or empty")
        }
        return createSuccessBundle(account, authToken)
    }

    override fun getAuthTokenLabel(authTokenType: String): String? = null

    @Throws(NetworkErrorException::class)
    override fun updateCredentials(
        response: AccountAuthenticatorResponse,
        account: Account,
        authTokenType: String,
        options: Bundle
    ) = createAuthenticationBundle(
        AuthenticationMode.UPDATE, account, response)

    @Throws(NetworkErrorException::class)
    override fun hasFeatures(response: AccountAuthenticatorResponse, account: Account,
                             features: Array<String>): Bundle? = null

    private fun createIntentBundle(intent: Intent) = Bundle().apply {
        putParcelable(AccountManager.KEY_INTENT, intent)
    }

    private fun createAuthenticationBundle(
        mode: AuthenticationMode,
        account: Account?,
        response: AccountAuthenticatorResponse?
    ) = createIntentBundle(Intent(context, AuthenticationActivity::class.java).apply {
        putExtras(AuthenticationFragmentArgs(mode, account?.name, response).toBundle())
    })

    private fun addAuthenticationFailedMessage(bundle: Bundle, message: String) =
        bundle.putString(AccountManager.KEY_AUTH_FAILED_MESSAGE, message)

    private fun createErrorBundle(code: Int, message: String) = Bundle().apply {
        putInt(AccountManager.KEY_ERROR_CODE, code)
        putString(AccountManager.KEY_ERROR_MESSAGE, message)
    }

    private fun createErrorBundle(code: Int, throwable: Throwable) =
        createErrorBundle(code, throwable.toString())

    private fun createSuccessBundle(account: Account, authToken: String) = Bundle().apply {
        putString(AccountManager.KEY_ACCOUNT_NAME, account.name)
        putString(AccountManager.KEY_ACCOUNT_TYPE, account.type)
        putString(AccountManager.KEY_AUTHTOKEN, authToken)
    }
}
