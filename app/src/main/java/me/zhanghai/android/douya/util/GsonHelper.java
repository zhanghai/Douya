/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

import java.lang.reflect.Type;

import me.zhanghai.android.douya.network.api.info.frodo.TimelineItem;
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
                .registerTypeAdapter(int.class, new IntegerDeserializer())
                .registerTypeAdapter(Integer.class, new IntegerDeserializer())
                .registerTypeAdapter(long.class, new LongDeserializer())
                .registerTypeAdapter(Long.class, new LongDeserializer())
                .registerTypeAdapter(float.class, new FloatDeserializer())
                .registerTypeAdapter(Float.class, new FloatDeserializer())
                .registerTypeAdapter(double.class, new DoubleDeserializer())
                .registerTypeAdapter(Double.class, new DoubleDeserializer())
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

    private static class IntegerDeserializer implements JsonDeserializer<Integer> {

        @Override
        public Integer deserialize(JsonElement json, Type typeOfT,
                                   JsonDeserializationContext context) throws JsonParseException {
            if (json.isJsonNull()) {
                return null;
            } else if (json.isJsonPrimitive()) {
                JsonPrimitive jsonPrimitive = (JsonPrimitive) json;
                if (jsonPrimitive.isString() && jsonPrimitive.getAsString().isEmpty()) {
                    return null;
                }
            }
            return json.getAsInt();
        }
    }

    private static class LongDeserializer implements JsonDeserializer<Long> {

        @Override
        public Long deserialize(JsonElement json, Type typeOfT,
                                JsonDeserializationContext context) throws JsonParseException {
            if (json.isJsonNull()) {
                return null;
            } else if (json.isJsonPrimitive()) {
                JsonPrimitive jsonPrimitive = (JsonPrimitive) json;
                if (jsonPrimitive.isString() && jsonPrimitive.getAsString().isEmpty()) {
                    return null;
                }
            }
            return json.getAsLong();
        }
    }

    private static class FloatDeserializer implements JsonDeserializer<Float> {

        @Override
        public Float deserialize(JsonElement json, Type typeOfT,
                                JsonDeserializationContext context) throws JsonParseException {
            if (json.isJsonNull()) {
                return null;
            } else if (json.isJsonPrimitive()) {
                JsonPrimitive jsonPrimitive = (JsonPrimitive) json;
                if (jsonPrimitive.isString() && jsonPrimitive.getAsString().isEmpty()) {
                    return null;
                }
            }
            return json.getAsFloat();
        }
    }

    private static class DoubleDeserializer implements JsonDeserializer<Double> {

        @Override
        public Double deserialize(JsonElement json, Type typeOfT,
                                 JsonDeserializationContext context) throws JsonParseException {
            if (json.isJsonNull()) {
                return null;
            } else if (json.isJsonPrimitive()) {
                JsonPrimitive jsonPrimitive = (JsonPrimitive) json;
                if (jsonPrimitive.isString() && jsonPrimitive.getAsString().isEmpty()) {
                    return null;
                }
            }
            return json.getAsDouble();
        }
    }
}
