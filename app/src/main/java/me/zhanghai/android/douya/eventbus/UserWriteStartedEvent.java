/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.eventbus;

public class UserWriteStartedEvent extends Event {

    public String userIdOrUid;

    public UserWriteStartedEvent(String userIdOrUid, Object source) {
        super(source);

        this.userIdOrUid = userIdOrUid;
    }
}
