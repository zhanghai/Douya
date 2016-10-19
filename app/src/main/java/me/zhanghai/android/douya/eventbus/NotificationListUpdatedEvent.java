/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.eventbus;

import android.accounts.Account;

import java.util.List;

import me.zhanghai.android.douya.network.api.info.frodo.Notification;

public class NotificationListUpdatedEvent extends Event {

    public Account account;
    public List<Notification> notificationList;

    public NotificationListUpdatedEvent(Account account, List<Notification> notificationList,
                                        Object source) {
        super(source);

        this.account = account;
        this.notificationList = notificationList;
    }
}
