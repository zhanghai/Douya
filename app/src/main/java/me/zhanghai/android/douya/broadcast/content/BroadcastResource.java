/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.broadcast.content;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

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
import me.zhanghai.android.douya.network.api.info.frodo.Broadcast;
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
            FragmentUtils.add(instance, activity, tag);
        }
        instance.setTarget(fragment, requestCode);
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
        FragmentUtils.getArgumentsBuilder(this)
                .putLong(EXTRA_BROADCAST_ID, broadcastId)
                .putParcelable(EXTRA_BROADCAST, broadcast);
        return this;
    }

    /**
     * @deprecated In most cases you may want to use {@link #getEffectiveBroadcastId()}.
     */
    public long getBroadcastId() {
        ensureArguments();
        return mBroadcastId;
    }

    /**
     * @deprecated In most cases you may want to use {@link #getEffectiveBroadcast()}.
     */
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

    /**
     * @deprecated In most cases you may want to use {@link #hasEffectiveBroadcast()}.
     */
    @Override
    public boolean has() {
        return super.has();
    }

    public boolean isEffectiveBroadcastId(long broadcastId) {
        return hasEffectiveBroadcast() && getEffectiveBroadcastId() == broadcastId;
    }

    public long getEffectiveBroadcastId() {
        // Can be called before onCreate() is called.
        if (!hasEffectiveBroadcast()) {
            throw new IllegalStateException("getEffectiveBroadcastId() called when broadcast is" +
                    " not yet loaded");
        }
        return getEffectiveBroadcast().id;
    }

    public Broadcast getEffectiveBroadcast() {
        // Can be called before onCreate() is called.
        //noinspection deprecation
        return has() ? get().getEffectiveBroadcast() : null;
    }

    public boolean hasEffectiveBroadcast() {
        return getEffectiveBroadcast() != null;
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

        //noinspection deprecation
        if (has()) {
            //noinspection deprecation
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
        if (successful) {
            set(response);
            getListener().onLoadBroadcastFinished(getRequestCode());
            getListener().onBroadcastChanged(getRequestCode(), response);
            EventBusUtils.postAsync(new BroadcastUpdatedEvent(response, this));
        } else {
            getListener().onLoadBroadcastFinished(getRequestCode());
            getListener().onLoadBroadcastError(getRequestCode(), error);
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onBroadcastUpdated(BroadcastUpdatedEvent event) {

        if (event.isFromMyself(this)) {
            return;
        }

        //noinspection deprecation
        Broadcast updatedBroadcast = event.update(mBroadcastId, get(), this);
        if (updatedBroadcast != null) {
            set(updatedBroadcast);
            getListener().onBroadcastChanged(getRequestCode(), updatedBroadcast);
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onBroadcastDeleted(BroadcastDeletedEvent event) {

        if (event.isFromMyself(this)) {
            return;
        }

        if (event.broadcastId == mBroadcastId) {
            set(null);
            getListener().onBroadcastRemoved(getRequestCode());
        } else //noinspection deprecation
        if (has()) {
            //noinspection deprecation
            Broadcast broadcast = get();
            //noinspection deprecation
            if (broadcast.isParentBroadcastId(event.broadcastId)) {
                // Same behavior as Frodo API.
                // FIXME: Won't reach here if another list shares this broadcast instance.
                broadcast.parentBroadcast = null;
                //noinspection deprecation
                broadcast.parentBroadcastId = null;
                getListener().onBroadcastChanged(getRequestCode(), broadcast);
            } else if (broadcast.rebroadcastedBroadcast != null
                    && broadcast.rebroadcastedBroadcast.id == event.broadcastId) {
                broadcast.rebroadcastedBroadcast.isDeleted = true;
                getListener().onBroadcastChanged(getRequestCode(), broadcast);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onBroadcastWriteStarted(BroadcastWriteStartedEvent event) {

        if (event.isFromMyself(this)) {
            return;
        }

        if (isEffectiveBroadcastId(event.broadcastId)) {
            getListener().onBroadcastWriteStarted(getRequestCode());
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onBroadcastWriteFinished(BroadcastWriteFinishedEvent event) {

        if (event.isFromMyself(this)) {
            return;
        }

        if (isEffectiveBroadcastId(event.broadcastId)) {
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
