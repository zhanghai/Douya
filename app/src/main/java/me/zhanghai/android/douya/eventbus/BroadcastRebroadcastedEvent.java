/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.eventbus;

import me.zhanghai.android.douya.network.api.info.frodo.Broadcast;

public class BroadcastRebroadcastedEvent extends Event {

    public long broadcastId;

    public Broadcast rebroadcastBroadcast;

    public BroadcastRebroadcastedEvent(long broadcastId, Broadcast rebroadcastBroadcast,
                                       Object source) {
        super(source);

        this.broadcastId = broadcastId;
        this.rebroadcastBroadcast = rebroadcastBroadcast;
    }
}
