/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api;

import android.support.annotation.NonNull;

import okhttp3.Request;

public class DelegateApiRequest<T> implements ApiRequest<T> {

    private ApiRequest<T> mRequest;

    public DelegateApiRequest(ApiRequest<T> request) {
        mRequest = request;
    }

    @Override
    public T execute() throws ApiError {
        return mRequest.execute();
    }

    @Override
    public void enqueue(@NonNull Callback<T> callback) {
        mRequest.enqueue(callback);
    }

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
    public DelegateApiRequest<T> clone() {
        DelegateApiRequest<T> clone;
        try {
            //noinspection unchecked
            clone = (DelegateApiRequest<T>) super.clone();
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
