/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.doumail.content;

import android.accounts.Account;
import android.content.Context;
import android.os.Handler;

import com.google.gson.reflect.TypeToken;

import me.zhanghai.android.douya.network.api.info.frodo.NotificationCount;
import me.zhanghai.android.douya.util.Callback;
import me.zhanghai.android.douya.util.DiskCacheHelper;

public class NotificationCountCache {

    private static final String KEY_PREFIX = NotificationCountCache.class.getName();

    public static void get(Account account, Handler handler, Callback<NotificationCount> callback,
                           Context context) {
        DiskCacheHelper.getGson(getKeyForAccount(account), NotificationCount.class, handler,
                callback, context);
    }

    public static void put(Account account, NotificationCount notificationList, Context context) {
        DiskCacheHelper.putGson(getKeyForAccount(account), notificationList,
                new TypeToken<NotificationCount>() {}, context);
    }

    private static String getKeyForAccount(Account account) {
        return KEY_PREFIX + '@' + account.name;
    }
}
