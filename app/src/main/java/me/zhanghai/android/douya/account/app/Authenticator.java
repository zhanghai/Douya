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

import com.android.volley.ParseError;
import com.android.volley.VolleyError;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import me.zhanghai.android.douya.account.info.AccountContract;
import me.zhanghai.android.douya.account.ui.AuthenticatorActivity;
import me.zhanghai.android.douya.account.util.AccountUtils;
import me.zhanghai.android.douya.network.api.TokenRequest;

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
        Intent intent = new Intent(mContext, AuthenticatorActivity.class)
                .putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        if (!AccountUtils.hasAccount(mContext)) {
            intent.putExtra(AuthenticatorActivity.EXTRA_AUTH_MODE,
                    AuthenticatorActivity.AUTH_MODE_NEW);
        } else {
            intent.putExtra(AuthenticatorActivity.EXTRA_AUTH_MODE,
                    AuthenticatorActivity.AUTH_MODE_ADD);
        }
        return makeIntentBundle(intent);
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account,
                                     Bundle options) throws NetworkErrorException {
        Intent intent = new Intent(mContext, AuthenticatorActivity.class)
                .putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)
                .putExtra(AuthenticatorActivity.EXTRA_AUTH_MODE,
                        AuthenticatorActivity.AUTH_MODE_CONFIRM)
                .putExtra(AuthenticatorActivity.EXTRA_USERNAME, account.name);
        return makeIntentBundle(intent);
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account,
                               String authTokenType, Bundle options) {

        // Validate authTokenType.
        if (!TextUtils.equals(authTokenType, AccountContract.AUTH_TOKEN_TYPE)) {
            return makeErrorBundle(AccountManager.ERROR_CODE_BAD_ARGUMENTS,
                    "invalid authTokenType:" + authTokenType);
        }

        // NOTE:
        // Seems that System is not doing this checking for us, see:
        // http://stackoverflow.com/questions/11434621/login-in-twice-when-using-syncadapters
        //
        // Peek authToken from AccountManager first, will return null if failed.
        String authToken = AccountManager.get(mContext).peekAuthToken(account,
                AccountContract.AUTH_TOKEN_TYPE);

        if (TextUtils.isEmpty(authToken)) {
            String refreshToken = AccountUtils.getRefreshToken(account, mContext);
            if (!TextUtils.isEmpty(refreshToken)) {
                try {
                    TokenRequest.Result result = new TokenRequest(refreshToken)
                            .getResponse();
                    authToken = result.accessToken;
                    AccountUtils.setUserName(account, result.userName, mContext);
                    AccountUtils.setUserId(account, result.userId, mContext);
                    AccountUtils.setRefreshToken(account, result.refreshToken, mContext);
                } catch (InterruptedException | TimeoutException e) {
                    return makeErrorBundle(AccountManager.ERROR_CODE_NETWORK_ERROR, e);
                } catch (ExecutionException e) {
                    VolleyError error = (VolleyError) e.getCause();
                    if (error instanceof ParseError) {
                        return makeErrorBundle(AccountManager.ERROR_CODE_INVALID_RESPONSE, error);
                    } else if (error instanceof TokenRequest.Error) {
                        return makeErrorBundle(AccountManager.ERROR_CODE_BAD_AUTHENTICATION, error);
                    } else {
                        return makeErrorBundle(AccountManager.ERROR_CODE_NETWORK_ERROR, error);
                    }
                }
            }
        }

        if (TextUtils.isEmpty(authToken)) {
            String password = AccountUtils.getPassword(account, mContext);
            if (password == null) {
                return makeErrorBundle(AccountManager.ERROR_CODE_BAD_AUTHENTICATION,
                        "AccountManager.getPassword() returned null");
            }
            try {
                TokenRequest.Result result = new TokenRequest(account.name, password)
                        .getResponse();
                authToken = result.accessToken;
                AccountUtils.setUserName(account, result.userName, mContext);
                AccountUtils.setUserId(account, result.userId, mContext);
                AccountUtils.setRefreshToken(account, result.refreshToken, mContext);
            } catch (InterruptedException | TimeoutException e) {
                return makeErrorBundle(AccountManager.ERROR_CODE_NETWORK_ERROR, e);
            } catch (ExecutionException e) {
                VolleyError error = (VolleyError) e.getCause();
                if (error instanceof ParseError) {
                    return makeErrorBundle(AccountManager.ERROR_CODE_INVALID_RESPONSE, error);
                } else if (error instanceof TokenRequest.Error) {
                    return makeErrorBundle(AccountManager.ERROR_CODE_BAD_AUTHENTICATION, error);
                } else {
                    return makeErrorBundle(AccountManager.ERROR_CODE_NETWORK_ERROR, error);
                }
            }
        }

        if (TextUtils.isEmpty(authToken)) {
            // Should not happen, the only case should be when TokenRequest.Result.accessToken is
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
        Intent intent = new Intent(mContext, AuthenticatorActivity.class)
                .putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)
                .putExtra(AuthenticatorActivity.EXTRA_AUTH_MODE,
                        AuthenticatorActivity.AUTH_MODE_UPDATE)
                .putExtra(AuthenticatorActivity.EXTRA_USERNAME, account.name)
                .putExtra(AuthenticatorActivity.EXTRA_PASSWORD, AccountUtils.getPassword(account,
                        mContext));
        return makeIntentBundle(intent);
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account,
                              String[] features) throws NetworkErrorException {
        return null;
    }

    private Bundle makeIntentBundle(Intent intent) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
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
