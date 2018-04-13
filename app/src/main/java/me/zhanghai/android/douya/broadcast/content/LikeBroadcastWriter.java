/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.broadcast.content;

import android.content.Context;

import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.content.RequestResourceWriter;
import me.zhanghai.android.douya.content.ResourceWriterManager;
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

class LikeBroadcastWriter extends RequestResourceWriter<LikeBroadcastWriter, Broadcast> {

    private long mBroadcastId;
    private boolean mLike;

    LikeBroadcastWriter(long broadcastId, boolean like,
                        ResourceWriterManager<LikeBroadcastWriter> manager) {
        super(manager);

        mBroadcastId = broadcastId;
        mLike = like;
    }

    public long getBroadcastId() {
        return mBroadcastId;
    }

    public boolean isLike() {
        return mLike;
    }

    @Override
    protected ApiRequest<Broadcast> onCreateRequest() {
        return ApiService.getInstance().likeBroadcast(mBroadcastId, mLike);
    }

    @Override
    public void onStart() {
        super.onStart();

        EventBusUtils.postAsync(new BroadcastWriteStartedEvent(mBroadcastId, this));
    }

    @Override
    public void onResponse(Broadcast response) {

        ToastUtils.show(mLike ? R.string.broadcast_like_successful
                : R.string.broadcast_unlike_successful, getContext());

        EventBusUtils.postAsync(new BroadcastUpdatedEvent(response, this));

        stopSelf();
    }

    @Override
    public void onErrorResponse(ApiError error) {

        LogUtils.e(error.toString());
        Context context = getContext();
        ToastUtils.show(context.getString(mLike ? R.string.broadcast_like_failed_format
                        : R.string.broadcast_unlike_failed_format,
                ApiError.getErrorString(error, context)), context);

        // Must notify to reset pending status. Off-screen items also needs to be invalidated.
        EventBusUtils.postAsync(new BroadcastWriteFinishedEvent(mBroadcastId, this));

        stopSelf();
    }
}
