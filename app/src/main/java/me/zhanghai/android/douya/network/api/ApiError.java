/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.RedirectError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.network.api.ApiContract.Response.Error;
import me.zhanghai.android.douya.network.api.ApiContract.Response.Error.Codes.*;

public class ApiError extends VolleyError {

    private static final Map<Integer, Integer> ERROR_CODE_STRING_RES_MAP;
    static {

        ERROR_CODE_STRING_RES_MAP = new HashMap<>();

        ERROR_CODE_STRING_RES_MAP.put(Custom.INVALID_ERROR_RESPONSE,
                R.string.api_error_invalid_error_response);

        ERROR_CODE_STRING_RES_MAP.put(Base.UNKNOWN_V2_ERROR, R.string.api_error_unknown_v2_error);
        ERROR_CODE_STRING_RES_MAP.put(Base.NEED_PERMISSION, R.string.api_error_need_permission);
        ERROR_CODE_STRING_RES_MAP.put(Base.URI_NOT_FOUND, R.string.api_error_uri_not_found);
        ERROR_CODE_STRING_RES_MAP.put(Base.MISSING_ARGS, R.string.api_error_missing_args);
        ERROR_CODE_STRING_RES_MAP.put(Base.IMAGE_TOO_LARGE, R.string.api_error_image_too_large);
        ERROR_CODE_STRING_RES_MAP.put(Base.HAS_BAN_WORD, R.string.api_error_has_ban_word);
        ERROR_CODE_STRING_RES_MAP.put(Base.INPUT_TOO_SHORT, R.string.api_error_input_too_short);
        ERROR_CODE_STRING_RES_MAP.put(Base.TARGET_NOT_FOUND, R.string.api_error_target_not_found);
        ERROR_CODE_STRING_RES_MAP.put(Base.NEED_CAPTCHA, R.string.api_error_need_captcha);
        ERROR_CODE_STRING_RES_MAP.put(Base.IMAGE_UNKNOWN, R.string.api_error_image_unknown);
        ERROR_CODE_STRING_RES_MAP.put(Base.IMAGE_WRONG_FORMAT,
                R.string.api_error_image_wrong_format);
        ERROR_CODE_STRING_RES_MAP.put(Base.IMAGE_WRONG_CK, R.string.api_error_image_wrong_ck);
        ERROR_CODE_STRING_RES_MAP.put(Base.IMAGE_CK_EXPIRED, R.string.api_error_image_ck_expired);
        ERROR_CODE_STRING_RES_MAP.put(Base.TITLE_MISSING, R.string.api_error_title_missing);
        ERROR_CODE_STRING_RES_MAP.put(Base.DESC_MISSING, R.string.api_error_desc_missing);

        ERROR_CODE_STRING_RES_MAP.put(Token.INVALID_REQUEST_SCHEME,
                R.string.api_error_token_invalid_request_scheme);
        ERROR_CODE_STRING_RES_MAP.put(Token.INVALID_REQUEST_METHOD,
                R.string.api_error_token_invalid_request_method);
        ERROR_CODE_STRING_RES_MAP.put(Token.ACCESS_TOKEN_IS_MISSING,
                R.string.api_error_token_access_token_is_missing);
        ERROR_CODE_STRING_RES_MAP.put(Token.INVALID_ACCESS_TOKEN,
                R.string.api_error_token_invalid_access_token);
        ERROR_CODE_STRING_RES_MAP.put(Token.INVALID_APIKEY,
                R.string.api_error_token_invalid_apikey);
        ERROR_CODE_STRING_RES_MAP.put(Token.APIKEY_IS_BLOCKED,
                R.string.api_error_token_apikey_is_blocked);
        ERROR_CODE_STRING_RES_MAP.put(Token.ACCESS_TOKEN_HAS_EXPIRED,
                R.string.api_error_token_access_token_has_expired);
        ERROR_CODE_STRING_RES_MAP.put(Token.INVALID_REQUEST_URI,
                R.string.api_error_token_invalid_request_uri);
        ERROR_CODE_STRING_RES_MAP.put(Token.INVALID_CREDENCIAL1,
                R.string.api_error_token_invalid_credencial1);
        ERROR_CODE_STRING_RES_MAP.put(Token.INVALID_CREDENCIAL2,
                R.string.api_error_token_invalid_credencial2);
        ERROR_CODE_STRING_RES_MAP.put(Token.NOT_TRIAL_USER,
                R.string.api_error_token_not_trial_user);
        ERROR_CODE_STRING_RES_MAP.put(Token.RATE_LIMIT_EXCEEDED1,
                R.string.api_error_token_rate_limit_exceeded1);
        ERROR_CODE_STRING_RES_MAP.put(Token.RATE_LIMIT_EXCEEDED2,
                R.string.api_error_token_rate_limit_exceeded2);
        ERROR_CODE_STRING_RES_MAP.put(Token.REQUIRED_PARAMETER_IS_MISSING,
                R.string.api_error_token_required_parameter_is_missing);
        ERROR_CODE_STRING_RES_MAP.put(Token.UNSUPPORTED_GRANT_TYPE,
                R.string.api_error_token_unsupported_grant_type);
        ERROR_CODE_STRING_RES_MAP.put(Token.UNSUPPORTED_RESPONSE_TYPE,
                R.string.api_error_token_unsupported_response_type);
        ERROR_CODE_STRING_RES_MAP.put(Token.CLIENT_SECRET_MISMATCH,
                R.string.api_error_token_client_secret_mismatch);
        ERROR_CODE_STRING_RES_MAP.put(Token.REDIRECT_URI_MISMATCH,
                R.string.api_error_token_redirect_uri_mismatch);
        ERROR_CODE_STRING_RES_MAP.put(Token.INVALID_AUTHORIZATION_CODE,
                R.string.api_error_token_invalid_authorization_code);
        ERROR_CODE_STRING_RES_MAP.put(Token.INVALID_REFRESH_TOKEN,
                R.string.api_error_token_invalid_refresh_token);
        ERROR_CODE_STRING_RES_MAP.put(Token.USERNAME_PASSWORD_MISMATCH,
                R.string.api_error_token_username_password_mismatch);
        ERROR_CODE_STRING_RES_MAP.put(Token.INVALID_USER, R.string.api_error_token_invalid_user);
        ERROR_CODE_STRING_RES_MAP.put(Token.USER_HAS_BLOCKED,
                R.string.api_error_token_user_has_blocked);
        ERROR_CODE_STRING_RES_MAP.put(Token.ACCESS_TOKEN_HAS_EXPIRED_SINCE_PASSWORD_CHANGED,
                R.string.api_error_token_access_token_has_expired_since_password_changed);
        ERROR_CODE_STRING_RES_MAP.put(Token.ACCESS_TOKEN_HAS_NOT_EXPIRED,
                R.string.api_error_token_access_token_has_not_expired);
        ERROR_CODE_STRING_RES_MAP.put(Token.INVALID_REQUEST_SCOPE,
                R.string.api_error_token_invalid_request_scope);
        ERROR_CODE_STRING_RES_MAP.put(Token.INVALID_REQUEST_SOURCE,
                R.string.api_error_token_invalid_request_source);
        ERROR_CODE_STRING_RES_MAP.put(Token.THIRDPARTY_LOGIN_AUTH_FAILED,
                R.string.api_error_token_thirdparty_login_auth_failed);
        ERROR_CODE_STRING_RES_MAP.put(Token.USER_LOCKED, R.string.api_error_token_user_locked);

        ERROR_CODE_STRING_RES_MAP.put(Followship.ALREADY_FOLLOWED,
                R.string.api_error_followship_already_followed);
        ERROR_CODE_STRING_RES_MAP.put(Followship.NOT_FOLLOWED_YET,
                R.string.api_error_followship_not_followed_yet);

        ERROR_CODE_STRING_RES_MAP.put(Broadcast.NOT_FOUND, R.string.api_error_broadcast_not_found);
        ERROR_CODE_STRING_RES_MAP.put(Broadcast.AUTHOR_BANNED,
                R.string.api_error_broadcast_author_banned);

        ERROR_CODE_STRING_RES_MAP.put(LikeBroadcast.ALREADY_LIKED,
                R.string.api_error_like_broadcast_already_liked);
        ERROR_CODE_STRING_RES_MAP.put(LikeBroadcast.NOT_LIKED_YET,
                R.string.api_error_like_broadcast_not_liked_yet);

        ERROR_CODE_STRING_RES_MAP.put(RebroadcastBroadcast.ALREADY_REBROADCASTED,
                R.string.api_error_rebroadcast_broadcast_already_rebroadcasted);
        ERROR_CODE_STRING_RES_MAP.put(RebroadcastBroadcast.NOT_REBROADCASTED_YET,
                R.string.api_error_rebroadcast_broadcast_not_rebroadcasted_yet);
    }

