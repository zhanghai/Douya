/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.apiv2;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import me.zhanghai.android.douya.network.api.info.frodo.SizedImage;
import me.zhanghai.android.douya.ui.SizedImageItem;

public class Image implements SizedImageItem, Parcelable {

    public int height;

    /**
     * @deprecated Use {@link #getSmallUrl()} instead.
     */
    @SerializedName("href")
    public String small;

    /**
     * @deprecated Use {@link #getLargeUrl()} instead.
     */
    @SerializedName("image")
    public String large;

    @SerializedName("is_animated")
    public boolean isAnimated;

    /**
     * @deprecated Use {@link #getMediumUrl()} instead.
     */
    @SerializedName("thumb")
    public String medium;

    public String type;

    public int width;


    @Override
    public String getLargeUrl() {
        //noinspection deprecation
        return large != null ? large
                : medium != null ? medium
                : small;
    }

    @Override
    public int getLargeWidth() {
        return width;
    }

    @Override
    public int getLargeHeight() {
        return height;
    }

    @Override
    public String getMediumUrl() {
        //noinspection deprecation
        return medium != null ? medium
                : large != null ? large
                : small;
    }

    @Override
    public int getMediumWidth() {
        return width;
    }

    @Override
    public int getMediumHeight() {
        return height;
    }

    @Override
    public String getSmallUrl() {
        //noinspection deprecation
        return small != null ? small
                : medium != null ? medium
                : large;
    }

    @Override
    public int getSmallWidth() {
        return width;
    }

    @Override
    public int getSmallHeight() {
        return height;
    }

    @Override
    public boolean isAnimated() {
        return isAnimated;
    }


    @SuppressWarnings("deprecation")
    public SizedImage toFrodoSizedImage() {
        SizedImage sizedImage = new SizedImage();
        sizedImage.raw = new SizedImage.Item();
        sizedImage.raw.url = getLargeUrl();
        sizedImage.raw.width = getLargeWidth();
        sizedImage.raw.height = getLargeHeight();
        sizedImage.large = new SizedImage.Item();
        sizedImage.large.url = getLargeUrl();
        sizedImage.large.width = getLargeWidth();
        sizedImage.large.height = getLargeHeight();
        sizedImage.medium = new SizedImage.Item();
        sizedImage.medium.url = getMediumUrl();
        sizedImage.medium.width = getMediumWidth();
        sizedImage.medium.height = getMediumHeight();
        sizedImage.small = new SizedImage.Item();
        sizedImage.small.url = getSmallUrl();
        sizedImage.small.width = getSmallWidth();
        sizedImage.small.height = getSmallHeight();
        sizedImage.isAnimated = isAnimated;
        return sizedImage;
    }


    public static final Parcelable.Creator<Image> CREATOR = new Parcelable.Creator<Image>() {
        public Image createFromParcel(Parcel source) {
            return new Image(source);
        }
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(height);
        //noinspection deprecation
        dest.writeString(small);
        //noinspection deprecation
        dest.writeString(large);
        dest.writeByte(isAnimated ? (byte) 1 : (byte) 0);
        //noinspection deprecation
        dest.writeString(medium);
        dest.writeString(type);
        dest.writeInt(width);
    }

    public Image() {}

    protected Image(Parcel in) {
        height = in.readInt();
        //noinspection deprecation
        small = in.readString();
        //noinspection deprecation
        large = in.readString();
        isAnimated = in.readByte() != 0;
        //noinspection deprecation
        medium = in.readString();
        type = in.readString();
        width = in.readInt();
    }
}
