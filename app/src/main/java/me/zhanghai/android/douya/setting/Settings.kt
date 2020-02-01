/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.setting

import me.zhanghai.android.douya.R

object Settings {
    val ACTIVE_ACCOUNT_NAME = NullableStringSettingLiveData(R.string.pref_key_active_account_name)

    val SHOW_LONG_URL = BooleanSettingLiveData(
        R.string.pref_key_show_long_url, R.bool.pref_default_value_show_long_url
    )
}
