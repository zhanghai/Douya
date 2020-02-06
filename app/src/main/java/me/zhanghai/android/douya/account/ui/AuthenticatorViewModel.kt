/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.account.ui

import android.accounts.AccountManager
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
import me.zhanghai.android.douya.api.app.apiMessage
import me.zhanghai.android.douya.app.accountManager
import me.zhanghai.android.douya.app.application
import me.zhanghai.android.douya.arch.DistinctMutableLiveData
import me.zhanghai.android.douya.arch.EventLiveData
import me.zhanghai.android.douya.arch.mapDistinct
import me.zhanghai.android.douya.arch.valueCompat
import timber.log.Timber

class AuthenticatorViewModel(
    private val mode: AuthenticatorMode,
    private val initialUsername: String
) : ViewModel() {
    data class State(
        val usernameEditable: Boolean,
        val usernameError: String,
        val passwordError: String,
        val authenticating: Boolean
    )

    private val state = MutableLiveData(
        State(
            usernameEditable = mode == AuthenticatorMode.ADD,
            usernameError = "",
            passwordError = "",
            authenticating = false
        )
    )
    val usernameEditable = state.mapDistinct { it.usernameEditable }
    val usernameError = state.mapDistinct { it.usernameError }
    val passwordError = state.mapDistinct { it.passwordError }
    val authenticating = state.mapDistinct { it.authenticating }

    val username = DistinctMutableLiveData(initialUsername)
    val password = DistinctMutableLiveData("")

    private val _signUpEvent = EventLiveData<Unit>()
    val signUpEvent: LiveData<Unit> = _signUpEvent

    private val _sendResultAndFinishEvent = EventLiveData<Intent>()
    val sendResultAndFinishEvent: LiveData<Intent> = _sendResultAndFinishEvent

    fun onUsernameChanged() {
        state.value = state.valueCompat.copy(
            usernameError = ""
        )
    }

    fun onPasswordChanged() {
        state.value = state.valueCompat.copy(
            passwordError = ""
        )
    }

    fun onSignIn() {
        viewModelScope.launch {
            if (authenticating.valueCompat) {
                return@launch
            }

            val username = if (mode == AuthenticatorMode.ADD) username.value else initialUsername
            val password = password.value

            state.value = state.valueCompat.copy(
                usernameError = if (username.isEmpty()) {
                    application.getString(R.string.authenticator_username_error_empty)
                } else {
                    ""
                },
                passwordError = if (password.isEmpty()) {
                    application.getString(R.string.authenticator_password_error_empty)
                } else {
                    ""
                }
            )
            if (usernameError.valueCompat.isNotEmpty() || passwordError.valueCompat.isNotEmpty()) {
                return@launch
            }

            state.value = state.valueCompat.copy(
                authenticating = true
            )
            try {
                val response = try {
                    ApiService.authenticate(username, password)
                } catch (e: Exception) {
                    Timber.e(e)
                    state.value = state.valueCompat.copy(
                        passwordError = e.apiMessage
                    )
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
                _sendResultAndFinishEvent.value = Intent().apply {
                    when (mode) {
                        AuthenticatorMode.ADD, AuthenticatorMode.UPDATE -> {
                            putExtra(AccountManager.KEY_ACCOUNT_NAME, account.name)
                            putExtra(AccountManager.KEY_ACCOUNT_TYPE, account.type)
                        }
                        AuthenticatorMode.CONFIRM -> {
                            putExtra(AccountManager.KEY_BOOLEAN_RESULT, true)
                        }
                    }
                }
            } finally {
                state.value = state.valueCompat.copy(
                    authenticating = false
                )
            }
        }
    }

    fun onSignUp() {
        _signUpEvent.value = Unit
    }
}
