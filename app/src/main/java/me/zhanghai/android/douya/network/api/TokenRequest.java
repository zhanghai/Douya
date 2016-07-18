/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import me.zhanghai.android.douya.network.Request;
import me.zhanghai.android.douya.network.api.credential.ApiCredential;

public class TokenRequest extends Request<TokenRequest.Result> {

    private TokenRequest() {
        super(Method.POST, ApiContract.Request.Token.URL);

        addParam(ApiContract.Request.Token.CLIENT_ID, ApiCredential.Frodo.KEY);
        addParam(ApiContract.Request.Token.CLIENT_SECRET, ApiCredential.Frodo.SECRET);
        addParam(ApiContract.Request.Token.REDIRECT_URI,
                ApiContract.Request.Token.RedirectUris.FRODO);

        addHeaderUserAgent(ApiContract.Request.Frodo.USER_AGENT);
        addHeaderAcceptCharsetUtf8();

        setRetryPolicy(new RetryPolicy(2500, 1, 1.0f));
    }

    public TokenRequest(String username, String password) {
        this();

        addParam(ApiContract.Request.Token.GRANT_TYPE,
                ApiContract.Request.Token.GrantTypes.PASSWORD);
        addParam(ApiContract.Request.Token.USERNAME, username);
        addParam(ApiContract.Request.Token.PASSWORD, password);

        setRetryPolicy(new RetryPolicy(20000, 0, 1.0f));
    }

    public TokenRequest(String refreshToken) {
        this();

        addParam(ApiContract.Request.Token.GRANT_TYPE,
                ApiContract.Request.Token.GrantTypes.REFRESH_TOKEN);
        addParam(ApiContract.Request.Token.REFRESH_TOKEN, refreshToken);
    }

    @Override
    protected Response<Result> parseNetworkResponse(NetworkResponse response) {
        try {
            String responseString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            return Response.success(new Result(responseString),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (JSONException | UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected Error parseNetworkError(VolleyError volleyError) {
        return Error.wrap(volleyError);
    }

    private static class RetryPolicy extends DefaultRetryPolicy {

        public RetryPolicy(int initialTimeoutMs, int maxNumRetries, float backoffMultiplier) {
            super(initialTimeoutMs, maxNumRetries, backoffMultiplier);
        }

        @Override
        public void retry(VolleyError error) throws VolleyError {
            Error tokenError = Error.wrap(error);
            if (tokenError.code != 0) {
                throw error;
            } else {
                super.retry(error);
            }
        }
    }

    public static class Result {

        public String userName;
        public long userId;
        public String accessToken;
        public long accessTokenExpiresIn;
        public String refreshToken;

        public Result(String jsonString) throws JSONException {
            this(new JSONObject(jsonString));
        }

        public Result(JSONObject jsonObject) {
            userName = jsonObject.optString(ApiContract.Response.Token.DOUBAN_UESR_NAME, null);
            userId = jsonObject.optLong(ApiContract.Response.Token.DOUBAN_USER_ID, 0);
            accessToken = jsonObject.optString(ApiContract.Response.Token.ACCESS_TOKEN, null);
            accessTokenExpiresIn = jsonObject.optLong(ApiContract.Response.Token.EXPIRES_IN, 0);
            refreshToken = jsonObject.optString(ApiContract.Response.Token.REFRESH_TOKEN, null);
        }

        @Override
        public String toString() {
            return "Result{" +
                    "userName='" + userName + '\'' +
                    ", userId=" + userId +
                    ", accessToken='" + accessToken + '\'' +
                    ", accessTokenExpiresIn=" + accessTokenExpiresIn +
                    ", refreshToken='" + refreshToken + '\'' +
                    '}';
        }
    }

    public static class Error extends ApiError {

        protected Error(NetworkResponse response) {
            super(response);
        }

        protected Error(VolleyError volleyError) {
            super(volleyError);
        }

        public static Error wrap(VolleyError error) {
            if (error.networkResponse != null) {
                return new Error(error.networkResponse);
            } else {
                return new Error(error);
            }
        }
    }
}
