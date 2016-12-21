/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class AccountManagerCompat {

    private AccountManagerCompat() {}

    public static Intent newChooseAccountIntent(Account selectedAccount,
                                                List<Account> allowableAccounts,
                                                String[] allowableAccountTypes,
                                                String descriptionOverrideText,
                                                String addAccountAuthTokenType,
                                                String[] addAccountRequiredFeatures,
                                                Bundle addAccountOptions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return AccountManager.newChooseAccountIntent(selectedAccount, allowableAccounts,
                    allowableAccountTypes, descriptionOverrideText, addAccountAuthTokenType,
                    addAccountRequiredFeatures, addAccountOptions);
        } else {
            ArrayList<Account> allowableAccountsArrayList;
            if (allowableAccounts instanceof ArrayList) {
                allowableAccountsArrayList = (ArrayList<Account>) allowableAccounts;
            } else {
                allowableAccountsArrayList = new ArrayList<>();
                allowableAccountsArrayList.addAll(allowableAccounts);
            }
            //noinspection deprecation
            return AccountManager.newChooseAccountIntent(selectedAccount,
                    allowableAccountsArrayList, allowableAccountTypes, false,
                    descriptionOverrideText, addAccountAuthTokenType, addAccountRequiredFeatures,
                    addAccountOptions);
        }
    }
}
