/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.credential;

import me.zhanghai.android.douya.DouyaApplication;

public final class ApiCredential {

    private ApiCredential() {}

    public static class Frodo {
        public static String KEY = HackyApiCredentialHelper.getApiKey();
        public static String SECRET = HackyApiCredentialHelper.getApiSecret();
    }

    public static class ApiV2 {
        public static String KEY = HackyApiCredentialHelper.getApiKey();
        public static String SECRET = HackyApiCredentialHelper.getApiSecret();
    }
}

class HackyApiCredentialHelper {

    public static String getApiKey() {
        return ApiCredentialManager.getApiKey(DouyaApplication.getInstance());
    }

    public static String getApiSecret() {
        return ApiCredentialManager.getApiSecret(DouyaApplication.getInstance());
    }
}
