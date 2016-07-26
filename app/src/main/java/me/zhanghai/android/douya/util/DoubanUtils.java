/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util;

import me.zhanghai.android.douya.network.api.info.apiv2.User;

public class DoubanUtils {

    private DoubanUtils() {}

    public static String getAtUserString(String userIdOrUid) {
        return '@' + userIdOrUid + ' ';
    }

    public static String getAtUserString(User user) {
        //noinspection deprecation
        return getAtUserString(user.uid);
    }
}
