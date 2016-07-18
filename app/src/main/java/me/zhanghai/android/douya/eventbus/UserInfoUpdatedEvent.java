/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.eventbus;

import me.zhanghai.android.douya.network.api.info.apiv2.UserInfo;

public class UserInfoUpdatedEvent extends Event {

    public UserInfo userInfo;

    public UserInfoUpdatedEvent(UserInfo userInfo, Object source) {
        super(source);

        this.userInfo = userInfo;
    }
}
