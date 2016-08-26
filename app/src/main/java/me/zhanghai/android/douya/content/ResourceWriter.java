/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.content;

import android.content.Context;

import com.android.volley.Response;

import me.zhanghai.android.douya.network.Request;
import me.zhanghai.android.douya.network.Volley;

public abstract class ResourceWriter<W extends ResourceWriter, T>
        implements Response.Listener<T>, Response.ErrorListener {

    private ResourceWriterManager<W> mManager;

    private Request<T> mRequest;

    public ResourceWriter(ResourceWriterManager<W> manager) {
        mManager = manager;
    }

    public void onStart() {
        mRequest = onCreateRequest();
        mRequest.setListener(this);
        mRequest.setErrorListener(this);
        Volley.getInstance().addToRequestQueue(mRequest);
    }

    protected abstract Request<T> onCreateRequest();

    public void onDestroy() {
        mRequest.cancel();
        mRequest.setListener(null);
        mRequest.setErrorListener(null);
    }

    protected Context getContext() {
        return mManager.getContext();
    }

    protected void stopSelf() {
        //noinspection unchecked
        mManager.stop((W) this);
    }
}
