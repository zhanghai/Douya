/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.account.ui;

import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import me.zhanghai.android.douya.util.FragmentUtils;

public class AuthenticatorActivity extends AppCompatAccountAuthenticatorActivity {

    private static final String KEY_PREFIX = AuthenticatorActivity.class.getName() + '.';

    // NOTE: EXTRA_AUTH_MODE and must be supplied.
    private static final String EXTRA_AUTH_MODE = KEY_PREFIX + "auth_mode";
    private static final String EXTRA_USERNAME = KEY_PREFIX + "username";

    public static final String AUTH_MODE_NEW = "new";
    public static final String AUTH_MODE_ADD = "add";
    public static final String AUTH_MODE_UPDATE = "update";
    public static final String AUTH_MODE_CONFIRM = "confirm";

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
            AUTH_MODE_NEW,
            AUTH_MODE_ADD,
            AUTH_MODE_CONFIRM,
            AUTH_MODE_UPDATE
    })
    @interface AuthMode {}

    public static Intent makeIntent(AccountAuthenticatorResponse response,
                                    @AuthMode String authMode, Context context) {
        return new Intent(context, AuthenticatorActivity.class)
                .putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)
                .putExtra(EXTRA_AUTH_MODE, authMode);
    }

    public static Intent makeIntent(AccountAuthenticatorResponse response,
                                    @AuthMode String authMode, String username, Context context) {
        return makeIntent(response, authMode, context)
                .putExtra(EXTRA_USERNAME, username);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Calls ensureSubDecor().
        findViewById(android.R.id.content);

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            @AuthMode
            String authMode = intent.getStringExtra(EXTRA_AUTH_MODE);
            String username = intent.getStringExtra(EXTRA_USERNAME);
            FragmentUtils.add(AuthenticatorFragment.newInstance(authMode, username), this,
                    android.R.id.content);
        }
    }
}
