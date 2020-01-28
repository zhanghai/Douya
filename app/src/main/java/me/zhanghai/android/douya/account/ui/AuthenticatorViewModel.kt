/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.account.ui

import android.accounts.AccountManager
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.zhanghai.android.douya.R
import me.zhanghai.android.douya.account.app.Account
import me.zhanghai.android.douya.account.app.activeAccount
import me.zhanghai.android.douya.account.app.addAccountExplicitly
import me.zhanghai.android.douya.account.app.ownAccounts
import me.zhanghai.android.douya.account.app.refreshToken
import me.zhanghai.android.douya.account.app.setAuthToken
import me.zhanghai.android.douya.account.app.userId
import me.zhanghai.android.douya.account.app.userName
import me.zhanghai.android.douya.account.info.AuthenticatorMode
import me.zhanghai.android.douya.api.app.ApiService
import me.zhanghai.android.douya.app.accountManager
import me.zhanghai.android.douya.app.appContext
import me.zhanghai.android.douya.arch.DistinctMutableLiveData
import me.zhanghai.android.douya.arch.EventLiveData
import timber.log.Timber

class AuthenticatorViewModel(
    private val mode: AuthenticatorMode,
    private val initialUsername: String
) : ViewModel() {

    val username = DistinctMutableLiveData(initialUsername)

    val usernameEditable = mode == AuthenticatorMode.ADD

    private val _usernameError = DistinctMutableLiveData<String?>(null)
    val usernameError: LiveData<String?> = _usernameError

    val password = DistinctMutableLiveData("")

    private val _passwordError = DistinctMutableLiveData<String?>(null)
    val passwordError: LiveData<String?> = _passwordError

    private val _authenticating = DistinctMutableLiveData(false)
    val authenticating: LiveData<Boolean> = _authenticating

    private val _signUpEvent = EventLiveData<Unit>()
    val signUpEvent: LiveData<Unit> = _signUpEvent

    private val _sendResultAndFinishEvent = EventLiveData<Intent>()
    val sendResultAndFinishEvent: LiveData<Intent> = _sendResultAndFinishEvent

    fun onUsernameChanged() {
        _usernameError.value = null
    }

    fun onPasswordChanged() {
        _passwordError.value = null
    }

    fun onSignIn() = viewModelScope.launch {
        if (_authenticating.value) {
            return@launch
        }

        val username = if (mode == AuthenticatorMode.ADD) username.value else initialUsername
        val password = password.value

        _usernameError.value = if (username.isEmpty()) {
            appContext.getString(R.string.authenticator_username_error_empty)
        } else {
            null
        }
        _passwordError.value = if (password.isEmpty()) {
            appContext.getString(R.string.authenticator_password_error_empty)
        } else {
            null
        }
        if (_usernameError.value != null || _passwordError.value != null) {
            return@launch
        }

        _authenticating.value = true
        try {
            val response = try {
                ApiService.authenticate(username, password)
            } catch (e: Exception) {
                Timber.e(e)
                _passwordError.value = ApiService.errorMessage(e)
                return@launch
            }

            val account = Account(username)
            when (mode) {
                AuthenticatorMode.ADD -> {
                    accountManager.addAccountExplicitly(account, password)
                    if (accountManager.ownAccounts.size == 1) {
                        accountManager.activeAccount = account
                    }
                }
                AuthenticatorMode.UPDATE, AuthenticatorMode.CONFIRM ->
                    accountManager.setPassword(account, password)
            }
            account.setAuthToken(response.accessToken)
            account.refreshToken = response.refreshToken
            account.userId = response.userId
            account.userName = response.userName
            _sendResultAndFinishEvent.value = when (mode) {
                AuthenticatorMode.ADD, AuthenticatorMode.UPDATE -> Intent().apply {
                    putExtra(AccountManager.KEY_ACCOUNT_NAME, account.name)
                    putExtra(AccountManager.KEY_ACCOUNT_TYPE, account.type)
                }
                AuthenticatorMode.CONFIRM -> Intent().apply {
                    putExtra(AccountManager.KEY_BOOLEAN_RESULT, true)
                }
            }
        } finally {
            _authenticating.value = false
        }
    }

    fun onSignUp() {
        _signUpEvent.value = Unit
    }
}
