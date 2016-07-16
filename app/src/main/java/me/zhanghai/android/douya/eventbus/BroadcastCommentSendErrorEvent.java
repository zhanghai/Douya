/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.eventbus;

public class BroadcastCommentSendErrorEvent extends Event {

    public long broadcastId;

    public BroadcastCommentSendErrorEvent(long broadcastId, Object source) {
        super(source);

        this.broadcastId = broadcastId;
    }
}
