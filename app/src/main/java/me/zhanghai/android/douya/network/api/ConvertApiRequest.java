/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api;

import androidx.annotation.NonNull;

import okhttp3.Request;

public abstract class ConvertApiRequest<S, T> implements ApiRequest<T> {

    private ApiRequest<S> mRequest;

    public ConvertApiRequest(ApiRequest<S> request) {
        mRequest = request;
    }

    @Override
    public T execute() throws ApiError {
        return transform(mRequest.execute());
    }

    @Override
    public void enqueue(@NonNull Callback<T> callback) {
        mRequest.enqueue(new Callback<S>() {
            @Override
            public void onResponse(S response) {
                callback.onResponse(transform(response));
            }
            @Override
            public void onErrorResponse(ApiError error) {
                callback.onErrorResponse(error);
            }
        });
    }

    protected abstract T transform(S responseBody);

    @Override
    public boolean isExecuted() {
        return mRequest.isExecuted();
    }

    @Override
    public void cancel() {
        mRequest.cancel();
    }

    @Override
    public boolean isCanceled() {
        return mRequest.isCanceled();
    }

    @Override
    public ConvertApiRequest<S, T> clone() {
        ConvertApiRequest<S, T> clone;
        try {
            //noinspection unchecked
            clone = (ConvertApiRequest<S, T>) super.clone();
        } catch (CloneNotSupportedException e) {
            // Should never happen.
            throw new RuntimeException(e);
        }
        clone.mRequest = mRequest.clone();
        return clone;
    }

    @Override
    public Request request() {
        return mRequest.request();
    }
}
