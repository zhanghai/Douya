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
import kotlinx.coroutines.runBlocking
import me.zhanghai.android.douya.account.info.AccountContract
import me.zhanghai.android.douya.account.info.AuthenticatorMode
import me.zhanghai.android.douya.account.ui.AuthenticatorActivity
import me.zhanghai.android.douya.account.ui.AuthenticatorFragmentArgs
import me.zhanghai.android.douya.api.app.ApiService
import me.zhanghai.android.douya.api.app.errorResponse
import me.zhanghai.android.douya.api.app.message
import me.zhanghai.android.douya.api.info.ApiContract
import retrofit2.HttpException
import timber.log.Timber

class Authenticator(private val context: Context) : AbstractAccountAuthenticator(context) {
    override fun editProperties(
        response: AccountAuthenticatorResponse,
        accountType: String
    ): Bundle? = throw UnsupportedOperationException()

    @Throws(NetworkErrorException::class)
    override fun addAccount(
        response: AccountAuthenticatorResponse,
        accountType: String,
        authTokenType: String?,
        requiredFeatures: Array<String>?,
        options: Bundle
    ): Bundle? = createAuthenticationBundle(AuthenticatorMode.ADD, null, response)

    @Throws(NetworkErrorException::class)
    override fun confirmCredentials(
        response: AccountAuthenticatorResponse,
        account: Account,
        options: Bundle?
    ): Bundle? = createAuthenticationBundle(AuthenticatorMode.CONFIRM, account, response)

    @Throws(NetworkErrorException::class)
    override fun getAuthToken(
        response: AccountAuthenticatorResponse,
        account: Account,
        authTokenType: String,
        options: Bundle
    ): Bundle? {
        if (account.type != AccountContract.ACCOUNT_TYPE) {
            return createErrorBundle(
                AccountManager.ERROR_CODE_BAD_ARGUMENTS,
                "Invalid account type: ${account.type}"
            )
        }
        if (authTokenType != AccountContract.AUTH_TOKEN_TYPE) {
            return createErrorBundle(
                AccountManager.ERROR_CODE_BAD_ARGUMENTS,
                "Invalid authTokenType: $authTokenType"
            )
        }

        // http://stackoverflow.com/questions/11434621/login-in-twice-when-using-syncadapters
        var authToken = account.peekAuthToken()

        if (authToken == null) {
            do {
                val refreshToken: String = account.refreshToken ?: break
                val session = try {
                    runBlocking { ApiService.authenticate(refreshToken) }
                } catch (e: Exception) {
                    Timber.e(e.toString())
                    break
                }
                authToken = session.accessToken
                account.refreshToken = session.refreshToken
                account.userId = session.userId
                account.userName = session.userName
            } while (false)
        }

        if (authToken == null) {
            val password = account.password ?: return createErrorBundle(
                AccountManager.ERROR_CODE_BAD_AUTHENTICATION,
                "AccountManager.getPassword() returned null"
            )
            val session = try {
                runBlocking { ApiService.authenticate(account.name, password) }
            } catch (e: Exception) {
                Timber.e(e.toString())
                return if (e is HttpException) {
                    val errorResponse = e.errorResponse
                    if (errorResponse != null) {
                        when (errorResponse.code) {
                            ApiContract.Error.Codes.USERNAME_PASSWORD_MISMATCH -> {
                                ApiService.cancelApiRequests()
                                createAuthenticationBundle(
                                    AuthenticatorMode.UPDATE, account, response
                                ).apply {
                                    putString(
                                        AccountManager.KEY_AUTH_FAILED_MESSAGE,
                                        errorResponse.message
                                    )
                                }
                            }
                            else -> createErrorBundle(
                                AccountManager.ERROR_CODE_BAD_AUTHENTICATION,
                                errorResponse.message
                            )
                        }
                    } else {
                        createErrorBundle(AccountManager.ERROR_CODE_INVALID_RESPONSE, e)
                    }
                } else {
                    createErrorBundle(AccountManager.ERROR_CODE_NETWORK_ERROR, e)
                }
            }
            authToken = session.accessToken
            account.refreshToken = session.refreshToken
            account.userId = session.userId
            account.userName = session.userName
        }

        return Bundle().apply {
            putString(AccountManager.KEY_ACCOUNT_NAME, account.name)
            putString(AccountManager.KEY_ACCOUNT_TYPE, account.type)
            putString(AccountManager.KEY_AUTHTOKEN, authToken)
        }
    }

    override fun getAuthTokenLabel(authTokenType: String): String? = null

    @Throws(NetworkErrorException::class)
    override fun updateCredentials(
        response: AccountAuthenticatorResponse,
        account: Account,
        authTokenType: String?,
        options: Bundle?
    ): Bundle? = createAuthenticationBundle(AuthenticatorMode.UPDATE, account, response)

    @Throws(NetworkErrorException::class)
    override fun hasFeatures(
        response: AccountAuthenticatorResponse,
        account: Account,
        features: Array<String>
    ): Bundle? = Bundle().apply {
        putBoolean(AccountManager.KEY_BOOLEAN_RESULT, false)
    }

    private fun createIntentBundle(intent: Intent): Bundle = Bundle().apply {
        putParcelable(AccountManager.KEY_INTENT, intent)
    }

    private fun createAuthenticationBundle(
        mode: AuthenticatorMode,
        account: Account?,
        response: AccountAuthenticatorResponse?
    ): Bundle = createIntentBundle(Intent(context, AuthenticatorActivity::class.java).apply {
        putExtras(AuthenticatorFragmentArgs(mode.ordinal, account?.name, response).toBundle())
    })

    private fun createErrorBundle(code: Int, message: String): Bundle = Bundle().apply {
        putInt(AccountManager.KEY_ERROR_CODE, code)
        putString(AccountManager.KEY_ERROR_MESSAGE, message)
    }

    private fun createErrorBundle(code: Int, exception: Exception) =
        createErrorBundle(code, exception.toString())
}
