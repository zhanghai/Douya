/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.annotation.Nullable;

import me.zhanghai.android.douya.util.GsonHelper;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GsonResponseBodyConverterFactory extends Converter.Factory {

    private GsonConverterFactory mGsonConverterFactory;

    public static GsonResponseBodyConverterFactory create() {
        return new GsonResponseBodyConverterFactory(GsonConverterFactory.create(GsonHelper.get()));
    }

    private GsonResponseBodyConverterFactory(GsonConverterFactory gsonConverterFactory) {
        mGsonConverterFactory = gsonConverterFactory;
    }

    @Nullable
    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
                                                            Retrofit retrofit) {
        final Converter<ResponseBody, ?> converter = mGsonConverterFactory.responseBodyConverter(
                type, annotations, retrofit);
        return new Converter<ResponseBody, Object>() {
            @Override
            public Object convert(@NonNull ResponseBody value) throws ResponseConversionException {
                try {
                    return converter.convert(value);
                } catch (IOException e) {
                    throw new ResponseConversionException(e);
                }
            }
        };
    }
}
