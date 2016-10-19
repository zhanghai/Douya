/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.eventbus;

public class NotificationDeletedEvent extends Event {

    public long notificationId;

    public NotificationDeletedEvent(long notificationId, Object source) {
        super(source);

        this.notificationId = notificationId;
    }
}
