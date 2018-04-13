/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.broadcast.content;

import android.content.Context;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.content.RequestResourceWriter;
import me.zhanghai.android.douya.content.ResourceWriterManager;
import me.zhanghai.android.douya.eventbus.BroadcastRebroadcastErrorEvent;
import me.zhanghai.android.douya.eventbus.BroadcastRebroadcastedEvent;
import me.zhanghai.android.douya.eventbus.BroadcastUpdatedEvent;
import me.zhanghai.android.douya.eventbus.BroadcastWriteFinishedEvent;
import me.zhanghai.android.douya.eventbus.BroadcastWriteStartedEvent;
import me.zhanghai.android.douya.eventbus.EventBusUtils;
import me.zhanghai.android.douya.network.api.ApiError;
import me.zhanghai.android.douya.network.api.ApiRequest;
import me.zhanghai.android.douya.network.api.ApiService;
import me.zhanghai.android.douya.network.api.info.frodo.Broadcast;
import me.zhanghai.android.douya.util.LogUtils;
import me.zhanghai.android.douya.util.ToastUtils;

class RebroadcastBroadcastWriter
        extends RequestResourceWriter<RebroadcastBroadcastWriter, Broadcast> {

    private long mBroadcastId;
    private Broadcast mBroadcast;
    private String mText;

    private RebroadcastBroadcastWriter(long broadcastId, Broadcast broadcast, String text,
                                       ResourceWriterManager<RebroadcastBroadcastWriter> manager) {
        super(manager);

        mBroadcastId = broadcastId;
        mBroadcast = broadcast;
        mText = text;

        EventBusUtils.register(this);
    }

    RebroadcastBroadcastWriter(long broadcastId, String text,
                               ResourceWriterManager<RebroadcastBroadcastWriter> manager) {
        this(broadcastId, null, text, manager);
    }

    RebroadcastBroadcastWriter(Broadcast broadcast, String text,
                               ResourceWriterManager<RebroadcastBroadcastWriter> manager) {
        this(broadcast.id, broadcast, text, manager);
    }

    public long getBroadcastId() {
        return mBroadcastId;
    }

    public String getText() {
        return mText;
    }

    @Override
    protected ApiRequest<Broadcast> onCreateRequest() {
        return ApiService.getInstance().rebroadcastBroadcast(mBroadcastId, mText);
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

        ToastUtils.show(R.string.broadcast_rebroadcast_successful, getContext());

        // Post BroadcastRebroadcastedEvent first so that RebroadcastBroadcastFragment will set
        // mRebroadcasted to true before the following BroadcastUpdatedEvents.
        EventBusUtils.postAsync(new BroadcastRebroadcastedEvent(mBroadcastId, response, this));

        if (response.parentBroadcast != null) {
            EventBusUtils.postAsync(new BroadcastUpdatedEvent(response.parentBroadcast, this));
        }
        EventBusUtils.postAsync(new BroadcastUpdatedEvent(response.rebroadcastedBroadcast, this));

        stopSelf();
    }

    @Override
    public void onErrorResponse(ApiError error) {

        LogUtils.e(error.toString());
        Context context = getContext();
        ToastUtils.show(context.getString(R.string.broadcast_rebroadcast_failed_format,
                ApiError.getErrorString(error, context)), context);

        // Must notify to reset pending status. Off-screen items also needs to be invalidated.
        EventBusUtils.postAsync(new BroadcastWriteFinishedEvent(mBroadcastId, this));

        EventBusUtils.postAsync(new BroadcastRebroadcastErrorEvent(mBroadcastId, this));

        stopSelf();
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onBroadcastUpdated(BroadcastUpdatedEvent event) {

        if (event.isFromMyself(this)) {
            return;
        }

        Broadcast updatedBroadcast = event.update(mBroadcastId, mBroadcast, this);
        if (updatedBroadcast != null) {
            mBroadcast = updatedBroadcast;
        }
    }
}
