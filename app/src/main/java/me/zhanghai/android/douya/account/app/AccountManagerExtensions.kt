/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.account.app

import android.accounts.Account
import android.accounts.AccountManager
import me.zhanghai.android.douya.account.info.AccountContract

fun AccountManager.getRefreshToken(account: Account): String? =
    getUserData(account, AccountContract.USER_DATA_KEY_REFRESH_TOKEN)

fun AccountManager.setRefreshToken(account: Account, refreshToken: String?) =
    setUserData(account, AccountContract.USER_DATA_KEY_REFRESH_TOKEN, refreshToken)

fun AccountManager.getUserId(account: Account): Long? =
    getUserData(account, AccountContract.USER_DATA_KEY_USER_ID)?.toLongOrNull()

fun AccountManager.setUserId(account: Account, userId: Long?) =
    setUserData(account, AccountContract.USER_DATA_KEY_USER_ID, userId?.toString())

fun AccountManager.getUserName(account: Account): String? =
    getUserData(account, AccountContract.USER_DATA_KEY_USER_NAME)

fun AccountManager.setUserName(account: Account, userName: String?) =
    setUserData(account, AccountContract.USER_DATA_KEY_USER_NAME, userName)
