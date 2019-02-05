/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import me.zhanghai.android.douya.util.GsonHelper;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GsonResponseBodyConverterFactory extends Converter.Factory {

    private GsonConverterFactory mGsonConverterFactory;

    public static GsonResponseBodyConverterFactory create() {
        return new GsonResponseBodyConverterFactory(GsonConverterFactory.create(
                GsonHelper.GSON_NETWORK));
    }

    private GsonResponseBodyConverterFactory(GsonConverterFactory gsonConverterFactory) {
        mGsonConverterFactory = gsonConverterFactory;
    }

    @Nullable
    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
                                                            Retrofit retrofit) {
        Converter<ResponseBody, ?> converter = mGsonConverterFactory.responseBodyConverter(type,
                annotations, retrofit);
        return value -> {
            try {
                return converter.convert(value);
            } catch (IOException e) {
                throw new ResponseConversionException(e);
            }
        };
    }
}
