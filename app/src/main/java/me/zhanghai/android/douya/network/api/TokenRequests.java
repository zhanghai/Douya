/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api;

import me.zhanghai.android.douya.account.info.AccountContract;

public class TokenRequests {

    private TokenRequests() {}

    public static TokenRequest newRequest(String authTokenType, String refreshToken) {
        switch (authTokenType) {
            case AccountContract.AUTH_TOKEN_TYPE_API_V2:
                return new ApiV2TokenRequest(refreshToken);
            case AccountContract.AUTH_TOKEN_TYPE_FRODO:
                return new FrodoTokenRequest(refreshToken);
            default:
                throw new IllegalArgumentException("Unknown authTokenType: " + authTokenType);
        }
    }

    public static TokenRequest newRequest(String authTokenType, String username,
                                          String password) {
        switch (authTokenType) {
            case AccountContract.AUTH_TOKEN_TYPE_API_V2:
                return new ApiV2TokenRequest(username, password);
            case AccountContract.AUTH_TOKEN_TYPE_FRODO:
                return new FrodoTokenRequest(username, password);
            default:
                throw new IllegalArgumentException("Unknown authTokenType: " + authTokenType);
        }
    }
}
