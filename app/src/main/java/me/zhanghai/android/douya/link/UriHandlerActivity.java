/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.link;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import me.zhanghai.android.douya.account.util.AccountUtils;

public class UriHandlerActivity extends AppCompatActivity {

    public static Intent makeIntent(Context context) {
        return new Intent(context, UriHandlerActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Uri uri = getIntent().getData();
        if (uri == null) {
            finish();
        }

        if (!AccountUtils.ensureActiveAccountAvailability(this)) {
            finish();
        }

        UriHandler.open(uri, this);
        finish();
    }
}
