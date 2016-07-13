/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.credential;

import me.zhanghai.android.douya.DouyaApplication;

public interface ApiCredential {

    interface Douya {
        String KEY = Frodo.KEY;
        String SECRET = Frodo.SECRET;
    }

    interface Frodo {
        String KEY = HackyApiCredentialHelper.getApiKey();
        String SECRET = HackyApiCredentialHelper.getApiSecret();
    }
}

class HackyApiCredentialHelper {

    public static String getApiKey() {
        //noinspection deprecation
        return ApiCredentialManager.getApiKey(DouyaApplication.getInstance());
    }

    public static String getApiSecret() {
        //noinspection deprecation
        return ApiCredentialManager.getApiSecret(DouyaApplication.getInstance());
    }
}
