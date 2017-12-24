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
import android.accounts.OnAccountsUpdateListener;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import com.google.gson.JsonParseException;

import java.io.IOException;

import me.zhanghai.android.douya.DouyaApplication;
import me.zhanghai.android.douya.account.app.AccountPreferences;
import me.zhanghai.android.douya.account.info.AccountContract;
import me.zhanghai.android.douya.account.ui.AddAccountActivity;
import me.zhanghai.android.douya.account.ui.SelectAccountActivity;
import me.zhanghai.android.douya.network.api.ApiAuthenticators;
import me.zhanghai.android.douya.network.api.info.apiv2.User;
import me.zhanghai.android.douya.settings.info.Settings;
import me.zhanghai.android.douya.util.GsonHelper;

public class AccountUtils {

    public static AccountManager getAccountManager() {
        return AccountManager.get(DouyaApplication.getInstance());
    }

    public static AccountManagerFuture<Bundle> addAccount(Activity activity,
                                                          AccountManagerCallback<Bundle> callback,
                                                          Handler handler) {
        return getAccountManager().addAccount(AccountContract.ACCOUNT_TYPE,
                AccountContract.AUTH_TOKEN_TYPE_FRODO, null, null, activity, callback, handler);
    }

    public static AccountManagerFuture<Bundle> addAccount(Activity activity) {
        return addAccount(activity, null, null);
    }

    public static void addAccount(Activity activity, Intent onAddedIntent) {
        activity.startActivity(AddAccountActivity.makeIntent(onAddedIntent, activity));
    }

    public static boolean addAccountExplicitly(Account account, String password) {
        return getAccountManager().addAccountExplicitly(account, password, null);
    }

    public static AccountManagerFuture<Bundle> updatePassword(Activity activity, Account account,
                                   AccountManagerCallback<Bundle> callback, Handler handler) {
        return getAccountManager().updateCredentials(account, AccountContract.AUTH_TOKEN_TYPE_FRODO,
                null, activity, callback, handler);
    }

    public static AccountManagerFuture<Bundle> updatePassword(Activity activity, Account account) {
        return updatePassword(activity, account, null, null);
    }

    public static AccountManagerFuture<Bundle> confirmPassword(
            Activity activity, Account account, AccountManagerCallback<Bundle> callback,
            Handler handler) {
        return getAccountManager().confirmCredentials(account, null, activity, callback, handler);
    }

    public interface ConfirmPasswordListener {
        void onConfirmed();
        void onFailed();
    }

    private static AccountManagerCallback<Bundle> makeConfirmPasswordCallback(
            ConfirmPasswordListener listener) {
        return future -> {
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
        };
    }

    public static void confirmPassword(Activity activity, Account account,
                                       ConfirmPasswordListener listener, Handler handler) {
        confirmPassword(activity, account, makeConfirmPasswordCallback(listener), handler);
    }

    public static void confirmPassword(Activity activity, ConfirmPasswordListener listener) {
        confirmPassword(activity, getActiveAccount(), listener, null);
    }

