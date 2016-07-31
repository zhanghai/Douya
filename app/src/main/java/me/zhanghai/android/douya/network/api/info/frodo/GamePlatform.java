/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class GamePlatform implements Parcelable {

    public String abbreviation;

    @SerializedName("cn_name")
    public String chineseName;

    public long id;

    public String name;


    public static final Creator<GamePlatform> CREATOR = new Creator<GamePlatform>() {
        @Override
        public GamePlatform createFromParcel(Parcel source) {
            return new GamePlatform(source);
        }
        @Override
        public GamePlatform[] newArray(int size) {
            return new GamePlatform[size];
        }
    };

    public GamePlatform() {}

    protected GamePlatform(Parcel in) {
        abbreviation = in.readString();
        chineseName = in.readString();
        id = in.readLong();
        name = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(abbreviation);
        dest.writeString(chineseName);
        dest.writeLong(id);
        dest.writeString(name);
    }
}
