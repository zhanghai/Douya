/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api;

import android.support.annotation.NonNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.annotation.Nullable;

import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;

public class ApiCallAdapter<R> implements CallAdapter<R, ApiRequest<R>> {

    private Type mResponseType;

    private ApiCallAdapter(Type responseType) {
        mResponseType = responseType;
    }

    @Override
    public Type responseType() {
        return mResponseType;
    }

    @Override
    public ApiRequest<R> adapt(@NonNull Call<R> call) {
        return new CallApiRequest<>(call);
    }

    public static class Factory extends CallAdapter.Factory {

        public static CallAdapter.Factory create() {
            return new Factory();
        }

        private Factory() {}

        @Nullable
        @Override
        public CallAdapter<?, ?> get(@NonNull Type returnType, @NonNull Annotation[] annotations,
                                     @NonNull Retrofit retrofit) {
            if (getRawType(returnType) != ApiRequest.class) {
                return null;
            }
            if (!(returnType instanceof ParameterizedType)) {
                throw new IllegalStateException("ApiRequest return type must be parameterized as" +
                        " ApiRequest<Foo> or ApiRequest<? extends Foo>");
            }
            Type innerType = getParameterUpperBound(0, (ParameterizedType) returnType);
            return new ApiCallAdapter<>(innerType);
        }
    }
}
