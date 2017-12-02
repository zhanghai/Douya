/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.eventbus;

public class DoulistDeletedEvent extends Event {

    public long doulistId;

    public DoulistDeletedEvent(long doulistId, Object source) {
        super(source);

        this.doulistId = doulistId;
    }
}
