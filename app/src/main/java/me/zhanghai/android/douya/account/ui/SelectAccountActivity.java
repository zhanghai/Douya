/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.account.ui;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import me.zhanghai.android.douya.account.util.AccountUtils;

public class SelectAccountActivity extends AppCompatActivity {

    private static final String KEY_PREFIX = SelectAccountActivity.class.getName() + '.';

    private static final String EXTRA_ON_SELECTED_INTENT = KEY_PREFIX + "on_selected_intent";

    private static final int REQUEST_CODE_CHOOSE_ACCOUNT = 11;

    public static Intent makeIntent(Intent onSelectedIntent, Context context) {
        return new Intent(context, SelectAccountActivity.class)
                .putExtra(EXTRA_ON_SELECTED_INTENT, onSelectedIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Calls ensureSubDecor().
        findViewById(android.R.id.content);

        if (savedInstanceState == null) {
            startActivityForResult(AccountUtils.newChooseAccountIntent(),
                    REQUEST_CODE_CHOOSE_ACCOUNT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_CHOOSE_ACCOUNT:
                if (resultCode == RESULT_OK) {
                    Account account = new Account(
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME),
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));
                    AccountUtils.setActiveAccount(account);
                    // Calling finish() before startActivity() makes it work when the Intent is a
                    // launcher one.
                    finish();
                    Intent onSelectedIntent = getIntent()
                            .getParcelableExtra(EXTRA_ON_SELECTED_INTENT);
                    startActivity(onSelectedIntent);
                } else {
                    finish();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
