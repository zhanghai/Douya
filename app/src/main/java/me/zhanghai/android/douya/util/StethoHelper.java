/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util;

import android.content.Context;
import android.support.annotation.NonNull;

import java.io.IOException;

import me.zhanghai.android.douya.BuildConfig;
import okhttp3.Interceptor;
import okhttp3.Response;

public class StethoHelper {

    private StethoHelper() {}

    public static void initializeWithDefaults(Context context) {
        if (BuildConfig.DEBUG) {
            com.facebook.stetho.Stetho.initializeWithDefaults(context);
        }
    }

    public static Interceptor newInterceptor() {
        if (BuildConfig.DEBUG) {
            return new com.facebook.stetho.okhttp3.StethoInterceptor();
        } else {
            return new NoOpInterceptor();
        }
    }

    private static class NoOpInterceptor implements Interceptor {
        @Override
        public Response intercept(@NonNull Chain chain) throws IOException {
            return chain.proceed(chain.request());
        }
    }
}
