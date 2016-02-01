/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Authenticator;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpStack;

import java.io.File;

import me.zhanghai.android.douya.account.info.AccountContract;
import me.zhanghai.android.douya.account.util.AccountUtils;

public class Volley {

    private static final Object INSTANCE_LOCK = new Object();
    private static Volley sInstance;

    private Authenticator mAuthenticator;
    private RequestQueue mRequestQueue;

    private Volley(Context context) {

        context = context.getApplicationContext();

        notifyActiveAccountChanged(context);

        mRequestQueue = newRequestQueue(context);
        mRequestQueue.start();
    }

    public static Volley peekInstance() {
        return sInstance;
    }

    public static Volley getInstance(Context context) {
        synchronized (INSTANCE_LOCK) {
            if (sInstance == null) {
                sInstance = new Volley(context);
            }
        }
        return sInstance;
    }

    /**
     * @see com.android.volley.toolbox.Volley#newRequestQueue(Context, HttpStack, int)
     */
    private static RequestQueue newRequestQueue(Context context) {
        RequestQueue queue = new RequestQueue(new DiskBasedCache(new File(context.getCacheDir(),
                "volley")), new BasicNetwork(new HurlStack()));
        queue.start();
        return queue;
    }

    public void notifyActiveAccountChanged(Context context) {
        context = context.getApplicationContext();
        mAuthenticator = new SynchronizedAndroidAuthenticator(context,
                AccountUtils.getActiveAccount(context), AccountContract.AUTH_TOKEN_TYPE, true);
    }

    public Authenticator getAuthenticator() {
        return mAuthenticator;
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
}
