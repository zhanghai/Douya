/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.eventbus;

public class BroadcastRebroadcastErrorEvent extends Event {

    public long broadcastId;

    public BroadcastRebroadcastErrorEvent(long broadcastId, Object source) {
        super(source);

        this.broadcastId = broadcastId;
    }
}
