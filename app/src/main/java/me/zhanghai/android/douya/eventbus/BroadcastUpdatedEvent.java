/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.eventbus;

import me.zhanghai.android.douya.network.api.info.frodo.Broadcast;

public class BroadcastUpdatedEvent extends Event {

    public Broadcast broadcast;

    public BroadcastUpdatedEvent(Broadcast broadcast, Object source) {
        super(source);

        this.broadcast = broadcast;
    }
}
