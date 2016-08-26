/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import me.zhanghai.android.douya.network.api.credential.ApiCredential;

public class ApiV2Request<T> extends ApiRequest<T> {

    public ApiV2Request(int method, String url, Type type) {
        super(method, url, type);

        init();
    }

    public ApiV2Request(int method, String url, TypeToken<T> typeToken) {
        super(method, url, typeToken);

        init();
    }

    private void init() {
        addParam(ApiContract.Request.ApiV2.Base.API_KEY, ApiCredential.ApiV2.KEY);
    }
}
