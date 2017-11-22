/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Tag implements Parcelable {

    public String icon;

    public String id;

    @SerializedName("is_follow")
    public boolean isFollowed;

    public String name;

    public String uri;

    public String url;


    public static final Parcelable.Creator<Tag> CREATOR = new Parcelable.Creator<Tag>() {
        @Override
        public Tag createFromParcel(Parcel source) {
            return new Tag(source);
        }
        @Override
        public Tag[] newArray(int size) {
            return new Tag[size];
        }
    };

    public Tag() {}

    protected Tag(Parcel in) {
        icon = in.readString();
        id = in.readString();
        isFollowed = in.readByte() != 0;
        name = in.readString();
        uri = in.readString();
        url = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(icon);
        dest.writeString(id);
        dest.writeByte(isFollowed ? (byte) 1 : (byte) 0);
        dest.writeString(name);
        dest.writeString(uri);
        dest.writeString(url);
    }
}
