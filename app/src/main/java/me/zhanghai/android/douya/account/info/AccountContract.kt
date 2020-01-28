/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.account.info

import me.zhanghai.android.douya.BuildConfig

object AccountContract {
    const val ACCOUNT_TYPE = BuildConfig.APPLICATION_ID

    const val AUTH_TOKEN_TYPE = "${BuildConfig.APPLICATION_ID}.auth_token.FRODO"

    const val USER_DATA_KEY_USER_ID = "${BuildConfig.APPLICATION_ID}.user_data.USER_ID"

    const val USER_DATA_KEY_USER_NAME = "${BuildConfig.APPLICATION_ID}.user_data.USER_NAME"

    const val USER_DATA_KEY_REFRESH_TOKEN = "${BuildConfig.APPLICATION_ID}.user_data.REFRESH_TOKEN"
}
