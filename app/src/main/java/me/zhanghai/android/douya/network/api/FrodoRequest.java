/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api;

import android.content.Context;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

@Frodo
public class FrodoRequest<T> extends ApiRequest<T> {

    public FrodoRequest(int method, String url, Type type, Context context) {
        super(method, url, type, context);

        init();
    }

    public FrodoRequest(int method, String url, TypeToken<T> typeToken, Context context) {
        super(method, url, typeToken, context);

        init();
    }

    private void init() {

        addHeaderUserAgent(ApiContract.Request.Frodo.USER_AGENT);

        addParam(ApiContract.Request.Base.API_KEY, ApiContract.Request.Frodo.API_KEY);
        addParam(ApiContract.Request.Base.CHANNEL, ApiContract.Request.Frodo.CHANNEL);
    }
}
