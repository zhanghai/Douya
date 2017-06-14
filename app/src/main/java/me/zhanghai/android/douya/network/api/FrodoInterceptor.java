/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api;

import android.support.annotation.NonNull;

import java.io.IOException;

import me.zhanghai.android.douya.network.Http;
import me.zhanghai.android.douya.network.api.credential.ApiCredential;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class FrodoInterceptor implements Interceptor {

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request oldRequest = chain.request();
        Request request = oldRequest.newBuilder()
                .header(Http.Headers.USER_AGENT, ApiContract.Request.Frodo.USER_AGENT)
                .url(oldRequest.url().newBuilder()
                        .addQueryParameter(ApiContract.Request.Frodo.Base.API_KEY,
                                ApiCredential.Frodo.KEY)
                        .addQueryParameter(ApiContract.Request.Frodo.Base.CHANNEL,
                                ApiContract.Request.Frodo.Base.Channels.DOUBAN)
                        .build())
                .build();
        return chain.proceed(request);
    }
}
