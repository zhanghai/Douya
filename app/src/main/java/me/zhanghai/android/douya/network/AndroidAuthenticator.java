/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;

import me.zhanghai.android.douya.account.util.AccountUtils;
import okhttp3.Request;
import okhttp3.Response;

public abstract class AndroidAuthenticator implements AuthenticationInterceptor.Authenticator {

    private Account mAccount;
    private String mAuthTokenType;
    private boolean mNotifyAuthFailure;

    public AndroidAuthenticator(Account account, String authTokenType, boolean notifyAuthFailure) {
        mAccount = account;
        mAuthTokenType = authTokenType;
        mNotifyAuthFailure = notifyAuthFailure;
    }

    @NonNull
    @Override
    public Request authenticate(Request request) throws IOException {
        String authToken = getAuthToken();
        return request.newBuilder()
                .header(Http.Headers.AUTHORIZATION, Http.Headers.makeBearerAuthorization(authToken))
                .build();
    }

    @Nullable
    @Override
    public Request retryAuthentication(Response response) throws IOException {
        if (!shouldRetryAuthentication(response)) {
            return null;
        }
        Request oldRequest = response.request();
        String oldAuthorization = oldRequest.header(Http.Headers.AUTHORIZATION);
        String oldAuthToken = Http.Headers.getTokenFromBearerAuthorization(oldAuthorization);
        if (oldAuthToken != null) {
            invalidateAuthToken(oldAuthToken);
        }
        return authenticate(oldRequest);
    }

    public abstract boolean shouldRetryAuthentication(Response response);

    private String getAuthToken() throws IOException {
        AccountManagerFuture<Bundle> future = AccountUtils.getAccountManager().getAuthToken(
                mAccount, mAuthTokenType, null, mNotifyAuthFailure, null, null);
        Bundle result;
        try {
            result = future.getResult();
        } catch (Exception e) {
            throw new AuthenticationException("Error when retrieving auth token", e);
        }
        String authToken = null;
        if (future.isDone() && !future.isCancelled()) {
            if (result.containsKey(AccountManager.KEY_INTENT)) {
                Intent intent = result.getParcelable(AccountManager.KEY_INTENT);
                throw new AuthenticationException("Got Intent when retrieving auth token: "
                        + intent);
            }
            authToken = result.getString(AccountManager.KEY_AUTHTOKEN);
        }
        if (authToken == null) {
            throw new AuthenticationException("Got null auth token for type: " + mAuthTokenType);
        }
        return authToken;
    }

    private void invalidateAuthToken(String authToken) {
        AccountUtils.getAccountManager().invalidateAuthToken(mAccount.type, authToken);
    }
}
