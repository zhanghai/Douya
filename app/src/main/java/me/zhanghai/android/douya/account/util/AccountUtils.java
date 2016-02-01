/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.account.util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import java.io.IOException;

import me.zhanghai.android.douya.account.app.AccountPreferences;
import me.zhanghai.android.douya.account.info.AccountContract;
import me.zhanghai.android.douya.account.ui.AddAccountActivity;
import me.zhanghai.android.douya.account.ui.SelectAccountActivity;
import me.zhanghai.android.douya.network.Volley;
import me.zhanghai.android.douya.settings.info.Settings;

public class AccountUtils {

    public static AccountManagerFuture<Bundle> addAccount(Activity activity,
                                                          AccountManagerCallback<Bundle> callback,
                                                          Handler handler) {
        return AccountManager.get(activity).addAccount(AccountContract.ACCOUNT_TYPE,
                AccountContract.AUTH_TOKEN_TYPE, null, null, activity, callback, handler);
    }

    public static AccountManagerFuture<Bundle> addAccount(Activity activity) {
        return addAccount(activity, null, null);
    }

    public static void addAccount(Activity activity, Intent onAddedIntent) {
        Intent intent = new Intent(activity, AddAccountActivity.class);
        intent.putExtra(AddAccountActivity.EXTRA_ON_ADDED_INTENT, onAddedIntent);
        activity.startActivity(intent);
    }

    public static boolean addAccountExplicitly(Account account, String password, Context context) {
        return AccountManager.get(context).addAccountExplicitly(account, password, null);
    }

    public static AccountManagerFuture<Bundle> updatePassword(Activity activity, Account account,
                                   AccountManagerCallback<Bundle> callback, Handler handler) {
        return AccountManager.get(activity).updateCredentials(account,
                AccountContract.AUTH_TOKEN_TYPE, null, activity, callback, handler);
    }

    public static AccountManagerFuture<Bundle> updatePassword(Activity activity, Account account) {
        return updatePassword(activity, account, null, null);
    }

    public static AccountManagerFuture<Bundle> confirmPassword(Activity activity, Account account,
                                                               AccountManagerCallback<Bundle> callback,
                                                               Handler handler) {
        return AccountManager.get(activity).confirmCredentials(account, null, activity, callback,
                handler);
    }

    public interface ConfirmPasswordListener {
        void onConfirmed();
        void onFailed();
    }

    private static AccountManagerCallback<Bundle> makeAccountManagerCallback(
            final ConfirmPasswordListener listener) {
        return new AccountManagerCallback<Bundle>() {
            @Override
            public void run(AccountManagerFuture<Bundle> future) {
                try {
                    boolean confirmed = future.getResult()
                            .getBoolean(AccountManager.KEY_BOOLEAN_RESULT);
                    if (confirmed) {
                        listener.onConfirmed();
                    } else {
                        listener.onFailed();
                    }
                } catch (AuthenticatorException | IOException | OperationCanceledException e) {
                    e.printStackTrace();
                    listener.onFailed();
                }
            }
        };
    }

    public static void confirmPassword(Activity activity, Account account,
                                       final ConfirmPasswordListener listener, Handler handler) {
        confirmPassword(activity, account, makeAccountManagerCallback(listener), handler);
    }

    public static void confirmPassword(Activity activity, final ConfirmPasswordListener listener) {
        confirmPassword(activity, getActiveAccount(activity), listener, null);
    }

