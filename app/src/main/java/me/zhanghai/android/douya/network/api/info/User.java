/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import me.zhanghai.android.douya.account.util.AccountUtils;

public class User implements Parcelable {

    private static final String LARGE_AVATAR_DEFAULT =
            "https://img3.doubanio.com/icon/user_large.jpg";

    public static final String TYPE_SITE = "site";
    public static final String TYPE_USER = "user";

    public String alt;

    public String avatar;

    public long id;

    @SerializedName("is_suicide")
    public boolean isSuicided;

    /**
     * @deprecated Use {@link #getLargeAvatarOrAvatar()} instead.
     */
    @SerializedName("large_avatar")
    public String largeAvatar;

    public String name;

    public String type;

    public String uid;

    public String getLargeAvatarOrAvatar() {
        //noinspection deprecation
        return !TextUtils.equals(largeAvatar, LARGE_AVATAR_DEFAULT)? largeAvatar : avatar;
    }

    public boolean hasIdOrUid(String idOrUid) {
        return TextUtils.equals(String.valueOf(id), idOrUid) || TextUtils.equals(uid, idOrUid);
    }

    public boolean isOneself(Context context) {
        return id == AccountUtils.getUserId(context);
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
        //noinspection deprecation
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
        //noinspection deprecation
        dest.writeString(largeAvatar);
        dest.writeString(name);
        dest.writeString(type);
        dest.writeString(uid);
    }
}
