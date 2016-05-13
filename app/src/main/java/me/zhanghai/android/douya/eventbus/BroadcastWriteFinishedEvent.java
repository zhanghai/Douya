/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.eventbus;

public class BroadcastWriteFinishedEvent {

    public long broadcastId;

    public BroadcastWriteFinishedEvent(long broadcastId) {
        this.broadcastId = broadcastId;
    }
}
