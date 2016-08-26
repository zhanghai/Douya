/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class LifeStreamRequest<T> extends ApiV2Request<T> {

    public LifeStreamRequest(int method, String url, Type type) {
        super(method, url, type);

        init();
    }

    public LifeStreamRequest(int method, String url, TypeToken<T> typeToken) {
        super(method, url, typeToken);

        init();
    }

    private void init() {
        addParam(ApiContract.Request.ApiV2.LifeStream.VERSION,
                String.valueOf(ApiContract.Request.ApiV2.LifeStream.Versions.TWO));
    }
}
