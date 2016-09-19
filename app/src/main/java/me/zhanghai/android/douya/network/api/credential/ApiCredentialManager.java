/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.credential;

import me.zhanghai.android.douya.settings.info.Settings;

class ApiCredentialManager {

    private ApiCredentialManager() {}

    public static String getApiKey() {
        return Settings.API_KEY.getValue();
    }

    public static String getApiSecret() {
        return Settings.API_SECRET.getValue();
    }

    public static void setApiCredential(String apiKey, String apiSecret) {

        Settings.API_KEY.putValue(apiKey);
        Settings.API_SECRET.putValue(apiSecret);

        ApiCredential.Frodo.KEY = apiKey;
        ApiCredential.Frodo.SECRET = apiSecret;
        ApiCredential.ApiV2.KEY = apiKey;
        ApiCredential.ApiV2.SECRET = apiSecret;
    }
}
