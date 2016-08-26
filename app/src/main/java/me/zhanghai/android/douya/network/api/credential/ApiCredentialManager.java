/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.credential;

import android.content.Context;

import me.zhanghai.android.douya.settings.info.Settings;

class ApiCredentialManager {

    private ApiCredentialManager() {}

    public static String getApiKey(Context context) {
        return Settings.API_KEY.getValue(context);
    }

    public static String getApiSecret(Context context) {
        return Settings.API_SECRET.getValue(context);
    }

    public static void setApiCredential(String apiKey, String apiSecret, Context context) {

        Settings.API_KEY.putValue(apiKey, context);
        Settings.API_SECRET.putValue(apiSecret, context);

        ApiCredential.Frodo.KEY = apiKey;
        ApiCredential.Frodo.SECRET = apiSecret;
        ApiCredential.ApiV2.KEY = apiKey;
        ApiCredential.ApiV2.SECRET = apiSecret;
    }
}
