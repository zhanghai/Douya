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

        EventBus.getDefault().register(this);
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
    protected Request<Broadcast> onCreateRequest() {
        return ApiRequests.newLikeBroadcastRequest(mBroadcastId, mLike, getContext());
    }

    @Override
    public void onStart() {
        super.onStart();

        EventBus.getDefault().post(new BroadcastWriteStartedEvent(mBroadcastId));
    }

    @Override
    public void onResponse(Broadcast response) {

        ToastUtils.show(mLike ? R.string.broadcast_like_successful
                : R.string.broadcast_unlike_successful, getContext());

        EventBus.getDefault().post(new BroadcastUpdatedEvent(response));

        stopSelf();
    }

    @Override
    public void onErrorResponse(VolleyError error) {

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
