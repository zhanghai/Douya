/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api;

import android.content.Context;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class LifeStreamRequest<T> extends ApiRequest<T> {

    public LifeStreamRequest(int method, String url, Type type, Context context) {
        super(method, url, type, context);

        init();
    }

    public LifeStreamRequest(int method, String url, TypeToken<T> typeToken, Context context) {
        super(method, url, typeToken, context);

        init();
    }

    private void init() {
        addParam(ApiContract.Request.LifeStream.VERSION,
                String.valueOf(ApiContract.Request.LifeStream.Versions.TWO));
    }
}
