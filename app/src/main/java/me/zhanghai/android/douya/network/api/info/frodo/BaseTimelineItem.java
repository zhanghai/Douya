/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public abstract class BaseTimelineItem implements Parcelable {

    public enum Type {

        FAILURE("fail"),
        GALLERY_TOPIC_HASHTAG("rec_card_module"),
        HASHTAG_HEADER("hash_tag_header"),
        INTRODUCTION("intro"),
        MISSED_BROADCAST("missed_status"),
        RECOMMENDED_USERS("rec_users"),
        BROADCAST("status"),
        UNIVERSAL("universal");

        private String mApiString;

        Type(String apiString) {
            mApiString = apiString;
        }

        public static Type ofApiString(String apiString, Type defaultValue) {
            for (Type type : Type.values()) {
                if (TextUtils.equals(type.mApiString, apiString)) {
                    return type;
                }
            }
            return defaultValue;
        }

        public static Type ofApiString(String apiString) {
            return ofApiString(apiString, null);
        }

        public String getApiString() {
            return mApiString;
        }
    }

    /**
     * @deprecated Use {@link #getType()} instead.
     */
    public String type;

    public Type getType() {
        //noinspection deprecation
        return Type.ofApiString(type);
    }


    public static class Deserializer implements JsonDeserializer<CollectableItem> {

        @Override
        public CollectableItem deserialize(JsonElement json, java.lang.reflect.Type typeOfT,
                                           JsonDeserializationContext context)
                throws JsonParseException {
            java.lang.reflect.Type type = null;
            Type itemType = Type.ofApiString(json.getAsJsonObject().get("type").getAsString());
            if (itemType != null) {
                switch (itemType) {
                    case BROADCAST:
                        type = BroadcastTimelineItem.class;
                        break;
                }
            }
            if (type == null) {
                type = UnknownTimelineItem.class;
            }
            return context.deserialize(json, type);
        }
    }


    public BaseTimelineItem() {}

    protected BaseTimelineItem(Parcel in) {
        //noinspection deprecation
        type = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //noinspection deprecation
        dest.writeString(type);
    }
}
