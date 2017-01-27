/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class ImageWithSize implements Parcelable {

    @SerializedName("is_animated")
    public boolean isAnimated;

    /**
     * @deprecated Use {@link #getLarge()} instead.
     */
    public Item large;

    /**
     * @deprecated Use {@link #getNormal()} ()} instead.
     */
    public Item normal;

    /**
     * @deprecated Use {@link #getSmall()} ()} instead.
     */
    public Item small;

    public Item getLarge() {
        //noinspection deprecation
        return large != null ? large
                : normal != null ? normal
                : small;
    }

    public Item getNormal() {
        //noinspection deprecation
        return normal != null ? normal
                : large != null ? large
                : small;
    }

    public Item getSmall() {
        //noinspection deprecation
        return small != null ? small
                : normal != null ? normal
                : large;
    }


    public static final Parcelable.Creator<ImageWithSize> CREATOR =
            new Parcelable.Creator<ImageWithSize>() {
                @Override
                public ImageWithSize createFromParcel(Parcel source) {
                    return new ImageWithSize(source);
                }
                @Override
                public ImageWithSize[] newArray(int size) {
                    return new ImageWithSize[size];
                }
            };

    public ImageWithSize() {}

    protected ImageWithSize(Parcel in) {
        isAnimated = in.readByte() != 0;
        //noinspection deprecation
        large = in.readParcelable(Item.class.getClassLoader());
        //noinspection deprecation
        normal = in.readParcelable(Item.class.getClassLoader());
        //noinspection deprecation
        small = in.readParcelable(Item.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(isAnimated ? (byte) 1 : (byte) 0);
        //noinspection deprecation
        dest.writeParcelable(large, flags);
        //noinspection deprecation
        dest.writeParcelable(normal, flags);
        //noinspection deprecation
        dest.writeParcelable(small, flags);
    }


    public static class Item implements Parcelable {

        public int height;

        public String url;

        public int width;


        public static final Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>() {
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
            height = in.readInt();
            url = in.readString();
            width = in.readInt();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(height);
            dest.writeString(url);
            dest.writeInt(width);
        }
    }
}
