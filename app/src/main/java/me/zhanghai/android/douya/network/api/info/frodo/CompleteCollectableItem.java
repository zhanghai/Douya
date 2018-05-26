/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

/**
 * Only for use with Gson deserialization.
 */
public class CompleteCollectableItem {

    private CompleteCollectableItem() {}

    public static class Deserializer implements JsonDeserializer<CollectableItem> {

        @Override
        public CollectableItem deserialize(JsonElement json, java.lang.reflect.Type typeOfT,
                                           JsonDeserializationContext context)
                throws JsonParseException {
            java.lang.reflect.Type type = null;
            CollectableItem.Type itemType = CollectableItem.Type.ofApiString(
                    json.getAsJsonObject().get("type").getAsString());
            if (itemType != null) {
                switch (itemType) {
//                    case APP:
//                        break;
                    case BOOK:
                        type = Book.class;
                        break;
//                    case EVENT:
//                        break;
                    case GAME:
                        type = Game.class;
                        break;
                    case MOVIE:
                    case TV:
                        type = Movie.class;
                        break;
                    case MUSIC:
                        type = Music.class;
                }
            }
            if (type == null) {
                type = UnknownCollectableItem.class;
            }
            return context.deserialize(json, type);
        }
    }
}
