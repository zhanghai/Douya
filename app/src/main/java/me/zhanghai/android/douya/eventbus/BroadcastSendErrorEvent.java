/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.eventbus;

public class BroadcastSendErrorEvent extends Event {

    public long writerId;

    public BroadcastSendErrorEvent(long writerId, Object source) {
        super(source);

        this.writerId = writerId;
    }
}
