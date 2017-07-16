/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api;

import android.accounts.Account;

import java.io.IOException;

import me.zhanghai.android.douya.network.AndroidAuthenticator;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ApiAuthenticator extends AndroidAuthenticator {

    public ApiAuthenticator(Account account, String authTokenType) {
        super(account, authTokenType, true);
    }

    @Override
    public boolean shouldRetryAuthentication(Response response) {
        // Don't consume the response body so that it can later be read by others.
        ResponseBody responseBody;
        try {
            responseBody = response.peekBody(Long.MAX_VALUE);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        //noinspection ThrowableInstanceNeverThrown
        ApiError apiError = new ApiError(response, responseBody);
        switch (apiError.code) {
            case ApiContract.Response.Error.Codes.Token.INVALID_ACCESS_TOKEN:
            case ApiContract.Response.Error.Codes.Token.ACCESS_TOKEN_HAS_EXPIRED:
            case ApiContract.Response.Error.Codes.Token.INVALID_REFRESH_TOKEN:
            case ApiContract.Response.Error.Codes.Token
                    .ACCESS_TOKEN_HAS_EXPIRED_SINCE_PASSWORD_CHANGED:
                return true;
            default:
                return false;
        }
    }
}
