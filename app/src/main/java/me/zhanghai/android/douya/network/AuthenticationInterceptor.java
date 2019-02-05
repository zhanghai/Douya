/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.net.HttpRetryException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.UnrepeatableRequestBody;

// We cannot use Authenticator because it only retries on HTTP 401 (see
// RetryAndFollowUpInterceptor.java).
public abstract class AuthenticationInterceptor implements Interceptor {

    private int mMaxNumRetries;

    public AuthenticationInterceptor(int maxNumRetries) {
        mMaxNumRetries = maxNumRetries;
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {

        Authenticator authenticator = getAuthenticator();
        Request request = authenticator.authenticate(chain.request());
        Response response = null;

        for (int retryCount = 0; retryCount < mMaxNumRetries; ++retryCount) {

            response = chain.proceed(request);
            if (response.isSuccessful()) {
                return response;
            }

            Request retryRequest = authenticator.retryAuthentication(response);
            if (retryRequest == null) {
                return response;
            }

            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                responseBody.close();
            }
            if (retryRequest.body() instanceof UnrepeatableRequestBody) {
                throw new HttpRetryException("Cannot retry streamed HTTP body", response.code());
            }
            request = retryRequest;
        }

        return response;
    }

    protected abstract Authenticator getAuthenticator();

    public interface Authenticator {

        @NonNull
        Request authenticate(Request request) throws IOException;

        @Nullable
        Request retryAuthentication(Response response) throws IOException;
    }
}
