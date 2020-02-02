/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.account.app

import android.accounts.Account
import android.accounts.AccountManager
import android.accounts.AccountManagerFuture
import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import me.zhanghai.android.douya.account.info.AccountContract
import me.zhanghai.android.douya.account.ui.SelectAccountDialogFragment
import me.zhanghai.android.douya.setting.Settings
import me.zhanghai.android.douya.util.show
import timber.log.Timber

val AccountManager.ownAccounts: Array<Account>
    get() = getAccountsByType(AccountContract.ACCOUNT_TYPE)

fun AccountManager.getAccountByName(name: String): Account? = ownAccounts.find { it.name == name }

fun AccountManager.addAccountExplicitly(account: Account, password: String) =
    addAccountExplicitly(account, password, null)

fun AccountManager.addAccount(
    activity: Activity?,
    callback: ((AccountManagerFuture<Bundle>) -> Unit)?
): AccountManagerFuture<Bundle> = addAccount(
    AccountContract.ACCOUNT_TYPE, AccountContract.AUTH_TOKEN_TYPE, null, null, activity, callback,
    null
)

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

fun AccountManager.ensureActiveAccount(activity: FragmentActivity): Boolean {
    if (activeAccount != null) {
        return true
    }
    if (ownAccounts.isEmpty()) {
        addAccount(activity) { future ->
            try {
                future.result
            } catch (e: Exception) {
                Timber.e(e)
            }
            if (activeAccount == null) {
                activity.finish()
            }
        }
    } else {
        SelectAccountDialogFragment().show(activity)
    }
    return false
}
