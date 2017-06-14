/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.content;

import android.content.Context;

import me.zhanghai.android.douya.network.api.ApiRequest;

public abstract class ResourceWriter<W extends ResourceWriter, T> implements ApiRequest.Callback<T> {

    private ResourceWriterManager<W> mManager;

    private ApiRequest<T> mRequest;

    public ResourceWriter(ResourceWriterManager<W> manager) {
        mManager = manager;
    }

    public void onStart() {
        mRequest = onCreateRequest();
        mRequest.enqueue(this);
    }

    protected abstract ApiRequest<T> onCreateRequest();

    public void onDestroy() {
        mRequest.cancel();
        mRequest = null;
    }

    protected Context getContext() {
        return mManager.getContext();
    }

    protected void stopSelf() {
        //noinspection unchecked
        mManager.stop((W) this);
    }
}
