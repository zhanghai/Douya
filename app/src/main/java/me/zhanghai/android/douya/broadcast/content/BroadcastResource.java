/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.broadcast.content;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import me.zhanghai.android.douya.content.ResourceFragment;
import me.zhanghai.android.douya.eventbus.BroadcastDeletedEvent;
import me.zhanghai.android.douya.eventbus.BroadcastUpdatedEvent;
import me.zhanghai.android.douya.eventbus.BroadcastWriteFinishedEvent;
import me.zhanghai.android.douya.eventbus.BroadcastWriteStartedEvent;
import me.zhanghai.android.douya.eventbus.EventBusUtils;
import me.zhanghai.android.douya.network.api.ApiError;
import me.zhanghai.android.douya.network.api.ApiRequest;
import me.zhanghai.android.douya.network.api.ApiService;
import me.zhanghai.android.douya.network.api.info.apiv2.Broadcast;
import me.zhanghai.android.douya.util.FragmentUtils;

public class BroadcastResource extends ResourceFragment<Broadcast, Broadcast> {

    private static final String KEY_PREFIX = BroadcastResource.class.getName() + '.';

    private static final String EXTRA_BROADCAST_ID = KEY_PREFIX + "broadcast_id";
    private static final String EXTRA_BROADCAST = KEY_PREFIX + "broadcast";

    private static final int BROADCAST_ID_INVALID = -1;

    private long mBroadcastId = BROADCAST_ID_INVALID;

    private Broadcast mExtraBroadcast;

    private static final String FRAGMENT_TAG_DEFAULT = BroadcastResource.class.getName();

    private static BroadcastResource newInstance(long broadcastId, Broadcast broadcast) {
        //noinspection deprecation
        return new BroadcastResource().setArguments(broadcastId, broadcast);
    }

    public static BroadcastResource attachTo(long broadcastId, Broadcast broadcast,
                                             Fragment fragment, String tag, int requestCode) {
        FragmentActivity activity = fragment.getActivity();
        BroadcastResource instance = FragmentUtils.findByTag(activity, tag);
        if (instance == null) {
            instance = newInstance(broadcastId, broadcast);
            instance.targetAt(fragment, requestCode);
            FragmentUtils.add(instance, activity, tag);
        }
        return instance;
    }

    public static BroadcastResource attachTo(long broadcastId, Broadcast broadcast,
                                             Fragment fragment) {
        return attachTo(broadcastId, broadcast, fragment, FRAGMENT_TAG_DEFAULT,
                REQUEST_CODE_INVALID);
    }

    /**
     * @deprecated Use {@code attachTo()} instead.
     */
    public BroadcastResource() {}

    protected BroadcastResource setArguments(long broadcastId, Broadcast broadcast) {
        Bundle arguments = FragmentUtils.ensureArguments(this);
        arguments.putLong(EXTRA_BROADCAST_ID, broadcastId);
        arguments.putParcelable(EXTRA_BROADCAST, broadcast);
        return this;
    }

    public long getBroadcastId() {
        ensureArguments();
        return mBroadcastId;
    }

    @Override
    public Broadcast get() {
        Broadcast broadcast = super.get();
        if (broadcast == null) {
            // Can be called before onCreate() is called.
            ensureArguments();
            broadcast = mExtraBroadcast;
        }
        return broadcast;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ensureArguments();
    }

    private void ensureArguments() {
        if (mBroadcastId != BROADCAST_ID_INVALID) {
            return;
        }
        Bundle arguments = getArguments();
        mExtraBroadcast = arguments.getParcelable(EXTRA_BROADCAST);
        if (mExtraBroadcast != null) {
            mBroadcastId = mExtraBroadcast.id;
        } else {
            mBroadcastId = arguments.getLong(EXTRA_BROADCAST_ID);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (has()) {
            Broadcast broadcast = get();
            setArguments(broadcast.id, broadcast);
        }
    }

    @Override
    protected ApiRequest<Broadcast> onCreateRequest() {
        return ApiService.getInstance().getBroadcast(mBroadcastId);
    }

    @Override
    protected void onLoadStarted() {
        getListener().onLoadBroadcastStarted(getRequestCode());
    }

    @Override
    protected void onLoadFinished(boolean successful, Broadcast response, ApiError error) {
        getListener().onLoadBroadcastFinished(getRequestCode());
        if (successful) {
            set(response);
            getListener().onBroadcastChanged(getRequestCode(), response);
            EventBusUtils.postAsync(new BroadcastUpdatedEvent(response, this));
        } else {
            getListener().onLoadBroadcastError(getRequestCode(), error);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBroadcastUpdated(BroadcastUpdatedEvent event) {

        if (event.isFromMyself(this)) {
            return;
        }

        boolean changed = false;
        if (event.broadcast.id == mBroadcastId) {
            set(event.broadcast);
            changed = true;
        } else if (has()) {
            Broadcast broadcast = get();
            if (broadcast.rebroadcastedBroadcast != null
                    && event.broadcast.id == broadcast.rebroadcastedBroadcast.id) {
                broadcast.rebroadcastedBroadcast = event.broadcast;
                changed = true;
            }
        }

        if (changed) {
            getListener().onBroadcastChanged(getRequestCode(), get());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBroadcastDeleted(BroadcastDeletedEvent event) {

        if (event.isFromMyself(this)) {
            return;
        }

        if (event.broadcastId == mBroadcastId) {
            set(null);
            getListener().onBroadcastRemoved(getRequestCode());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBroadcastWriteStarted(BroadcastWriteStartedEvent event) {

        if (event.isFromMyself(this)) {
            return;
        }

        // Only call listener when we have the data.
        if (event.broadcastId == mBroadcastId && has()) {
            getListener().onBroadcastWriteStarted(getRequestCode());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBroadcastWriteFinished(BroadcastWriteFinishedEvent event) {

        if (event.isFromMyself(this)) {
            return;
        }

        // Only call listener when we have the data.
        if (event.broadcastId == mBroadcastId && has()) {
            getListener().onBroadcastWriteFinished(getRequestCode());
        }
    }

    private Listener getListener() {
        return (Listener) getTarget();
    }

    public interface Listener {
        void onLoadBroadcastStarted(int requestCode);
        void onLoadBroadcastFinished(int requestCode);
        void onLoadBroadcastError(int requestCode, ApiError error);
        void onBroadcastChanged(int requestCode, Broadcast newBroadcast);
        void onBroadcastRemoved(int requestCode);
        void onBroadcastWriteStarted(int requestCode);
        void onBroadcastWriteFinished(int requestCode);
    }
}
