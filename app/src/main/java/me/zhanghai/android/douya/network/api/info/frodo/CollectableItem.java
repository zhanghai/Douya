/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.content.Context;
import android.os.Parcel;
import android.text.TextUtils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.util.GsonHelper;

/**
 * {@code LegacySubject} in Frodo, for those that can have a rating.
 */
public abstract class CollectableItem extends BaseItem {

    public enum Type {

        APP("app", R.string.item_app_name, R.string.item_app_action),
        BOOK("book", R.string.item_book_name, R.string.item_book_action),
        EVENT("event", R.string.item_event_name, R.string.item_event_action),
        GAME("game", R.string.item_game_name, R.string.item_game_action),
        MOVIE("movie", R.string.item_movie_name, R.string.item_movie_action),
        MUSIC("music", R.string.item_music_name, R.string.item_music_action),
        TV("tv", R.string.item_tv_name, R.string.item_tv_action);

        private String apiString;
        private int nameRes;
        private int actionRes;

        Type(String apiString, int nameRes, int actionRes) {
            this.apiString = apiString;
            this.nameRes = nameRes;
            this.actionRes = actionRes;
        }

        public static Type ofApiString(String apiString, Type defaultValue) {
            for (Type type : Type.values()) {
                if (TextUtils.equals(type.apiString, apiString)) {
                    return type;
                }
            }
            return defaultValue;
        }

        public static Type ofApiString(String apiString) {
            return ofApiString(apiString, null);
        }

        /**
         * @deprecated HACK-only.
         */
        public String getApiString() {
            return apiString;
        }

        public int getNameRes() {
            return nameRes;
        }

        public String getName(Context context) {
            return context.getString(nameRes);
        }

        public int getActionRes() {
            return actionRes;
        }

        public String getAction(Context context) {
            return context.getString(actionRes);
        }
    }

    @SerializedName("body_bg_color")
    public String backgroundColor;

    @SerializedName("comment_count")
    public int commentCount;

    @SerializedName("header_bg_color")
    public String themeColor;

    @SerializedName("interest")
    public ItemCollection collection;

    @SerializedName("intro")
    public String introduction;

    @SerializedName("is_douban_intro")
    public boolean isIntroductionByDouban;

    @SerializedName("null_rating_reason")
    public String ratingUnavailableReason;

    public Rating rating;

    @SerializedName("review_count")
    public int reviewCount;

    @SerializedName("vendor_count")
    public int vendorCount;

    public Type getType() {
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
//                case APP:
//
//                case BOOK:
//
//                case EVENT:
//
//                case GAME:
//
                    case MOVIE:
                    case TV:
                        type = new TypeToken<SimpleMovie>() {}.getType();
//                case MUSIC:
//                    return context.deserialize(json, new TypeToken<Music>() {}.getClass());
                }
            }
            if (type == null) {
                type = new TypeToken<UnknownCollectableItem>() {}.getType();
            }
            return context.deserialize(json, type);
        }
    }

    public static class Serializer implements JsonSerializer<CollectableItem> {
        @Override
        public JsonElement serialize(CollectableItem src, java.lang.reflect.Type typeOfSrc,
                                     JsonSerializationContext context) {
            java.lang.reflect.Type type = null;
            Type itemType = src.getType();
            if (itemType != null) {
                switch (itemType) {
//                    case APP:
//
//                    case BOOK:
//
//                    case EVENT:
//
//                    case GAME:
//
                    case MOVIE:
                    case TV:
                        type = new TypeToken<SimpleMovie>() {}.getClass();
//                    case MUSIC:
//
                }
            }
            if (type == null) {
                type = new TypeToken<UnknownCollectableItem>() {}.getType();
            }
            return context.serialize(src, type);
        }
    }


    public CollectableItem() {}

    protected CollectableItem(Parcel in) {
        super(in);

        backgroundColor = in.readString();
        commentCount = in.readInt();
        themeColor = in.readString();
        collection = in.readParcelable(ItemCollection.class.getClassLoader());
        introduction = in.readString();
        isIntroductionByDouban = in.readByte() != 0;
        ratingUnavailableReason = in.readString();
        rating = in.readParcelable(Rating.class.getClassLoader());
        reviewCount = in.readInt();
        vendorCount = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeString(backgroundColor);
        dest.writeInt(commentCount);
        dest.writeString(themeColor);
        dest.writeParcelable(collection, flags);
        dest.writeString(introduction);
        dest.writeByte(isIntroductionByDouban ? (byte) 1 : (byte) 0);
        dest.writeString(ratingUnavailableReason);
        dest.writeParcelable(rating, flags);
        dest.writeInt(reviewCount);
        dest.writeInt(vendorCount);
    }
}
