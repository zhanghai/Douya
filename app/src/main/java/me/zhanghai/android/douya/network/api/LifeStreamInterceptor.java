/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api;

import androidx.annotation.NonNull;

import java.io.IOException;

import me.zhanghai.android.douya.network.Http;
import me.zhanghai.android.douya.network.api.credential.ApiCredential;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class LifeStreamInterceptor implements Interceptor {

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request oldRequest = chain.request();
        Request request = oldRequest.newBuilder()
                .header(Http.Headers.USER_AGENT, ApiContract.Request.ApiV2.USER_AGENT)
                .url(oldRequest.url().newBuilder()
                        .addQueryParameter(ApiContract.Request.ApiV2.Base.API_KEY,
                                ApiCredential.ApiV2.KEY)
                        .addQueryParameter(ApiContract.Request.ApiV2.LifeStream.VERSION,
                                String.valueOf(ApiContract.Request.ApiV2.LifeStream.Versions.TWO))
                        .build())
                .build();
        return chain.proceed(request);
    }
}
