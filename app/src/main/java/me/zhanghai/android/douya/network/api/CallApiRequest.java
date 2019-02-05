/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api;

import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Response;

public class CallApiRequest<T> implements ApiRequest<T> {

    private static Handler sMainThreadHandler = new Handler(Looper.getMainLooper());

    private Call<T> mCall;

    public CallApiRequest(Call<T> call) {
        mCall = call;
    }

    public T execute() throws ApiError {
        Response<T> response;
        try {
            response = mCall.execute();
        } catch (IOException e) {
            throw new ApiError(e);
        }
        if (response.isSuccessful()) {
            return response.body();
        } else {
            throw new ApiError(response);
        }
    }

    public void enqueue(@NonNull final Callback<T> callback) {
        mCall.enqueue(new retrofit2.Callback<T>() {
            @Override
            public void onResponse(@NonNull Call<T> call, @NonNull final Response<T> response) {
                if (response.isSuccessful()) {
                    sMainThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onResponse(response.body());
                        }
                    });
                } else {
                    sMainThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onErrorResponse(new ApiError(response));
                        }
                    });
                }
            }
            @Override
            public void onFailure(@NonNull Call<T> call, @NonNull final Throwable t) {
                sMainThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onErrorResponse(new ApiError(t));
                    }
                });
            }
        });
    }

    public boolean isExecuted() {
        return mCall.isExecuted();
    }

    public void cancel() {
        mCall.cancel();
    }

    public boolean isCanceled() {
        return mCall.isCanceled();
    }

    @Override
    public CallApiRequest<T> clone() {
        CallApiRequest<T> clone;
        try {
            //noinspection unchecked
            clone = (CallApiRequest<T>) super.clone();
        } catch (CloneNotSupportedException e) {
            // Should never happen.
            throw new RuntimeException(e);
        }
        clone.mCall = mCall.clone();
        return clone;
    }

    public Request request() {
        return mCall.request();
    }
}
