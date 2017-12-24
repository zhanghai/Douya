/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import me.zhanghai.android.douya.network.api.info.frodo.BaseTimelineItem;
import me.zhanghai.android.douya.network.api.info.frodo.Broadcast;
import me.zhanghai.android.douya.network.api.info.frodo.CollectableItem;
import me.zhanghai.android.douya.network.api.info.frodo.CompleteCollectableItem;
import me.zhanghai.android.douya.network.api.info.frodo.Notification;

public class GsonHelper {

    public static final Gson GSON;
    public static final Gson GSON_NETWORK;
    static {
        GsonBuilder builder = new GsonBuilder()
                .serializeNulls()
                .registerTypeAdapter(BaseTimelineItem.class, new BaseTimelineItem.Deserializer())
                .registerTypeAdapter(CollectableItem.class, new CollectableItem.Deserializer())
                .registerTypeAdapter(CompleteCollectableItem.class,
                        new CompleteCollectableItem.Deserializer());
        GSON = builder.create();
        builder
                .registerTypeAdapter(Notification.class, new Notification.Deserializer())
                .registerTypeAdapter(Broadcast.class, new Broadcast.Deserializer());
        GSON_NETWORK = builder.create();
    }

    private GsonHelper() {}
}
