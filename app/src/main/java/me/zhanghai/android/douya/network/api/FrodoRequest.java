/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import me.zhanghai.android.douya.network.api.credential.ApiCredential;

public class FrodoRequest<T> extends ApiRequest<T> {

    public FrodoRequest(int method, String url, Type type) {
        super(method, url, type);

        init();
    }

    public FrodoRequest(int method, String url, TypeToken<T> typeToken) {
        super(method, url, typeToken);

        init();
    }

    private void init() {

        addHeaderUserAgent(ApiContract.Request.Frodo.USER_AGENT);

        addParam(ApiContract.Request.Frodo.Base.API_KEY, ApiCredential.Frodo.KEY);
        addParam(ApiContract.Request.Frodo.Base.CHANNEL,
                ApiContract.Request.Frodo.Base.Channels.DOUBAN);
    }
}
