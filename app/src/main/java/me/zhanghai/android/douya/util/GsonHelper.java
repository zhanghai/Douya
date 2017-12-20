/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import me.zhanghai.android.douya.network.api.info.frodo.BaseTimelineItem;
import me.zhanghai.android.douya.network.api.info.frodo.CollectableItem;
import me.zhanghai.android.douya.network.api.info.frodo.CompleteCollectableItem;

public class GsonHelper {

    private static final Gson GSON = new GsonBuilder()
            .serializeNulls()
            .registerTypeAdapter(BaseTimelineItem.class, new BaseTimelineItem.Deserializer())
            .registerTypeAdapter(CollectableItem.class, new CollectableItem.Deserializer())
            .registerTypeAdapter(CompleteCollectableItem.class,
                    new CompleteCollectableItem.Deserializer())
            // TODO
            .create();

    private GsonHelper() {}

    public static Gson get() {
        return GSON;
    }
}
