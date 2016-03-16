/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.eventbus;

import me.zhanghai.android.douya.network.api.info.UserInfo;

public class UserInfoUpdatedEvent {

    public UserInfo userInfo;

    public UserInfoUpdatedEvent(UserInfo userInfo) {
        this.userInfo = userInfo;
    }
}
