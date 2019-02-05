/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.account.ui;

import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import me.zhanghai.android.douya.account.util.AccountUtils;

public class AddAccountActivity extends AppCompatActivity {

    private static final String KEY_PREFIX = AddAccountActivity.class.getName() + '.';

    private static final String EXTRA_ON_ADDED_INTENT = KEY_PREFIX + "on_added_intent";

    public static Intent makeIntent(Intent onAddedIntent, Context context) {
        return new Intent(context, AddAccountActivity.class)
                .putExtra(EXTRA_ON_ADDED_INTENT, onAddedIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            AccountUtils.addAccount(this, new AccountManagerCallback<Bundle>() {
                @Override
                public void run(AccountManagerFuture<Bundle> future) {
                    finish();
                    try {
                        Bundle result = future.getResult();
                        if (result.containsKey(AccountManager.KEY_ACCOUNT_NAME)
                                && result.containsKey(AccountManager.KEY_ACCOUNT_TYPE)) {
                            // NOTE:
                            // Active account should have been set in
                            // AuthenticatorActivity.onAuthResult() since the mode should be
                            // AUTH_MODE_NEW.
                            Intent onAddedIntent = getIntent()
                                    .getParcelableExtra(EXTRA_ON_ADDED_INTENT);
                            startActivity(onAddedIntent);
                        }
                    } catch (AuthenticatorException | IOException | OperationCanceledException e) {
                        e.printStackTrace();
                    }
                }
            }, null);
        }
    }
}
