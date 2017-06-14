/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api;

import me.zhanghai.android.douya.account.info.AccountContract;
import me.zhanghai.android.douya.account.util.AccountUtils;

public class ApiAuthenticators {

    private static final ApiAuthenticators sInstance = new ApiAuthenticators();

    private ApiAuthenticator mApiV2Authenticator;
    private ApiAuthenticator mFrodoAuthenticator;

    public static ApiAuthenticators getInstance() {
        return sInstance;
    }

    private ApiAuthenticators() {
        createAuthenticatorsForActiveAccount();
    }

    private void createAuthenticatorsForActiveAccount() {
        mApiV2Authenticator = new ApiAuthenticator(AccountUtils.getActiveAccount(),
                AccountContract.AUTH_TOKEN_TYPE_API_V2);
        mFrodoAuthenticator = new ApiAuthenticator(AccountUtils.getActiveAccount(),
                AccountContract.AUTH_TOKEN_TYPE_FRODO);
    }

    public void notifyActiveAccountChanged() {
        createAuthenticatorsForActiveAccount();
    }

    public ApiAuthenticator getAuthenticator(String authTokenType) {
        switch (authTokenType) {
            case AccountContract.AUTH_TOKEN_TYPE_API_V2:
                return mApiV2Authenticator;
            case AccountContract.AUTH_TOKEN_TYPE_FRODO:
                return mFrodoAuthenticator;
            default:
                throw new IllegalArgumentException("Unknown authTokenType: " + authTokenType);
        }
    }
}
