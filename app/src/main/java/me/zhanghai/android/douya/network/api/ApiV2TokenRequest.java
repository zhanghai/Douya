/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api;

import me.zhanghai.android.douya.network.api.credential.ApiCredential;

public class ApiV2TokenRequest extends TokenRequest {

    private ApiV2TokenRequest() {
        super(Method.POST, ApiContract.Request.Token.URL);

        addParam(ApiContract.Request.Token.CLIENT_ID, ApiCredential.ApiV2.KEY);
        addParam(ApiContract.Request.Token.CLIENT_SECRET, ApiCredential.ApiV2.SECRET);
        addParam(ApiContract.Request.Token.REDIRECT_URI,
                ApiContract.Request.Token.RedirectUris.API_V2);

        addHeaderUserAgent(ApiContract.Request.ApiV2.USER_AGENT);
        addHeaderAcceptCharsetUtf8();
    }

    public ApiV2TokenRequest(String username, String password) {
        this();

        addParam(ApiContract.Request.Token.GRANT_TYPE,
                ApiContract.Request.Token.GrantTypes.PASSWORD);
        addParam(ApiContract.Request.Token.USERNAME, username);
        addParam(ApiContract.Request.Token.PASSWORD, password);
    }

    public ApiV2TokenRequest(String refreshToken) {
        this();

        addParam(ApiContract.Request.Token.GRANT_TYPE,
                ApiContract.Request.Token.GrantTypes.REFRESH_TOKEN);
        addParam(ApiContract.Request.Token.REFRESH_TOKEN, refreshToken);
    }
}
