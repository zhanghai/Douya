/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.account.app

import android.accounts.Account
import android.accounts.AuthenticatorException
import android.accounts.OperationCanceledException
import me.zhanghai.android.douya.account.info.AccountContract
import java.io.IOException

fun Account(name: String) = Account(name, AccountContract.ACCOUNT_TYPE)

var Account.password: String?
    get() = accountManager.getPassword(this)
    set(value) = accountManager.setPassword(this, value)

fun Account.peekAuthToken(): String? =
    accountManager.peekAuthToken(this, AccountContract.AUTH_TOKEN_TYPE)

@Throws(AuthenticatorException::class, IOException::class, OperationCanceledException::class)
fun Account.blockingGetAuthToken(notifyAuthFailure: Boolean): String? =
    accountManager.blockingGetAuthToken(this, AccountContract.AUTH_TOKEN_TYPE, notifyAuthFailure)

fun Account.setAuthToken(authToken: String) =
    accountManager.setAuthToken(this, AccountContract.AUTH_TOKEN_TYPE, authToken)

fun Account.invalidateAuthToken(authToken: String) =
    accountManager.invalidateAuthToken(type, authToken)

var Account.refreshToken: String?
    get() = accountManager.getUserData(this, AccountContract.USER_DATA_KEY_REFRESH_TOKEN)
    set(value) =
        accountManager.setUserData(this, AccountContract.USER_DATA_KEY_REFRESH_TOKEN, value)

var Account.userId: Long?
    get() = accountManager.getUserData(this, AccountContract.USER_DATA_KEY_USER_ID)?.toLongOrNull()
    set(value) =
        accountManager.setUserData(this, AccountContract.USER_DATA_KEY_USER_ID, value?.toString())

var Account.userName: String?
    get() = accountManager.getUserData(this, AccountContract.USER_DATA_KEY_USER_NAME)
    set(value) = accountManager.setUserData(this, AccountContract.USER_DATA_KEY_USER_NAME, value)
