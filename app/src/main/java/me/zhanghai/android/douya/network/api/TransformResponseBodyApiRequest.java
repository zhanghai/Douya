/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api;

import android.support.annotation.NonNull;

public abstract class TransformResponseBodyApiRequest<T> extends DelegateApiRequest<T> {

    public TransformResponseBodyApiRequest(ApiRequest<T> call) {
        super(call);
    }

    @Override
    public T execute() throws ApiError {
        return transformResponseBody(super.execute());
    }

    @Override
    public void enqueue(@NonNull final Callback<T> callback) {
        super.enqueue(new Callback<T>() {
            @Override
            public void onResponse(T response) {
                callback.onResponse(transformResponseBody(response));
            }
            @Override
            public void onErrorResponse(ApiError error) {
                callback.onErrorResponse(error);
            }
        });
    }

    private T transformResponseBody(T responseBody) {
        onTransformResponseBody(responseBody);
        return responseBody;
    }

    protected abstract void onTransformResponseBody(T responseBody);
}
