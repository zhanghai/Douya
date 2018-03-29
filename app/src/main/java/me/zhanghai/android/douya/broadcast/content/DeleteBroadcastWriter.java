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
import me.zhanghai.android.douya.eventbus.BroadcastDeletedEvent;
import me.zhanghai.android.douya.eventbus.BroadcastUpdatedEvent;
import me.zhanghai.android.douya.eventbus.EventBusUtils;
import me.zhanghai.android.douya.network.api.ApiError;
import me.zhanghai.android.douya.network.api.ApiRequest;
import me.zhanghai.android.douya.network.api.ApiService;
import me.zhanghai.android.douya.network.api.info.frodo.Broadcast;
import me.zhanghai.android.douya.util.LogUtils;
import me.zhanghai.android.douya.util.ToastUtils;

class DeleteBroadcastWriter extends RequestResourceWriter<DeleteBroadcastWriter, Void> {

    private long mBroadcastId;
    private Broadcast mBroadcast;

    DeleteBroadcastWriter(long broadcastId, Broadcast broadcast,
                          ResourceWriterManager<DeleteBroadcastWriter> manager) {
        super(manager);

        mBroadcastId = broadcastId;
        mBroadcast = broadcast;

        EventBusUtils.register(this);
    }

    DeleteBroadcastWriter(long broadcastId, ResourceWriterManager<DeleteBroadcastWriter> manager) {
        this(broadcastId, null, manager);
    }

    DeleteBroadcastWriter(Broadcast broadcast,
                          ResourceWriterManager<DeleteBroadcastWriter> manager) {
        this(broadcast.id, broadcast, manager);
    }

    public long getBroadcastId() {
        return mBroadcastId;
    }

    @Override
    protected ApiRequest<Void> onCreateRequest() {
        return ApiService.getInstance().deleteBroadcast(mBroadcastId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        EventBusUtils.unregister(this);
    }

    @Override
    public void onResponse(Void response) {

        ToastUtils.show(mBroadcast != null && mBroadcast.isSimpleRebroadcast() ?
                R.string.broadcast_unrebroadcast_successful : R.string.broadcast_delete_successful,
                getContext());

        if (mBroadcast != null) {
            Broadcast rebroadcastedBroadcast;
            if (mBroadcast.parentBroadcast != null) {
                rebroadcastedBroadcast = mBroadcast.parentBroadcast;
            } else if (mBroadcast.getParentBroadcastId() != null) {
                rebroadcastedBroadcast = null;
            } else {
                rebroadcastedBroadcast = mBroadcast.rebroadcastedBroadcast;
            }
            if (rebroadcastedBroadcast != null) {
                --rebroadcastedBroadcast.rebroadcastCount;
                EventBusUtils.postAsync(new BroadcastUpdatedEvent(rebroadcastedBroadcast, this));
            }
        }
        EventBusUtils.postAsync(new BroadcastDeletedEvent(mBroadcastId, this));

        stopSelf();
    }

    @Override
    public void onErrorResponse(ApiError error) {

        LogUtils.e(error.toString());
        Context context = getContext();
        ToastUtils.show(context.getString(mBroadcast != null && mBroadcast.isSimpleRebroadcast() ?
                        R.string.broadcast_unrebroadcast_failed_format
                        : R.string.broadcast_delete_failed_format,
                ApiError.getErrorString(error, context)), context);

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
