/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

public class Image implements Parcelable {

    @SerializedName("is_animated")
    public boolean isAnimated;

    /**
     * @deprecated Use {@link #getLarge()} instead.
     */
    public String large;

    /**
     * @deprecated Use {@link #getNormal()} ()} instead.
     */
    public String normal;


    public String getNormal() {
        //noinspection deprecation
        return !TextUtils.isEmpty(normal) ? normal : large;
    }

    public String getLarge() {
        //noinspection deprecation
        return !TextUtils.isEmpty(large) ? large : normal;
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
        normal = in.readString();
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
        dest.writeString(normal);
    }
}
