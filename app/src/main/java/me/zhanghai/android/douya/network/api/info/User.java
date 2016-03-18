/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

public class User implements Parcelable {

    public static final String TYPE_SITE = "site";
    public static final String TYPE_USER = "user";

    public String alt;

    public String avatar;

    public long id;

    @SerializedName("is_suicide")
    public boolean isSuicided;

    @SerializedName("large_avatar")
    public String largeAvatar;

    public String name;

    public String type;

    public String uid;

    public boolean hasIdOrUid(String idOrUid) {
        return TextUtils.equals(String.valueOf(id), idOrUid) || TextUtils.equals(uid, idOrUid);
    }


    public static final Creator<User> CREATOR = new Creator<User>() {
        public User createFromParcel(Parcel source) {
            return new User(source);
        }
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public User() {}

    protected User(Parcel in) {
        alt = in.readString();
        avatar = in.readString();
        id = in.readLong();
        isSuicided = in.readByte() != 0;
        largeAvatar = in.readString();
        name = in.readString();
        type = in.readString();
        uid = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(alt);
        dest.writeString(avatar);
        dest.writeLong(id);
        dest.writeByte(isSuicided ? (byte) 1 : (byte) 0);
        dest.writeString(largeAvatar);
        dest.writeString(name);
        dest.writeString(type);
        dest.writeString(uid);
    }
}
