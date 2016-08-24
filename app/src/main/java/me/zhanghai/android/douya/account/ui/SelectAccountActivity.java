/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.account.ui;

import android.accounts.Account;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.account.util.AccountUtils;
import me.zhanghai.android.douya.ui.SimpleDialogFragment;

public class SelectAccountActivity extends AppCompatActivity
        implements SimpleDialogFragment.SimpleDialogListenerProvider {

    public static final String EXTRA_ON_SELECTED_INTENT = SelectAccountActivity.class.getName()
            + ".on_selected_intent";

    private SimpleDialogFragment.SimpleDialogListener mDialogListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Account[] accounts = AccountUtils.getAccounts(this);
        int numAccounts = accounts.length;
        String[] accountNames = new String[numAccounts];
        for (int i = 0; i < numAccounts; ++i) {
            accountNames[i] = accounts[i].name;
        }

        mDialogListener = new SimpleDialogFragment.SimpleDialogListener() {
            @Override
            public void onSingleChoiceItemClicked(int requestCode, int index) {
                AccountUtils.setActiveAccount(accounts[index], SelectAccountActivity.this);
                // Calling finish() before startActivity() makes it work when the Intent is a
                // launcher one.
                finish();
                Intent onSelectedIntent = getIntent().getParcelableExtra(EXTRA_ON_SELECTED_INTENT);
                startActivity(onSelectedIntent);
            }
            @Override
            public void onNegativeButtonClicked(int requestCode) {
                onCancel(requestCode);
            }
            @Override
            public void onCancel(int requestCode) {
                finish();
            }
        };

        if (savedInstanceState == null) {
            SimpleDialogFragment.makeSingleChoice(R.string.auth_select_account, accountNames, -1,
                    this)
                    .show(this);
        }
    }

    @Override
    public SimpleDialogFragment.SimpleDialogListener getDialogListener() {
        return mDialogListener;
    }
}
