/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Authenticator;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpStack;

import java.io.File;

import me.zhanghai.android.douya.DouyaApplication;
import me.zhanghai.android.douya.account.info.AccountContract;
import me.zhanghai.android.douya.account.util.AccountUtils;
import me.zhanghai.android.douya.network.api.ApiRequest;

public class Volley {

    private static final Volley INSTANCE = new Volley();

    private Authenticator mApiV2Authenticator;
    private Authenticator mFrodoAuthenticator;
    private RequestQueue mRequestQueue;

    public static Volley getInstance() {
        return INSTANCE;
    }

    private Volley() {
        createAuthenticatorsForActiveAccount();
        createAndStartRequestQueue();
    }

    private void createAuthenticatorsForActiveAccount() {
        Context context = DouyaApplication.getInstance();
        mApiV2Authenticator = new SynchronizedAndroidAuthenticator(context,
                AccountUtils.getActiveAccount(), AccountContract.AUTH_TOKEN_TYPE_API_V2, true);
        mFrodoAuthenticator = new SynchronizedAndroidAuthenticator(context,
                AccountUtils.getActiveAccount(), AccountContract.AUTH_TOKEN_TYPE_FRODO, true);
    }

    public void notifyActiveAccountChanged() {
        createAuthenticatorsForActiveAccount();
    }

    public Authenticator getAuthenticator(String authTokenType) {
        switch (authTokenType) {
            case AccountContract.AUTH_TOKEN_TYPE_API_V2:
                return mApiV2Authenticator;
            case AccountContract.AUTH_TOKEN_TYPE_FRODO:
                return mFrodoAuthenticator;
            default:
                throw new IllegalArgumentException("Unknown authTokenType: " + authTokenType);
        }
    }

    /**
     * @see com.android.volley.toolbox.Volley#newRequestQueue(Context, HttpStack)
     */
    private void createAndStartRequestQueue() {
        mRequestQueue = new RequestQueue(new DiskBasedCache(new File(
                DouyaApplication.getInstance().getCacheDir(), "volley")), new BasicNetwork(
                new HurlStack()));
        mRequestQueue.start();
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    public <T> Request<T> addToRequestQueue(Request<T> request) {
        mRequestQueue.add(request);
        return request;
    }

    public void cancelRequests(Object tag) {
        mRequestQueue.cancelAll(tag);
    }

    public void cancelApiRequests() {
        mRequestQueue.cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(com.android.volley.Request<?> request) {
                return request instanceof ApiRequest;
            }
        });
    }
}
