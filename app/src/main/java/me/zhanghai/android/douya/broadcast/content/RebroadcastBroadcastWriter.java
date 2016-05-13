/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.broadcast.content;

import android.content.Context;
import android.support.annotation.Keep;

import com.android.volley.VolleyError;

import de.greenrobot.event.EventBus;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.content.ResourceWriter;
import me.zhanghai.android.douya.content.ResourceWriterManager;
import me.zhanghai.android.douya.eventbus.BroadcastDeletedEvent;
import me.zhanghai.android.douya.eventbus.BroadcastUpdatedEvent;
import me.zhanghai.android.douya.eventbus.BroadcastWriteFinishedEvent;
import me.zhanghai.android.douya.eventbus.BroadcastWriteStartedEvent;
import me.zhanghai.android.douya.network.Request;
import me.zhanghai.android.douya.network.api.ApiContract.Response.Error.Codes;
import me.zhanghai.android.douya.network.api.ApiError;
import me.zhanghai.android.douya.network.api.ApiRequests;
import me.zhanghai.android.douya.network.api.info.Broadcast;
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

        EventBus.getDefault().register(this);
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
    protected Request<Broadcast> onCreateRequest() {
        return ApiRequests.newRebroadcastBroadcastRequest(mBroadcastId, mRebroadcast, getContext());
    }

    @Override
    public void onStart() {
        super.onStart();

        EventBus.getDefault().post(new BroadcastWriteStartedEvent(mBroadcastId));
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
                EventBus.getDefault().post(new BroadcastDeletedEvent(mBroadcast.rebroadcastId));
            }
        }

        EventBus.getDefault().post(new BroadcastUpdatedEvent(response));

        stopSelf();
    }

    @Override
    public void onErrorResponse(VolleyError error) {

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
                EventBus.getDefault().post(new BroadcastUpdatedEvent(mBroadcast));
                notified = true;
            }
        }
        if (!notified) {
            // Must notify to reset pending status. Off-screen items also needs to be invalidated.
            EventBus.getDefault().post(new BroadcastWriteFinishedEvent(mBroadcastId));
        }

        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }

    @Keep
    public void onEventMainThread(BroadcastUpdatedEvent event) {
        Broadcast updatedBroadcast = event.broadcast;
        if (updatedBroadcast.id == mBroadcast.id) {
            mBroadcast = updatedBroadcast;
        }
    }
}
