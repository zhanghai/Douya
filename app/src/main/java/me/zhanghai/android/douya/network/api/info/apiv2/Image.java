/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.apiv2;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Image implements Parcelable {

    public int height;

    @SerializedName("href")
    public String small;

    @SerializedName("image")
    public String raw;

    @SerializedName("is_animated")
    public boolean animated;

    @SerializedName("thumb")
    public String medium;
    
    public String type;
    
    public int width;

    public String getLargest() {
        return raw != null ? raw
                : medium != null ? medium
                : small;
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
        dest.writeString(small);
        dest.writeString(raw);
        dest.writeByte(animated ? (byte) 1 : (byte) 0);
        dest.writeString(medium);
        dest.writeString(type);
        dest.writeInt(width);
    }

    public Image() {}

    protected Image(Parcel in) {
        height = in.readInt();
        small = in.readString();
        raw = in.readString();
        animated = in.readByte() != 0;
        medium = in.readString();
        type = in.readString();
        width = in.readInt();
    }
}