    // REMOVEME: This seems infeasible. And we should check against local password instead of using
    // network
    public static Intent makeConfirmPasswordIntent(Account account,
                                                   ConfirmPasswordListener listener) {
        try {
            return confirmPassword(null, account, makeConfirmPasswordCallback(listener), null)
                    .getResult().getParcelable(AccountManager.KEY_INTENT);
        } catch (AuthenticatorException | IOException | OperationCanceledException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Intent makeConfirmPasswordIntent(ConfirmPasswordListener listener) {
        return makeConfirmPasswordIntent(getActiveAccount(), listener);
    }

    public static void addOnAccountListUpdatedListener(OnAccountsUpdateListener listener) {
        getAccountManager().addOnAccountsUpdatedListener(listener, null, false);
    }

    public static void removeOnAccountListUpdatedListener(OnAccountsUpdateListener listener) {
        getAccountManager().removeOnAccountsUpdatedListener(listener);
    }

    public static Account[] getAccounts() {
        return getAccountManager().getAccountsByType(AccountContract.ACCOUNT_TYPE);
    }

    private static Account getAccountByName(String accountName) {

        if (TextUtils.isEmpty(accountName)) {
            return null;
        }

        for (Account account : getAccounts()) {
            if (TextUtils.equals(account.name, accountName)) {
                return account;
            }
        }

        return null;
    }

    // NOTE: This method is asynchronous.
    public static void removeAccount(Account account) {
        //noinspection deprecation
        getAccountManager().removeAccount(account, null, null);
    }

    public static boolean hasAccount() {
        return getAccounts().length != 0;
    }

    // NOTE: Use getActiveAccount() instead for availability checking.
    private static String getActiveAccountName() {
        return Settings.ACTIVE_ACCOUNT_NAME.getValue();
    }

    private static void setActiveAccountName(String accountName) {
        Settings.ACTIVE_ACCOUNT_NAME.putValue(accountName);
    }

    private static void removeActiveAccountName() {
        Settings.ACTIVE_ACCOUNT_NAME.remove();
    }

    public static boolean hasActiveAccountName() {
        return !TextUtils.isEmpty(getActiveAccountName());
    }

    public static boolean isActiveAccountName(String accountName) {
        return TextUtils.equals(accountName, getActiveAccountName());
    }

    // NOTICE:
    // Will clear the invalid setting and return null if no matching account with the name from
    // setting is found.
    public static Account getActiveAccount() {
        Account account = getAccountByName(getActiveAccountName());
        if (account != null) {
            return account;
        } else {
            removeActiveAccountName();
            return null;
        }
    }

    public static void setActiveAccount(Account account) {

        if (account == null) {
            removeActiveAccountName();
            return;
        }

        Account oldActiveAccount = getActiveAccount();
        setActiveAccountName(account.name);
        if (oldActiveAccount != null) {
            if (TextUtils.equals(getRecentOneAccountName(), account.name)) {
                setRecentOneAccountName(oldActiveAccount.name);
            } else if (TextUtils.equals(getRecentTwoAccountName(), account.name)) {
                setRecentTwoAccountName(oldActiveAccount.name);
            } else {
                setRecentTwoAccountName(getRecentOneAccountName());
                setRecentOneAccountName(oldActiveAccount.name);
            }
        }

        ApiAuthenticators.getInstance().notifyActiveAccountChanged();
    }

    public static boolean hasActiveAccount() {
        return getActiveAccount() != null;
    }

    public static boolean isActiveAccount(Account account) {
        return isActiveAccountName(account.name);
    }

    private static String getRecentOneAccountName() {
        return Settings.RECENT_ONE_ACCOUNT_NAME.getValue();
    }

    private static void setRecentOneAccountName(String accountName) {
        Settings.RECENT_ONE_ACCOUNT_NAME.putValue(accountName);
    }

    private static void removeRecentOneAccountName() {
        Settings.RECENT_ONE_ACCOUNT_NAME.remove();
    }

    public static Account getRecentOneAccount() {

        Account activeAccount = getActiveAccount();
        if (activeAccount == null) {
            return null;
        }

        String accountName = getRecentOneAccountName();
        if (!TextUtils.isEmpty(accountName) && !TextUtils.equals(accountName, activeAccount.name)) {
            Account account = getAccountByName(accountName);
            if (account != null) {
                return account;
            }
        }

        for (Account account : getAccounts()) {
            if (!account.equals(activeAccount)) {
                setRecentOneAccountName(account.name);
                return account;
            }
        }

        removeRecentOneAccountName();
        return null;
    }

    private static String getRecentTwoAccountName() {
        return Settings.RECENT_TWO_ACCOUNT_NAME.getValue();
    }

    private static void setRecentTwoAccountName(String accountName) {
        Settings.RECENT_TWO_ACCOUNT_NAME.putValue(accountName);
    }

    private static void removeRecentTwoAccountName() {
        Settings.RECENT_TWO_ACCOUNT_NAME.remove();
    }

    public static Account getRecentTwoAccount() {

        Account activeAccount = getActiveAccount();
        if (activeAccount == null) {
            return null;
        }
        Account recentOneAccount = getRecentOneAccount();
        if (recentOneAccount == null) {
            return null;
        }

        String accountName = getRecentTwoAccountName();
        if (!TextUtils.isEmpty(accountName) && !TextUtils.equals(accountName, activeAccount.name)
                && !TextUtils.equals(accountName, recentOneAccount.name)) {
            Account account = getAccountByName(accountName);
            if (account != null) {
                return account;
            }
        }

        for (Account account : getAccounts()) {
            if (!account.equals(activeAccount) && !account.equals(recentOneAccount)) {
                setRecentTwoAccountName(account.name);
                return account;
            }
        }

        removeRecentTwoAccountName();
        return null;
    }

    // NOTICE: Be sure to check hasAccount() before calling this.
    // NOTE:
    // Only intended for selecting an active account when there is none, changing active
    // account should be handled in settings.
    public static void selectAccount(Activity activity, Intent onSelectedIntent) {

        if (getAccounts().length == 0) {
            throw new IllegalStateException("Should have checked for hasAccount()");
        }

        activity.startActivity(SelectAccountActivity.makeIntent(onSelectedIntent, activity));
    }

    public static boolean ensureActiveAccountAvailability(Activity activity) {
        boolean accountAvailable = true;
        if (!hasAccount()) {
            accountAvailable = false;
            addAccount(activity, activity.getIntent());
        } else if (!hasActiveAccount()) {
            accountAvailable = false;
            selectAccount(activity, activity.getIntent());
        }
        if (!accountAvailable) {
            activity.finish();
        }
        return accountAvailable;
    }

    public static String getPassword(Account account) {
        return getAccountManager().getPassword(account);
    }

    public static void setPassword(Account account, String password) {
        getAccountManager().setPassword(account, password);
    }

    public static String peekAuthToken(Account account, String type) {
        return getAccountManager().peekAuthToken(account, type);
    }

    public static void getAuthToken(Account account, String type,
                                    AccountManagerCallback<Bundle> callback, Handler handler) {
        getAccountManager().getAuthToken(account, type, null, true, callback, handler);
    }

    public interface GetAuthTokenListener {
        void onResult(String authToken);
        void onFailed();
    }

    private static AccountManagerCallback<Bundle> makeGetAuthTokenCallback(
            GetAuthTokenListener listener) {
        return future -> {
            try {
                String authToken = future.getResult()
                        .getString(AccountManager.KEY_AUTHTOKEN);
                if (!TextUtils.isEmpty(authToken)) {
                    listener.onResult(authToken);
                } else {
                    listener.onFailed();
                }
            } catch (AuthenticatorException | IOException | OperationCanceledException e) {
                e.printStackTrace();
                listener.onFailed();
            }
        };
    }

    public static void getAuthToken(Account account, String type, GetAuthTokenListener listener,
                                    Handler handler) {
        getAuthToken(account, type, makeGetAuthTokenCallback(listener), handler);
    }

    public static void getAuthToken(Account account, String type, GetAuthTokenListener listener) {
        getAuthToken(account, type, listener, null);
    }

    public static void setAuthToken(Account account, String type, String authToken) {
        getAccountManager().setAuthToken(account, type, authToken);
    }

    public static void invalidateAuthToken(String authToken) {
        getAccountManager().invalidateAuthToken(AccountContract.ACCOUNT_TYPE, authToken);
    }

    // User name is different from username: user name is the display name in User.name, but
    // username is the account name for logging in.
    public static String getUserName(Account account) {
        return AccountPreferences.forAccount(account).getString(AccountContract.KEY_USER_NAME,
                null);
    }

    public static String getUserName() {
        return getUserName(getActiveAccount());
    }

    public static void setUserName(Account account, String userName) {
        AccountPreferences.forAccount(account).putString(AccountContract.KEY_USER_NAME, userName);
    }

    public static long getUserId(Account account) {
        return AccountPreferences.forAccount(account).getLong(AccountContract.KEY_USER_ID,
                AccountContract.INVALID_USER_ID);
    }

    public static long getUserId() {
        return getUserId(getActiveAccount());
    }

    public static void setUserId(Account account, long userId) {
        AccountPreferences.forAccount(account).putLong(AccountContract.KEY_USER_ID, userId);
    }

    public static String getRefreshToken(Account account, String authTokenType) {
        return AccountPreferences.forAccount(account).getString(getRefreshTokenKey(authTokenType),
                null);
    }

    public static void setRefreshToken(Account account, String authTokenType, String refreshToken) {
        AccountPreferences.forAccount(account).putString(getRefreshTokenKey(authTokenType),
                refreshToken);
    }

    private static String getRefreshTokenKey(String authTokenType) {
        switch (authTokenType) {
            case AccountContract.AUTH_TOKEN_TYPE_API_V2:
                return AccountContract.KEY_REFRESH_TOKEN_API_V2;
            case AccountContract.AUTH_TOKEN_TYPE_FRODO:
                return AccountContract.KEY_REFRESH_TOKEN_FRODO;
            default:
                throw new IllegalArgumentException("Unknown authTokenType: " + authTokenType);
        }
    }

    public static User getUser(Account account) {
        String userInfoJson = AccountPreferences.forAccount(account).getString(
                AccountContract.KEY_USER_INFO, null);
        if (!TextUtils.isEmpty(userInfoJson)) {
            try {
                return GsonHelper.GSON.fromJson(userInfoJson, User.class);
            } catch (JsonParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void setUser(Account account, User user) {
        String userInfoJson = GsonHelper.GSON.toJson(user, User.class);
        AccountPreferences.forAccount(account).putString(AccountContract.KEY_USER_INFO,
                userInfoJson);
    }

    public static User getUser() {
        return getUser(getActiveAccount());
    }

    public static void setUser(User user) {
        setUser(getActiveAccount(), user);
    }
}
