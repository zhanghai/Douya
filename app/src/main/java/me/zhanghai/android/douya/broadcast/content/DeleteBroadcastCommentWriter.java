/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.broadcast.content;

import android.content.Context;

import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.content.ResourceWriter;
import me.zhanghai.android.douya.eventbus.CommentDeletedEvent;
import me.zhanghai.android.douya.eventbus.EventBusUtils;
import me.zhanghai.android.douya.network.api.ApiError;
import me.zhanghai.android.douya.network.api.ApiRequest;
import me.zhanghai.android.douya.network.api.ApiService;
import me.zhanghai.android.douya.util.LogUtils;
import me.zhanghai.android.douya.util.ToastUtils;

class DeleteBroadcastCommentWriter extends ResourceWriter<DeleteBroadcastCommentWriter, Boolean> {

    private long mBroadcastId;
    private long mCommentId;

    DeleteBroadcastCommentWriter(long broadcastId, long commentId,
                                 DeleteBroadcastCommentManager manager) {
        super(manager);

        mBroadcastId = broadcastId;
        mCommentId = commentId;
    }

    public long getBroadcastId() {
        return mBroadcastId;
    }

    public long getCommentId() {
        return mCommentId;
    }

    @Override
    protected ApiRequest<Boolean> onCreateRequest() {
        return ApiService.getInstance().deleteBroadcastComment(mBroadcastId, mCommentId);
    }

    @Override
    public void onResponse(Boolean response) {

        ToastUtils.show(R.string.broadcast_comment_delete_successful, getContext());

        EventBusUtils.postAsync(new CommentDeletedEvent(mCommentId, this));

        stopSelf();
    }

    @Override
    public void onErrorResponse(ApiError error) {

        LogUtils.e(error.toString());
        Context context = getContext();
        ToastUtils.show(context.getString(R.string.broadcast_comment_delete_failed_format,
                ApiError.getErrorString(error, context)), context);

        stopSelf();
    }
}
