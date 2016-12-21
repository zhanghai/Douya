/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import me.zhanghai.android.douya.network.Request;

public abstract class TokenRequest extends Request<TokenRequest.Response> {

    public TokenRequest(int method, String url) {
        super(method, url);
    }

    @Override
    protected com.android.volley.Response<Response> parseNetworkResponse(NetworkResponse response) {
        try {
            String responseString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            return com.android.volley.Response.success(new Response(responseString),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (JSONException | UnsupportedEncodingException e) {
            return com.android.volley.Response.error(new ParseError(e));
        }
    }

    @Override
    protected Error parseNetworkError(VolleyError volleyError) {
        return Error.wrap(volleyError);
    }

    public static class Response {

        public String userName;
        public long userId;
        public String accessToken;
        public long accessTokenExpiresIn;
        public String refreshToken;

        public Response(String jsonString) throws JSONException {
            this(new JSONObject(jsonString));
        }

        public Response(JSONObject jsonObject) {
            userName = jsonObject.optString(ApiContract.Response.Token.DOUBAN_UESR_NAME, null);
            userId = jsonObject.optLong(ApiContract.Response.Token.DOUBAN_USER_ID, 0);
            accessToken = jsonObject.optString(ApiContract.Response.Token.ACCESS_TOKEN, null);
            accessTokenExpiresIn = jsonObject.optLong(ApiContract.Response.Token.EXPIRES_IN, 0);
            refreshToken = jsonObject.optString(ApiContract.Response.Token.REFRESH_TOKEN, null);
        }

        @Override
        public String toString() {
            return "Response{" +
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
