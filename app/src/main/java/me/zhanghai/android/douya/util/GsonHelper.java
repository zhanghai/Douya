/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import me.zhanghai.android.douya.network.api.info.frodo.CollectableItem;

public class GsonHelper {

    private static final Gson GSON = new GsonBuilder()
            .serializeNulls()
            .registerTypeAdapter(new TypeToken<CollectableItem>() {}.getType(),
                    new CollectableItem.Deserializer())
            .registerTypeAdapter(new TypeToken<CollectableItem>() {}.getType(),
                    new CollectableItem.Serializer())
            // TODO
            .create();

    private GsonHelper() {}

    public static Gson get() {
        return GSON;
    }
}
