/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.credential;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import me.zhanghai.android.douya.util.LogUtils;

public class SetApiCredentialReceiver extends BroadcastReceiver {

    private static final String EXTRA_API_V2_API_KEY =
            "me.zhanghai.android.douya.intent.extra.API_V2_API_KEY";
    private static final String EXTRA_API_V2_API_SECRET =
            "me.zhanghai.android.douya.intent.extra.API_V2_API_SECRET";
    private static final String EXTRA_FRODO_API_KEY =
            "me.zhanghai.android.douya.intent.extra.FRODO_API_KEY";
    private static final String EXTRA_FRODO_API_SECRET =
            "me.zhanghai.android.douya.intent.extra.FRODO_API_SECRET";

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.i("Received intent: " + intent);
        String apiV2ApiKey = intent.getStringExtra(EXTRA_API_V2_API_KEY);
        String apiV2ApiSecret = intent.getStringExtra(EXTRA_API_V2_API_SECRET);
        String frodoApiKey = intent.getStringExtra(EXTRA_FRODO_API_KEY);
        String frodoApiSecret = intent.getStringExtra(EXTRA_FRODO_API_SECRET);
        ApiCredentialManager.setApiCredential(apiV2ApiKey, apiV2ApiSecret, frodoApiKey,
                frodoApiSecret);
    }
}
