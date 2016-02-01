/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.eventbus;

public class BroadcastDeletedEvent {

    public long broadcastId;

    public BroadcastDeletedEvent(long broadcastId) {
        this.broadcastId = broadcastId;
    }
}
