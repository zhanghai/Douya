/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.broadcast.content;

import android.accounts.Account;
import android.content.Context;
import android.os.Handler;

import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import me.zhanghai.android.douya.network.api.info.frodo.Broadcast;
import me.zhanghai.android.douya.util.Callback;
import me.zhanghai.android.douya.util.DiskCacheHelper;

public class HomeBroadcastListCache {

    public static final int MAX_LIST_SIZE = 20;

    private static final String KEY_PREFIX = HomeBroadcastListCache.class.getName();

    public static void get(Account account, Handler handler, Callback<List<Broadcast>> callback,
                           Context context) {
        DiskCacheHelper.getGson(getKeyForAccount(account), new TypeToken<List<Broadcast>>() {},
                handler, callback, context);
    }

    public static void put(Account account, List<Broadcast> broadcastList, Context context) {
        if (broadcastList.size() > MAX_LIST_SIZE) {
            broadcastList = broadcastList.subList(0, MAX_LIST_SIZE);
        }
        // NOTE: Defend against ConcurrentModificationException.
        DiskCacheHelper.putGson(getKeyForAccount(account), new ArrayList<>(broadcastList),
                new TypeToken<List<Broadcast>>() {}, context);
    }

    private static String getKeyForAccount(Account account) {
        return KEY_PREFIX + '@' + account.name;
    }
}
