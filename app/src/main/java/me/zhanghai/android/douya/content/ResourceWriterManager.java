/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.content;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import me.zhanghai.android.douya.util.CollectionUtils;

public class ResourceWriterManager<T extends ResourceWriter> {

    private ResourceWriterService mService;

    private List<T> mPendingWriters = new ArrayList<>();
    private List<T> mRunningWriters = new ArrayList<>();
    private List<T> mUnmodifiableWriters = Collections.unmodifiableList(
            CollectionUtils.union(mRunningWriters, mPendingWriters));

    public void onBind(ResourceWriterService service) {

        mService = service;

        movePendingWritersToRunning();
    }

    private void movePendingWritersToRunning() {
        Iterator<T> iterator = mPendingWriters.iterator();
        while (iterator.hasNext()) {
            T writer = iterator.next();
            iterator.remove();
            mRunningWriters.add(writer);
            writer.onStart();
        }
    }

    public void onUnbind() {

        removeRunningWriters();

        mService = null;
    }

    private void removeRunningWriters() {
        Iterator<T> iterator = mRunningWriters.iterator();
        while (iterator.hasNext()) {
            T writer = iterator.next();
            writer.onDestroy();
            iterator.remove();
        }
    }

    protected boolean isBound() {
        return mService != null;
    }

    public Context getContext() {
        return mService;
    }

    protected void add(T writer, Context context) {
        if (isBound()) {
            mRunningWriters.add(writer);
            writer.onStart();
        } else {
            mPendingWriters.add(writer);
            context.startService(ResourceWriterService.makeIntent(context));
        }
    }

    protected boolean remove(T writer) {
        if (mRunningWriters.contains(writer)) {
            writer.onDestroy();
            return mRunningWriters.remove(writer);
        } else {
            return mPendingWriters.remove(writer);
        }
    }

    public void stop(T writer) {
        if (!remove(writer)) {
            throw new IllegalStateException("stop() called with unknown writer");
        }
    }

    protected List<T> getWriters() {
        return mUnmodifiableWriters;
    }
}
