/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.app

import android.accounts.AccountManager
import android.view.inputmethod.InputMethodManager
import me.zhanghai.android.douya.compat.getSystemServiceCompat

val accountManager: AccountManager by lazy {
    application.getSystemServiceCompat(AccountManager::class.java)
}

val inputMethodManager: InputMethodManager by lazy {
    application.getSystemServiceCompat(InputMethodManager::class.java)
}
