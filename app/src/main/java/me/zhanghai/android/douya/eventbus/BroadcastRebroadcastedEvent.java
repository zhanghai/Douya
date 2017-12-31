/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.eventbus;

import me.zhanghai.android.douya.network.api.info.frodo.Broadcast;
import me.zhanghai.android.douya.network.api.info.frodo.Comment;

public class BroadcastRebroadcastedEvent extends Event {

    public long broadcastId;

    public Broadcast broadcast;

    public BroadcastRebroadcastedEvent(long broadcastId, Broadcast broadcast, Object source) {
        super(source);

        this.broadcastId = broadcastId;
        this.broadcast = broadcast;
    }
}
