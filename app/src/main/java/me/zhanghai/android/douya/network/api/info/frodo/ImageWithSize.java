/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import me.zhanghai.android.douya.ui.ImageItemWithSize;

public class ImageWithSize implements ImageItemWithSize, Parcelable {

    @SerializedName("is_animated")
    public boolean isAnimated;

    /**
     * @deprecated Use {@link #getLarge()} instead.
     */
    public Item large;

    /**
     * @deprecated Use {@link #getMedium()} ()} instead.
     */
    @SerializedName("normal")
    public Item medium;

    /**
     * @deprecated Use {@link #getSmall()} ()} instead.
     */
    public Item small;

    public Item getLarge() {
        //noinspection deprecation
        return large != null ? large
                : medium != null ? medium
                : small;
    }

    public Item getMedium() {
        //noinspection deprecation
        return medium != null ? medium
                : large != null ? large
                : small;
    }

    public Item getSmall() {
        //noinspection deprecation
        return small != null ? small
                : medium != null ? medium
                : large;
    }


    @Override
    public String getLargeUrl() {
        return getLarge().url;
    }

    @Override
    public int getLargeWidth() {
        return getLarge().width;
    }

    @Override
    public int getLargeHeight() {
        return getLarge().height;
    }

    @Override
    public String getMediumUrl() {
        return getMedium().url;
    }

    @Override
    public int getMediumWidth() {
        return getMedium().width;
    }

    @Override
    public int getMediumHeight() {
        return getMedium().height;
    }

    @Override
    public String getSmallUrl() {
        return getSmall().url;
    }

    @Override
    public int getSmallWidth() {
        return getSmall().width;
    }

    @Override
    public int getSmallHeight() {
        return getSmall().height;
    }

    @Override
    public boolean isAnimated() {
        return isAnimated;
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
        medium = in.readParcelable(Item.class.getClassLoader());
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
        dest.writeParcelable(medium, flags);
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
