/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.credential;

import me.zhanghai.android.douya.settings.info.Settings;

class ApiCredentialManager {

    private ApiCredentialManager() {}

    public static String getApiV2ApiKey() {
        return Settings.API_V2_API_KEY.getValue();
    }

    public static String getApiV2ApiSecret() {
        return Settings.API_V2_API_SECRET.getValue();
    }

    public static String getFrodoApiKey() {
        return Settings.FRODO_API_KEY.getValue();
    }

    public static String getFrodoApiSecret() {
        return Settings.FRODO_API_SECRET.getValue();
    }

    public static void setApiCredential(String apiV2ApiKey, String apiV2ApiSecret,
                                        String frodoApiKey, String frodoApiSecret) {

        Settings.API_V2_API_KEY.putValue(apiV2ApiKey);
        Settings.API_V2_API_SECRET.putValue(apiV2ApiSecret);
        ApiCredential.ApiV2.KEY = apiV2ApiKey;
        ApiCredential.ApiV2.SECRET = apiV2ApiSecret;

        Settings.FRODO_API_KEY.putValue(frodoApiKey);
        Settings.FRODO_API_SECRET.putValue(frodoApiSecret);
        ApiCredential.Frodo.KEY = frodoApiKey;
        ApiCredential.Frodo.SECRET = frodoApiSecret;
    }
}
