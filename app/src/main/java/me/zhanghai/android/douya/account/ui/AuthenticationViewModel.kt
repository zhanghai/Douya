/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.account.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AuthenticationViewModel : ViewModel() {

    val username = MutableLiveData<String>()

    val usernameError = MutableLiveData<String>()

    val password = MutableLiveData<String>()

    val passwordError = MutableLiveData<String>()

    fun onUsernameChanged() {
        usernameError.value = null
    }

    fun onPasswordChanged() {
        passwordError.value = null
    }

    fun onSignIn() {
        // TODO
    }

    fun onSignUp() {
        // TODO
    }
}