    public String responseString;
    public JSONObject responseJson;
    public int code;
    public String localizedMessage;
    public String message;
    public String request;

    protected ApiError(NetworkResponse response) {
        super(response);

        if (response.headers == null || response.data == null) {
            return;
        }
        String charset = HttpHeaderParser.parseCharset(response.headers);
        // Don't throw an exception from here, just do the best we can.
        try {
            responseString = new String(response.data, charset);
            parseResponse();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    protected ApiError(VolleyError volleyError) {
        super(volleyError);
    }

    public static ApiError wrap(VolleyError error) {
        if (error.networkResponse != null) {
            return new ApiError(error.networkResponse);
        } else {
            return new ApiError(error);
        }
    }

    private void parseResponse() {
        try {
            responseJson = new JSONObject(responseString);
            code = responseJson.optInt(Error.CODE, 0);
            message = responseJson.optString(Error.MSG, null);
            request = responseJson.optString(Error.REQUEST, null);
            localizedMessage = responseJson.optString(Error.LOCALIZED_MESSAGE, null);
        } catch (JSONException e) {
            e.printStackTrace();
            code = Custom.INVALID_ERROR_RESPONSE;
        }
    }

    public int getErrorStringRes() {

        if (networkResponse == null) {
            // Return as the wrapped error.
            // We only have two constructors, so this cast is safe.
            return getErrorStringRes((VolleyError) this.getCause());
        }

        Integer StringRes = ERROR_CODE_STRING_RES_MAP.get(code);
        return StringRes != null ? StringRes : R.string.api_error_unknown;
    }

    public static int getErrorStringRes(VolleyError error) {
        if (error instanceof ParseError) {
            return R.string.api_error_parse;
        } else if (error instanceof TimeoutError) {
            return R.string.api_error_timeout;
        } else if (error instanceof NoConnectionError) {
            return R.string.api_error_no_connection;
        } else if (error instanceof AuthFailureError) {
            return R.string.api_error_auth_failure;
        } else if (error instanceof RedirectError) {
            return R.string.api_error_redirect;
        } else if (error instanceof ServerError) {
            return R.string.api_error_server;
        } else if (error instanceof NetworkError) {
            return R.string.api_error_network;
        } else if (error instanceof ApiError) {
            return ((ApiError) error).getErrorStringRes();
        } else {
            return R.string.api_error_unknown;
        }
    }

    public static String getErrorString(VolleyError error, Context context) {
        return context.getString(getErrorStringRes(error));
    }

    @Override
    public String toString() {
        return "ApiError{" +
                "responseString='" + responseString + '\'' +
                ", responseJson=" + responseJson +
                ", code=" + code +
                ", localizedMessage='" + localizedMessage + '\'' +
                ", message='" + message + '\'' +
                ", request='" + request + '\'' +
                "} " + super.toString();
    }
}
