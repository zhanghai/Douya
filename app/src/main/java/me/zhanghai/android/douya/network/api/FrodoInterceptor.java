/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import me.zhanghai.android.douya.network.Http;
import me.zhanghai.android.douya.network.api.credential.ApiCredential;
import me.zhanghai.android.douya.network.api.util.InterceptorUtils;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class FrodoInterceptor implements Interceptor {

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder builder = request.newBuilder()
                .header(Http.Headers.USER_AGENT, ApiContract.Request.Frodo.USER_AGENT);
        Map<String, String> parameters = new HashMap<>();
        parameters.put(ApiContract.Request.Frodo.API_KEY, ApiCredential.Frodo.KEY);
        parameters.put(ApiContract.Request.Frodo.CHANNEL,
                ApiContract.Request.Frodo.Channels.DOUBAN);
        // TODO: UUID
        parameters.put(ApiContract.Request.Frodo.OS_ROM, ApiContract.Request.Frodo.OsRoms.ANDROID);
        InterceptorUtils.addParameters(builder, request, parameters);
        request = builder.build();
        return chain.proceed(request);
    }
}
