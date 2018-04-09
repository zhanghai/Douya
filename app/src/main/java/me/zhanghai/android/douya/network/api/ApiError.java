/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api;

import android.content.Context;
import android.util.SparseIntArray;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.network.AuthenticationException;
import me.zhanghai.android.douya.network.ResponseConversionException;
import me.zhanghai.android.douya.network.api.ApiContract.Response.Error;
import me.zhanghai.android.douya.network.api.ApiContract.Response.Error.Codes.*;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ApiError extends Throwable {

    private static final SparseIntArray ERROR_CODE_STRING_RES_MAP;
    static {

        ERROR_CODE_STRING_RES_MAP = new SparseIntArray();

        ERROR_CODE_STRING_RES_MAP.put(Custom.INVALID_ERROR_RESPONSE,
                R.string.api_error_invalid_error_response);

        ERROR_CODE_STRING_RES_MAP.put(Base.INVALID_REQUEST_997,
                R.string.api_error_invalid_request_997);
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

        ERROR_CODE_STRING_RES_MAP.put(RebroadcastBroadcast.REBROADCASTED_BROADCAST_DELETED,
                R.string.api_error_rebroadcast_broadcast_deleted);
    }

    public Object response;
    public String bodyString;
    public JSONObject bodyJson;
    public int code;
    public String localizedMessage;
    public String message;
    public String request;

    public ApiError(Throwable throwable) {
        super(throwable);
    }

    public ApiError(Object response, ResponseBody responseBody) {
        this.response = response;
        if (responseBody != null) {
            // Don't throw an exception from here, just do the best we can.
            try {
                bodyString = responseBody.string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            responseBody.close();
            if (bodyString != null) {
                parseResponse();
            }
        }
    }

    public ApiError(Response response) {
        this(response, response.body());
    }

    public ApiError(retrofit2.Response<?> response) {
        this(response, response.errorBody());
    }

    private void parseResponse() {
        try {
            bodyJson = new JSONObject(bodyString);
            code = bodyJson.optInt(Error.CODE, 0);
            message = bodyJson.optString(Error.MSG, null);
            request = bodyJson.optString(Error.REQUEST, null);
            localizedMessage = bodyJson.optString(Error.LOCALIZED_MESSAGE, null);
        } catch (JSONException e) {
            e.printStackTrace();
            code = Custom.INVALID_ERROR_RESPONSE;
        }
    }

    public int getErrorStringRes() {

        if (response == null) {
            // Return as the wrapped error.
            // We only have two constructors, so this cast is safe.
            return getErrorStringRes(this.getCause());
        }

        Integer StringRes = ERROR_CODE_STRING_RES_MAP.get(code);
        return StringRes != 0 ? StringRes : R.string.api_error_unknown;
    }

    public static int getErrorStringRes(Throwable error) {
        if (error instanceof ResponseConversionException) {
            return R.string.api_error_parse;
        } else if (error instanceof AuthenticationException) {
            return R.string.api_error_auth_failure;
        } else if (error instanceof SocketTimeoutException) {
            return R.string.api_error_timeout;
        } else if (error instanceof UnknownHostException) {
            return R.string.api_error_no_connection;
        } else if (error instanceof IOException) {
            return R.string.api_error_network;
        } else if (error instanceof ApiError) {
            return ((ApiError) error).getErrorStringRes();
        } else {
            return R.string.api_error_unknown;
        }
    }

    public static String getErrorString(Throwable error, Context context) {
        return context.getString(getErrorStringRes(error));
    }

    @Override
    public String toString() {
        return "ApiError{" +
                "response=" + response +
                ", bodyString='" + bodyString + '\'' +
                ", bodyJson=" + bodyJson +
                ", code=" + code +
                ", localizedMessage='" + localizedMessage + '\'' +
                ", message='" + message + '\'' +
                ", request='" + request + '\'' +
                '}';
    }
}
