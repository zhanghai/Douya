/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.VolleyError;

import me.zhanghai.android.douya.network.api.credential.ApiCredential;

public class FrodoTokenRequest extends TokenRequest {

    private FrodoTokenRequest() {
        super(Method.POST, ApiContract.Request.Token.URL);

        addParam(ApiContract.Request.Token.CLIENT_ID, ApiCredential.Frodo.KEY);
        addParam(ApiContract.Request.Token.CLIENT_SECRET, ApiCredential.Frodo.SECRET);
        addParam(ApiContract.Request.Token.REDIRECT_URI,
                ApiContract.Request.Token.RedirectUris.FRODO);

        addHeaderUserAgent(ApiContract.Request.Frodo.USER_AGENT);
        addHeaderAcceptCharsetUtf8();
    }

    public FrodoTokenRequest(String username, String password) {
        this();

        addParam(ApiContract.Request.Token.GRANT_TYPE,
                ApiContract.Request.Token.GrantTypes.PASSWORD);
        addParam(ApiContract.Request.Token.USERNAME, username);
        addParam(ApiContract.Request.Token.PASSWORD, password);

        setRetryPolicy(new RetryPolicy(20000, 0, 1.0f));
    }

    public FrodoTokenRequest(String refreshToken) {
        this();

        addParam(ApiContract.Request.Token.GRANT_TYPE,
                ApiContract.Request.Token.GrantTypes.REFRESH_TOKEN);
        addParam(ApiContract.Request.Token.REFRESH_TOKEN, refreshToken);

        setRetryPolicy(new RetryPolicy(2500, 1, 1.0f));
    }

    private static class RetryPolicy extends DefaultRetryPolicy {

        public RetryPolicy(int initialTimeoutMs, int maxNumRetries, float backoffMultiplier) {
            super(initialTimeoutMs, maxNumRetries, backoffMultiplier);
        }

        @Override
        public void retry(VolleyError error) throws VolleyError {
            //noinspection ThrowableResultOfMethodCallIgnored
            Error tokenError = Error.wrap(error);
            if (tokenError.code != 0) {
                throw error;
            } else {
                super.retry(error);
            }
        }
    }
}
