/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network;

import android.accounts.Account;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Authenticator;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpStack;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import me.zhanghai.android.douya.DouyaApplication;
import me.zhanghai.android.douya.account.info.AccountContract;

public class Volley {

    private static final Volley INSTANCE = new Volley();

    private RequestQueue mRequestQueue;

    private final Object mAuthenticatorMapLock = new Object();
    private Map<Account, Authenticator> mAuthenticatorMap = new HashMap<>();

    public static Volley getInstance() {
        return INSTANCE;
    }

    private Volley() {
        mRequestQueue = newRequestQueue();
    }

    /**
     * @see com.android.volley.toolbox.Volley#newRequestQueue(Context, HttpStack, int)
     */
    private static RequestQueue newRequestQueue() {
        RequestQueue queue = new RequestQueue(new DiskBasedCache(new File(
                DouyaApplication.getInstance().getCacheDir(), "volley")), new BasicNetwork(
                new HurlStack()));
        queue.start();
        return queue;
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

    public Authenticator getAuthenticator(Account account) {
        Authenticator authenticator;
        synchronized (mAuthenticatorMapLock) {
            authenticator = mAuthenticatorMap.get(account);
            if (authenticator == null) {
                authenticator = new SynchronizedAndroidAuthenticator(DouyaApplication.getInstance(),
                        account, AccountContract.AUTH_TOKEN_TYPE, true);
                mAuthenticatorMap.put(account, authenticator);
            }
        }
        return authenticator;
    }
}