    // REMOVEME: This seems infeasible. And we should check against local password instead of using
    // network
    public static Intent makeConfirmPasswordIntent(Account account,
                                                   final ConfirmPasswordListener listener) {
        try {
            return confirmPassword(null, account, makeAccountManagerCallback(listener), null)
                    .getResult().getParcelable(AccountManager.KEY_INTENT);
        } catch (AuthenticatorException | IOException | OperationCanceledException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Intent makeConfirmPasswordIntent(final ConfirmPasswordListener listener,
                                                   Context context) {
        return makeConfirmPasswordIntent(getActiveAccount(context), listener);
    }

    public static Account[] getAccounts(Context context) {
        return AccountManager.get(context).getAccountsByType(AccountContract.ACCOUNT_TYPE);
    }

    // NOTE: This method is asynchronous.
    public static void removeAccount(Account account, Context context) {
        AccountManager.get(context).removeAccount(account, null, null);
    }

    public static boolean hasAccount(Context context) {
        return getAccounts(context).length != 0;
    }

    // NOTE: Use getActiveAccount().name instead for availability checking.
    private static String getActiveAccountName(Context context) {
        return Settings.ACTIVE_ACCOUNT_NAME.getValue(context);
    }

    private static void setActiveAccountName(String accountName, Context context) {
        Settings.ACTIVE_ACCOUNT_NAME.putValue(accountName, context);
    }

    private static void removeActiveAccountName(Context context) {
        Settings.ACTIVE_ACCOUNT_NAME.remove(context);
    }

    public static boolean hasActiveAccountName(Context context) {
        return !TextUtils.isEmpty(getActiveAccountName(context));
    }

    public static boolean isActiveAccountName(String accountName, Context context) {
        return TextUtils.equals(accountName, getActiveAccountName(context));
    }

    // NOTICE:
    // Will clear the invalid setting and return null if no matching account with the name from
    // setting is found.
    public static Account getActiveAccount(Context context) {

        String activeAccountName = getActiveAccountName(context);
        if (TextUtils.isEmpty(activeAccountName)) {
            return null;
        }

        for (Account account : getAccounts(context)) {
            if (TextUtils.equals(account.name, activeAccountName)) {
                return account;
            }
        }

        removeActiveAccountName(context);
        return null;
    }

    public static void setActiveAccount(Account account, Context context) {
        setActiveAccountName(account.name, context);
        Volley volley = Volley.peekInstance();
        if (volley != null) {
            volley.notifyActiveAccountChanged(context);
        }
    }

    public static boolean hasActiveAccount(Context context) {
        return getActiveAccount(context) != null;
    }

    public static boolean isActiveAccount(Account account, Context context) {
        return isActiveAccountName(account.name, context);
    }

    // NOTICE: Be sure to check hasAccount() before calling this.
    // NOTE:
    // Only intended for selecting an active account when there is none, changing active
    // account should be handled in settings.
    public static void selectAccount(Activity activity, Intent onSelectedIntent) {

        if (getAccounts(activity).length == 0) {
            throw new IllegalStateException("Should have checked for hasAccount()");
        }

        Intent intent = new Intent(activity, SelectAccountActivity.class);
        intent.putExtra(SelectAccountActivity.EXTRA_ON_SELECTED_INTENT, onSelectedIntent);
        activity.startActivity(intent);
    }

    public static boolean ensureAccountAvailability(Activity activity) {
        boolean accountAvailable = true;
        if (!hasAccount(activity)) {
            accountAvailable = false;
            addAccount(activity, activity.getIntent());
        } else if (!hasActiveAccount(activity)) {
            accountAvailable = false;
            selectAccount(activity, activity.getIntent());
        }
        if (!accountAvailable) {
            activity.finish();
        }
        return accountAvailable;
    }

    public static String getPassword(Account account, Context context) {
        return AccountManager.get(context).getPassword(account);
    }

    public static void setPassword(Account account, String password, Context context) {
        AccountManager.get(context).setPassword(account, password);
    }

    public static String getAuthToken(Account account, Context context)
            throws AuthenticatorException, OperationCanceledException, IOException {
        return AccountManager.get(context).blockingGetAuthToken(account,
                AccountContract.AUTH_TOKEN_TYPE, true);
    }

    public static void setAuthToken(Account account, String authToken, Context context) {
        AccountManager.get(context).setAuthToken(account, AccountContract.AUTH_TOKEN_TYPE,
                authToken);
    }

    public static void invalidateAuthToken(String authToken, Context context) {
        AccountManager.get(context).invalidateAuthToken(AccountContract.ACCOUNT_TYPE, authToken);
    }

    // User name is different from username: user name is the display name in User.name, but
    // username is the account name for logging in.
    public static String getUserName(Account account, Context context) {
        return AccountPreferences.from(account, context).getString(AccountContract.KEY_USER_NAME,
                null);
    }

    public static String getUserName(Context context) {
        return getUserName(getActiveAccount(context), context);
    }

    public static void setUserName(Account account, String userName, Context context) {
        AccountPreferences.from(account, context).putString(AccountContract.KEY_USER_NAME,
                userName);
    }

    public static long getUserId(Account account, Context context) {
        return AccountPreferences.from(account, context).getLong(AccountContract.KEY_USER_ID,
                AccountContract.INVALID_USER_ID);
    }

    public static long getUserId(Context context) {
        return getUserId(getActiveAccount(context), context);
    }

    public static void setUserId(Account account, long userId, Context context) {
        AccountPreferences.from(account, context).putLong(AccountContract.KEY_USER_ID, userId);
    }

    public static String getRefreshToken(Account account, Context context) {
        return AccountPreferences.from(account, context).getString(
                AccountContract.KEY_REFRESH_TOKEN, null);
    }

    public static void setRefreshToken(Account account, String refreshToken, Context context) {
        AccountPreferences.from(account, context).putString(AccountContract.KEY_REFRESH_TOKEN,
                refreshToken);
    }
}
