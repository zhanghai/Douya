/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.broadcast.content;

import android.content.Context;

import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.content.RequestResourceWriter;
import me.zhanghai.android.douya.eventbus.BroadcastCommentSendErrorEvent;
import me.zhanghai.android.douya.eventbus.BroadcastCommentSentEvent;
import me.zhanghai.android.douya.eventbus.EventBusUtils;
import me.zhanghai.android.douya.network.api.ApiError;
import me.zhanghai.android.douya.network.api.ApiRequest;
import me.zhanghai.android.douya.network.api.ApiService;
import me.zhanghai.android.douya.network.api.info.frodo.Comment;
import me.zhanghai.android.douya.util.LogUtils;
import me.zhanghai.android.douya.util.ToastUtils;

class SendBroadcastCommentWriter extends RequestResourceWriter<SendBroadcastCommentWriter, Comment> {

    private long mBroadcastId;
    private String  mComment;

    SendBroadcastCommentWriter(long broadcastId, String comment,
                               SendBroadcastCommentManager manager) {
        super(manager);

        mBroadcastId = broadcastId;
        mComment = comment;
    }

    public long getBroadcastId() {
        return mBroadcastId;
    }

    public String getComment() {
        return mComment;
    }

    @Override
    protected ApiRequest<Comment> onCreateRequest() {
        return ApiService.getInstance().sendBroadcastComment(mBroadcastId, mComment);
    }

    @Override
    public void onResponse(Comment response) {

        ToastUtils.show(R.string.broadcast_send_comment_successful, getContext());

        EventBusUtils.postAsync(new BroadcastCommentSentEvent(mBroadcastId, response, this));

        stopSelf();
    }

    @Override
    public void onErrorResponse(ApiError error) {

        LogUtils.e(error.toString());
        Context context = getContext();
        ToastUtils.show(context.getString(R.string.broadcast_send_comment_failed_format,
                ApiError.getErrorString(error, context)), context);

        EventBusUtils.postAsync(new BroadcastCommentSendErrorEvent(mBroadcastId, this));

        stopSelf();
    }
}
