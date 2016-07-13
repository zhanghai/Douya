/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.credential;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SetApiCredentialReceiver extends BroadcastReceiver {

    private static final String EXTRA_API_KEY = "me.zhanghai.android.douya.intent.extra.API_KEY";
    private static final String EXTRA_API_SECRET =
            "me.zhanghai.android.douya.intent.extra.API_SECRET";

    @Override
    public void onReceive(Context context, Intent intent) {
        String apiKey = intent.getStringExtra(EXTRA_API_KEY);
        String apiSecret = intent.getStringExtra(EXTRA_API_SECRET);
        ApiCredentialManager.setApiCredential(apiKey, apiSecret, context);
    }
}
