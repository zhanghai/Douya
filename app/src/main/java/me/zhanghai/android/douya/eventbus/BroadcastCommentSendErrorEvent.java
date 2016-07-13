/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.eventbus;

public class BroadcastCommentSendErrorEvent {

    public long broadcastId;

    public BroadcastCommentSendErrorEvent(long broadcastId) {
        this.broadcastId = broadcastId;
    }
}
