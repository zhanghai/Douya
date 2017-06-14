/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.account.app;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import me.zhanghai.android.douya.account.info.AccountContract;
import me.zhanghai.android.douya.account.ui.AuthenticatorActivity;
import me.zhanghai.android.douya.account.util.AccountUtils;
import me.zhanghai.android.douya.account.util.AuthenticatorUtils;
import me.zhanghai.android.douya.network.api.ApiContract.Response.Error.Codes;
import me.zhanghai.android.douya.network.api.ApiError;
import me.zhanghai.android.douya.network.api.ApiService;
import me.zhanghai.android.douya.network.api.info.AuthenticationResponse;
import me.zhanghai.android.douya.util.LogUtils;
import me.zhanghai.android.douya.util.MoreTextUtils;

public class Authenticator extends AbstractAccountAuthenticator {

    private Context mContext;

    public Authenticator(Context context) {
        super(context);

        mContext = context;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType,
                             String authTokenType, String[] requiredFeatures, Bundle options)
            throws NetworkErrorException {
        return makeIntentBundle(AuthenticatorActivity.makeIntent(response,
                AccountUtils.hasAccount() ? AuthenticatorActivity.AUTH_MODE_ADD
                        : AuthenticatorActivity.AUTH_MODE_NEW, mContext));
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account,
                                     Bundle options) throws NetworkErrorException {
        return makeIntentBundle(AuthenticatorActivity.makeIntent(response,
                AuthenticatorActivity.AUTH_MODE_CONFIRM, account.name, mContext));
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account,
                               String authTokenType, Bundle options) {

        // Validate authTokenType.
        if (!MoreTextUtils.equalsAny(authTokenType, AccountContract.AUTH_TOKEN_TYPE_API_V2,
                AccountContract.AUTH_TOKEN_TYPE_FRODO)) {
            return makeErrorBundle(AccountManager.ERROR_CODE_BAD_ARGUMENTS,
                    "invalid authTokenType:" + authTokenType);
        }

        // NOTE:
        // Seems that System is not doing this checking for us, see:
        // http://stackoverflow.com/questions/11434621/login-in-twice-when-using-syncadapters
        //
        // Peek authToken from AccountManager first, will return null if failed.
        String authToken = AccountUtils.peekAuthToken(account, authTokenType);

        if (TextUtils.isEmpty(authToken)) {
            String refreshToken = AccountUtils.getRefreshToken(account, authTokenType);
            if (!TextUtils.isEmpty(refreshToken)) {
                try {
                    AuthenticationResponse authenticationResponse = ApiService.getInstance()
                            .authenticate(authTokenType, refreshToken).execute();
                    authToken = authenticationResponse.accessToken;
                    AccountUtils.setUserName(account, authenticationResponse.userName);
                    AccountUtils.setUserId(account, authenticationResponse.userId);
                    AccountUtils.setRefreshToken(account, authTokenType,
                            authenticationResponse.refreshToken);
                } catch (ApiError e) {
                    LogUtils.e(e.toString());
                    // Try again with XAuth afterwards.
                }
            }
        }

        if (TextUtils.isEmpty(authToken)) {
            String password = AccountUtils.getPassword(account);
            if (password == null) {
                return makeErrorBundle(AccountManager.ERROR_CODE_BAD_AUTHENTICATION,
                        "AccountManager.getPassword() returned null");
            }
            ApiService apiService = ApiService.getInstance();
            try {
                AuthenticationResponse authenticationResponse = apiService.authenticate(
                        authTokenType, account.name, password).execute();
                authToken = authenticationResponse.accessToken;
                AccountUtils.setUserName(account, authenticationResponse.userName);
                AccountUtils.setUserId(account, authenticationResponse.userId);
                AccountUtils.setRefreshToken(account, authTokenType,
                        authenticationResponse.refreshToken);
            } catch (ApiError e) {
                LogUtils.e(e.toString());
                if (e.bodyJson != null && e.code != Codes.Custom.INVALID_ERROR_RESPONSE) {
                    String errorString = ApiError.getErrorString(e, mContext);
                    switch (e.code) {
                        case Codes.Token.INVALID_APIKEY:
                        case Codes.Token.APIKEY_IS_BLOCKED:
                        case Codes.Token.INVALID_REQUEST_URI:
                        case Codes.Token.INVALID_CREDENCIAL2:
                        case Codes.Token.REQUIRED_PARAMETER_IS_MISSING:
                        case Codes.Token.CLIENT_SECRET_MISMATCH:
                            ApiService.getInstance().cancelApiRequests();
                            return makeFailureIntentBundle(
                                    AuthenticatorUtils.makeSetApiKeyIntent(mContext), errorString);
                        case Codes.Token.USERNAME_PASSWORD_MISMATCH:
                            ApiService.getInstance().cancelApiRequests();
                            return makeFailureIntentBundle(makeUpdateCredentialIntent(response,
                                    account), errorString);
                        case Codes.Token.INVALID_USER:
                        case Codes.Token.USER_HAS_BLOCKED:
                        case Codes.Token.USER_LOCKED:
                            ApiService.getInstance().cancelApiRequests();
                            return makeFailureIntentBundle(
                                    AuthenticatorUtils.makeWebsiteIntent(mContext), errorString);
                    }
                    return makeErrorBundle(AccountManager.ERROR_CODE_BAD_AUTHENTICATION,
                            errorString);
                } else if (e.response != null) {
                    return makeErrorBundle(AccountManager.ERROR_CODE_INVALID_RESPONSE, e);
                } else {
                    return makeErrorBundle(AccountManager.ERROR_CODE_NETWORK_ERROR, e);
                }
            }
        }

        if (TextUtils.isEmpty(authToken)) {
            // Should not happen, the only case should be when TokenRequest.Response.accessToken is
            // null.
            return makeErrorBundle(AccountManager.ERROR_CODE_INVALID_RESPONSE,
                    "authToken is still null");
        }

        // Return the result.
        Bundle result = makeSuccessBundle(account.name);
        result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
        return result;
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        // NOTE:
        // null means we don't support multiple authToken types, according to the example
        // SampleSyncAdapter.
        return null;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account,
                                    String authTokenType, Bundle options)
            throws NetworkErrorException {
        return makeIntentBundle(makeUpdateCredentialIntent(response, account));
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account,
                              String[] features) throws NetworkErrorException {
        return null;
    }

