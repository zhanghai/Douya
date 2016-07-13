/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.eventbus;

public class CommentDeletedEvent {

    public long commentId;

    public CommentDeletedEvent(long commentId) {
        this.commentId = commentId;
    }
}
