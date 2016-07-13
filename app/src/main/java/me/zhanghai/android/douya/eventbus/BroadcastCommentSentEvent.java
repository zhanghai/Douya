/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.eventbus;

import me.zhanghai.android.douya.network.api.info.Comment;

public class BroadcastCommentSentEvent {

    public long broadcastId;

    public Comment comment;

    public BroadcastCommentSentEvent(long broadcastId, Comment comment) {
        this.broadcastId = broadcastId;
        this.comment = comment;
    }
}
