/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.link;

import android.content.Context;

public class NotImplementedManager {

    private NotImplementedManager() {}

    public static void sendBroadcast(String topic, Context context) {
        if (!FrodoBridge.sendBroadcast(topic, context)) {
            UrlHandler.open("https://www.douban.com/#isay-cont", context);
        }
    }
}
