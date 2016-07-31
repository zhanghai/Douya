/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

public class Item implements Parcelable {

    public enum Type {

        APP("app"),
        BOOK("book"),
        EVENT("event"),
        GAME("game"),
        MOVIE("movie"),
        MUSIC("music");

        private String apiString;

        Type(String apiString) {
            this.apiString = apiString;
        }

        public static Type ofString(String apiString, Type defaultValue) {
            for (Type type : Type.values()) {
                if (TextUtils.equals(type.apiString, apiString)) {
                    return type;
                }
            }
            return defaultValue;
        }

        public static Type ofString(String apiString) {
            return ofString(apiString, BOOK);
        }
    }

    public String id;

    @SerializedName("pic")
    public Image picture;

    public Rating rating;

    @SerializedName("sharing_url")
    public String shareUrl;

    public String title;

    /**
     * @deprecated Use {@link #getType()} instead.
     */
    public String type;

    public String uri;

    public String url;

    public Type getType() {
        //noinspection deprecation
        return Type.ofString(type);
    }

    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel source) {
            return new Item(source);
        }
        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    public Item() {}

    protected Item(Parcel in) {
        id = in.readString();
        picture = in.readParcelable(Image.class.getClassLoader());
        rating = in.readParcelable(Rating.class.getClassLoader());
        shareUrl = in.readString();
        title = in.readString();
        //noinspection deprecation
        type = in.readString();
        uri = in.readString();
        url = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeParcelable(picture, flags);
        dest.writeParcelable(rating, flags);
        dest.writeString(shareUrl);
        dest.writeString(title);
        //noinspection deprecation
        dest.writeString(type);
        dest.writeString(uri);
        dest.writeString(url);
    }
}
