/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.eventbus;

public class BroadcastCommentDeletedEvent {

    public long broadcastId;
    public long commentId;

    public BroadcastCommentDeletedEvent(long broadcastId, long commentId) {
        this.broadcastId = broadcastId;
        this.commentId = commentId;
    }
}