    private Intent makeUpdateCredentialIntent(AccountAuthenticatorResponse response,
                                              Account account) {
        return AuthenticatorActivity.makeIntent(response, AuthenticatorActivity.AUTH_MODE_UPDATE,
                account.name, mContext);
    }

    private Bundle makeIntentBundle(Intent intent) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    private Bundle makeFailureIntentBundle(Intent intent, String authFailedMessage) {
        Bundle bundle = makeIntentBundle(intent);
        bundle.putString(AccountManager.KEY_AUTH_FAILED_MESSAGE, authFailedMessage);
        return bundle;
    }

    private Bundle makeErrorBundle(int errorCode, String errorMessage) {
        Bundle bundle = new Bundle();
        bundle.putInt(AccountManager.KEY_ERROR_CODE, errorCode);
        bundle.putString(AccountManager.KEY_ERROR_MESSAGE, errorMessage);
        return bundle;
    }

    private Bundle makeErrorBundle(int errorCode, Throwable throwable) {
        return makeErrorBundle(errorCode, throwable.toString());
    }

    private Bundle makeSuccessBundle(String accountName) {
        Bundle bundle = new Bundle();
        bundle.putString(AccountManager.KEY_ACCOUNT_NAME, accountName);
        bundle.putString(AccountManager.KEY_ACCOUNT_TYPE, AccountContract.ACCOUNT_TYPE);
        return bundle;
    }
}
