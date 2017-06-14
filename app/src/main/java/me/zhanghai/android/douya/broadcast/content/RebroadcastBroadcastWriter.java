/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.broadcast.content;

import android.content.Context;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.content.ResourceWriter;
import me.zhanghai.android.douya.content.ResourceWriterManager;
import me.zhanghai.android.douya.eventbus.BroadcastDeletedEvent;
import me.zhanghai.android.douya.eventbus.BroadcastUpdatedEvent;
import me.zhanghai.android.douya.eventbus.BroadcastWriteFinishedEvent;
import me.zhanghai.android.douya.eventbus.BroadcastWriteStartedEvent;
import me.zhanghai.android.douya.eventbus.EventBusUtils;
import me.zhanghai.android.douya.network.api.ApiContract.Response.Error.Codes;
import me.zhanghai.android.douya.network.api.ApiError;
import me.zhanghai.android.douya.network.api.ApiRequest;
import me.zhanghai.android.douya.network.api.ApiService;
import me.zhanghai.android.douya.network.api.info.apiv2.Broadcast;
import me.zhanghai.android.douya.util.LogUtils;
import me.zhanghai.android.douya.util.ToastUtils;

class RebroadcastBroadcastWriter extends ResourceWriter<RebroadcastBroadcastWriter, Broadcast> {

    private long mBroadcastId;
    private Broadcast mBroadcast;
    private boolean mRebroadcast;

    private RebroadcastBroadcastWriter(long broadcastId, Broadcast broadcast, boolean rebroadcast,
                                       ResourceWriterManager<RebroadcastBroadcastWriter> manager) {
        super(manager);

        mBroadcastId = broadcastId;
        mBroadcast = broadcast;
        mRebroadcast = rebroadcast;

        EventBusUtils.register(this);
    }

    RebroadcastBroadcastWriter(long broadcastId, boolean rebroadcast,
                               ResourceWriterManager<RebroadcastBroadcastWriter> manager) {
        this(broadcastId, null, rebroadcast, manager);
    }

    RebroadcastBroadcastWriter(Broadcast broadcast, boolean rebroadcast,
                               ResourceWriterManager<RebroadcastBroadcastWriter> manager) {
        this(broadcast.id, broadcast, rebroadcast, manager);
    }

    public long getBroadcastId() {
        return mBroadcastId;
    }

    public boolean isRebroadcast() {
        return mRebroadcast;
    }

    @Override
    protected ApiRequest<Broadcast> onCreateRequest() {
        return ApiService.getInstance().rebroadcastBroadcast(mBroadcastId, mRebroadcast);
    }

    @Override
    public void onStart() {
        super.onStart();

        EventBusUtils.postAsync(new BroadcastWriteStartedEvent(mBroadcastId, this));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        EventBusUtils.unregister(this);
    }

    @Override
    public void onResponse(Broadcast response) {

        ToastUtils.show(mRebroadcast ? R.string.broadcast_rebroadcast_successful
                : R.string.broadcast_unrebroadcast_successful, getContext());

        if (!mRebroadcast) {
            // Delete the rebroadcast broadcast by user. Must be done before we
            // update the broadcast so that we can retrieve rebroadcastId for the
            // old one.
            if (mBroadcast != null && mBroadcast.rebroadcastId != null) {
                EventBusUtils.postAsync(new BroadcastDeletedEvent(mBroadcast.rebroadcastId, this));
            }
        }

        EventBusUtils.postAsync(new BroadcastUpdatedEvent(response, this));

        stopSelf();
    }

    @Override
    public void onErrorResponse(ApiError error) {

        LogUtils.e(error.toString());
        Context context = getContext();
        ToastUtils.show(context.getString(mRebroadcast ? R.string.broadcast_rebroadcast_failed_format
                        : R.string.broadcast_unrebroadcast_failed_format,
                ApiError.getErrorString(error, context)), context);

        boolean notified = false;
        if (mBroadcast != null && error instanceof ApiError) {
            // Correct our local state if needed.
            ApiError apiError = (ApiError) error;
            Boolean shouldBeRebroadcasted = null;
            if (apiError.code == Codes.RebroadcastBroadcast.ALREADY_REBROADCASTED) {
                shouldBeRebroadcasted = true;
            } else if (apiError.code == Codes.RebroadcastBroadcast.NOT_REBROADCASTED_YET) {
                shouldBeRebroadcasted = false;
            }
            if (shouldBeRebroadcasted != null) {
                mBroadcast.fixRebroadcasted(shouldBeRebroadcasted);
                EventBusUtils.postAsync(new BroadcastUpdatedEvent(mBroadcast, this));
                notified = true;
            }
        }
        if (!notified) {
            // Must notify to reset pending status. Off-screen items also needs to be invalidated.
            EventBusUtils.postAsync(new BroadcastWriteFinishedEvent(mBroadcastId, this));
        }

        stopSelf();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBroadcastUpdated(BroadcastUpdatedEvent event) {

        if (event.isFromMyself(this)) {
            return;
        }

        if (event.broadcast.id == mBroadcast.id) {
            mBroadcast = event.broadcast;
        }
    }
}
