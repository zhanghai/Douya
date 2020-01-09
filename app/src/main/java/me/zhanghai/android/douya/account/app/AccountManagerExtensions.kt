/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.account.app

import android.accounts.Account
import android.accounts.AccountManager
import me.zhanghai.android.douya.account.info.AccountContract
import me.zhanghai.android.douya.app.accountManager
import me.zhanghai.android.douya.setting.Settings

val AccountManager.ownAccounts get() = getAccountsByType(AccountContract.ACCOUNT_TYPE)

fun AccountManager.getAccountByName(name: String) = ownAccounts.find { it.name == name }

fun AccountManager.addAccountExplicitly(account: Account, password: String) =
    accountManager.addAccountExplicitly(account, password, null)

var AccountManager.activeAccount: Account?
    get() {
        val accountName = Settings.ACTIVE_ACCOUNT_NAME.value ?: return null
        val account = getAccountByName(accountName)
        if (account == null) {
            Settings.ACTIVE_ACCOUNT_NAME.putValue(null)
        }
        return account
    }
    set(value) {
        Settings.ACTIVE_ACCOUNT_NAME.putValue(value?.name)
    }
