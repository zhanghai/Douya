/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.link;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import me.zhanghai.android.douya.account.util.AccountUtils;

public class UriHandlerActivity extends AppCompatActivity {

    public static Intent makeIntent(Uri uri, Context context) {
        return new Intent(context, UriHandlerActivity.class)
                .setData(uri);
    }

    public static Intent makeIntent(String url, Context context) {
        return makeIntent(Uri.parse(url), context);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Uri uri = getIntent().getData();
        if (uri == null) {
            finish();
            return;
        }

        if (!AccountUtils.ensureActiveAccountAvailability(this)) {
            finish();
            return;
        }

        UriHandler.open(uri, this);
        finish();
    }
}
