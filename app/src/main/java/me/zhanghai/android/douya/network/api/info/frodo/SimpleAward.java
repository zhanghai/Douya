/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;

public class SimpleAward extends BaseItem {

    @SerializedName("number")
    public int ordinal;

    @SerializedName("pic")
    public Image cover;

    public int year;


    public static final Creator<SimpleAward> CREATOR = new Creator<SimpleAward>() {
        @Override
        public SimpleAward createFromParcel(Parcel source) {
            return new SimpleAward(source);
        }
        @Override
        public SimpleAward[] newArray(int size) {
            return new SimpleAward[size];
        }
    };

    public SimpleAward() {}

    protected SimpleAward(Parcel in) {
        super(in);

        ordinal = in.readInt();
        cover = in.readParcelable(Image.class.getClassLoader());
        year = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeInt(ordinal);
        dest.writeParcelable(cover, flags);
        dest.writeInt(year);
    }
}
