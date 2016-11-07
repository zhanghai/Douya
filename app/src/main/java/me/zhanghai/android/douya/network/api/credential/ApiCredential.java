/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.credential;

public final class ApiCredential {

    private ApiCredential() {}

    public static class ApiV2 {
        public static String KEY = ApiCredentialManager.getApiV2ApiKey();
        public static String SECRET = ApiCredentialManager.getApiV2ApiSecret();
    }

    public static class Frodo {
        public static String KEY = ApiCredentialManager.getFrodoApiKey();
        public static String SECRET = ApiCredentialManager.getFrodoApiSecret();
    }
}
