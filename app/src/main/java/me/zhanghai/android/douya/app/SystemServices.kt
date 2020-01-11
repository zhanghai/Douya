/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.app

import android.accounts.AccountManager
import androidx.core.content.ContextCompat

val accountManager: AccountManager by lazy {
    ContextCompat.getSystemService(appContext, AccountManager::class.java)!!
}
