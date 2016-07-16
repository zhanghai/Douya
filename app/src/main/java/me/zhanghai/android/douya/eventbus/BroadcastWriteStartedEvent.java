/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.eventbus;

public class BroadcastWriteStartedEvent extends Event {

    public long broadcastId;

    public BroadcastWriteStartedEvent(long broadcastId, Object source) {
        super(source);

        this.broadcastId = broadcastId;
    }
}
