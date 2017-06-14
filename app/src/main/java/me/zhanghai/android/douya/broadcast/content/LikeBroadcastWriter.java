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

class LikeBroadcastWriter extends ResourceWriter<LikeBroadcastWriter, Broadcast> {

    private long mBroadcastId;
    private Broadcast mBroadcast;
    private boolean mLike;

    private LikeBroadcastWriter(long broadcastId, Broadcast broadcast, boolean like,
                                LikeBroadcastManager manager) {
        super(manager);

        mBroadcastId = broadcastId;
        mBroadcast = broadcast;
        mLike = like;

        EventBusUtils.register(this);
    }

    LikeBroadcastWriter(long broadcastId, boolean like, LikeBroadcastManager manager) {
        this(broadcastId, null, like, manager);
    }

    LikeBroadcastWriter(Broadcast broadcast, boolean like, LikeBroadcastManager manager) {
        this(broadcast.id, broadcast, like, manager);
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
    public void onDestroy() {
        super.onDestroy();

        EventBusUtils.unregister(this);
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

        boolean notified = false;
        if (mBroadcast != null && error instanceof ApiError) {
            // Correct our local state if needed.
            ApiError apiError = (ApiError) error;
            Boolean shouldBeLiked = null;
            if (apiError.code == Codes.LikeBroadcast.ALREADY_LIKED) {
                shouldBeLiked = true;
            } else if (apiError.code == Codes.LikeBroadcast.NOT_LIKED_YET) {
                shouldBeLiked = false;
            }
            if (shouldBeLiked != null) {
                mBroadcast.fixLiked(shouldBeLiked);
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
