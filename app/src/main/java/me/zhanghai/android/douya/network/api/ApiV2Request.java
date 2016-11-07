/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api;

import com.android.volley.toolbox.Authenticator;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import me.zhanghai.android.douya.account.info.AccountContract;
import me.zhanghai.android.douya.network.Volley;
import me.zhanghai.android.douya.network.api.credential.ApiCredential;

public class ApiV2Request<T> extends ApiRequest<T> {

    public ApiV2Request(int method, String url, Type type) {
        super(method, url, type);

        init();
    }

    public ApiV2Request(int method, String url, TypeToken<T> typeToken) {
        super(method, url, typeToken);

        init();
    }

    private void init() {

        addHeaderUserAgent(ApiContract.Request.ApiV2.USER_AGENT);

        addParam(ApiContract.Request.ApiV2.Base.API_KEY, ApiCredential.ApiV2.KEY);
    }

    @Override
    protected Authenticator getAuthenticator() {
        return Volley.getInstance().getAuthenticator(AccountContract.AUTH_TOKEN_TYPE_API_V2);
    }
}
