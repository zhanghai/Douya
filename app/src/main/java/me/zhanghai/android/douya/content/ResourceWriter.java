/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.content;

import android.app.Service;
import android.content.Context;

public abstract class ResourceWriter<W extends ResourceWriter> {

    protected ResourceWriterManager<W> mManager;

    public ResourceWriter(ResourceWriterManager<W> manager) {
        mManager = manager;
    }

    public abstract void onStart();

    public abstract void onDestroy();

    protected Context getContext() {
        return mManager.getContext();
    }

    protected Service getService() {
        return mManager.getService();
    }

    protected void stopSelf() {
        //noinspection unchecked
        mManager.stop((W) this);
    }
}
