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
import me.zhanghai.android.douya.account.info.AuthenticationMode
import me.zhanghai.android.douya.api.app.ApiService
import me.zhanghai.android.douya.app.accountManager
import me.zhanghai.android.douya.app.appContext
import me.zhanghai.android.douya.arch.EventLiveData
import me.zhanghai.android.douya.arch.MutableLiveData
import timber.log.Timber

class AuthenticationViewModel(
    private val mode: AuthenticationMode,
    private val initialUsername: String
) : ViewModel() {

    val username = MutableLiveData(initialUsername)

    private val _usernameError = MutableLiveData<String?>(null)
    val usernameError: LiveData<String?> = _usernameError

    val password = MutableLiveData("")

    private val _passwordError = MutableLiveData<String?>(null)
    val passwordError: LiveData<String?> = _passwordError

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _sendResultAndFinish = EventLiveData<Intent>()
    val sendResultAndFinish: LiveData<Intent> = _sendResultAndFinish

    fun onUsernameChanged() {
        _usernameError.value = null
    }

    fun onPasswordChanged() {
        _passwordError.value = null
    }

    fun onSignIn() = viewModelScope.launch {
        if (_loading.value) {
            return@launch
        }

        val username = if (mode == AuthenticationMode.ADD) username.value else initialUsername
        val password = password.value

        _usernameError.value = if (username.isEmpty()) {
            appContext.getString(R.string.authentication_username_error_empty)
        } else {
            null
        }
        _passwordError.value = if (password.isEmpty()) {
            appContext.getString(R.string.authentication_password_error_empty)
        } else {
            null
        }
        if (_usernameError.value != null || _passwordError.value != null) {
            return@launch
        }

        _loading.value = true
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
                AuthenticationMode.ADD -> {
                    accountManager.addAccountExplicitly(account, password)
                    if (accountManager.ownAccounts.size == 1) {
                        accountManager.activeAccount = account
                    }
                }
                AuthenticationMode.UPDATE, AuthenticationMode.CONFIRM ->
                    accountManager.setPassword(account, password)
            }
            account.setAuthToken(response.accessToken)
            account.refreshToken = response.refreshToken
            account.userId = response.userId
            account.userName = response.userName
            _sendResultAndFinish.value = when (mode) {
                AuthenticationMode.ADD, AuthenticationMode.UPDATE -> Intent().apply {
                    putExtra(AccountManager.KEY_ACCOUNT_NAME, account.name)
                    putExtra(AccountManager.KEY_ACCOUNT_TYPE, account.type)
                }
                AuthenticationMode.CONFIRM -> Intent().apply {
                    putExtra(AccountManager.KEY_BOOLEAN_RESULT, true)
                }
            }
        } finally {
            _loading.value = false
        }
    }

    fun onSignUp() = Unit
}
