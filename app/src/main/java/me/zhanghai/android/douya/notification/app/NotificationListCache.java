/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.notification.app;

import android.content.Context;
import android.os.Handler;

import com.google.gson.reflect.TypeToken;

import java.util.List;

import me.zhanghai.android.douya.network.api.info.frodo.Notification;
import me.zhanghai.android.douya.util.Callback;
import me.zhanghai.android.douya.util.DiskCacheHelper;

public class NotificationListCache {

    private static final int MAX_LIST_SIZE = 20;

    private static final String KEY = NotificationListCache.class.getName();

    public static void get(Handler handler, Callback<List<Notification>> callback,
                           Context context) {
        DiskCacheHelper.getGson(KEY, new TypeToken<List<Notification>>() {}, handler, callback,
                context);
    }

    public static void put(List<Notification> notificationList, Context context) {
        if (notificationList.size() > MAX_LIST_SIZE) {
            notificationList = notificationList.subList(0, MAX_LIST_SIZE);
        }
        DiskCacheHelper.putGson(KEY, notificationList, new TypeToken<List<Notification>>() {},
                context);
    }
}
