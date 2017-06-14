/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api;

import android.support.annotation.NonNull;

import okhttp3.Request;

public class DelegateApiRequest<T> implements ApiRequest<T> {

    private ApiRequest<T> mCall;

    public DelegateApiRequest(ApiRequest<T> call) {
        mCall = call;
    }

    @Override
    public T execute() throws ApiError {
        return mCall.execute();
    }

    @Override
    public void enqueue(@NonNull Callback<T> callback) {
        mCall.enqueue(callback);
    }

    @Override
    public boolean isExecuted() {
        return mCall.isExecuted();
    }

    @Override
    public void cancel() {
        mCall.cancel();
    }

    @Override
    public boolean isCanceled() {
        return mCall.isCanceled();
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
        clone.mCall = mCall.clone();
        return clone;
    }

    @Override
    public Request request() {
        return mCall.request();
    }
}
