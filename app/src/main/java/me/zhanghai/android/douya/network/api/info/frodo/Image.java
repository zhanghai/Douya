/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import me.zhanghai.android.douya.ui.ImageItem;

public class Image implements ImageItem, Parcelable {

    @SerializedName("is_animated")
    public boolean isAnimated;

    /**
     * @deprecated Use {@link #getLargeUrl()} instead.
     */
    public String large;

    /**
     * @deprecated Use {@link #getMediumUrl()} ()} instead.
     */
    @SerializedName("normal")
    public String medium;


    public String getLargeUrl() {
        //noinspection deprecation
        return large != null ? large : medium;
    }

    public String getMediumUrl() {
        //noinspection deprecation
        return medium != null ? medium : large;
    }

    @Override
    public String getSmallUrl() {
        return getMediumUrl();
    }

    @Override
    public boolean isAnimated() {
        return isAnimated;
    }


    public static final Parcelable.Creator<Image> CREATOR = new Parcelable.Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel source) {
            return new Image(source);
        }
        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };

    public Image() {}

    protected Image(Parcel in) {
        isAnimated = in.readByte() != 0;
        //noinspection deprecation
        large = in.readString();
        //noinspection deprecation
        medium = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(isAnimated ? (byte) 1 : (byte) 0);
        //noinspection deprecation
        dest.writeString(large);
        //noinspection deprecation
        dest.writeString(medium);
    }
}
