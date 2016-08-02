/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import me.zhanghai.android.douya.account.util.AccountUtils;

public class User implements Parcelable {

    @SerializedName("abstract")
    public String introduction;

    public String avatar;

    public String gender;

    public long id;

    @SerializedName("kind")
    public String type;

    /**
     * @deprecated Use {@link #getLargeAvatar()} instead.
     */
    @SerializedName("large_avatar")
    public String largeAvatar;

    @SerializedName("loc")
    public Location location;

    public String name;

    // The value of this field is always "user", so we are using "kind" as "type" instead.
    // public String type;

    public String uid;

    public String uri;

    public String url;

    public String getLargeAvatar() {
        //noinspection deprecation
        return !TextUtils.isEmpty(largeAvatar) ? largeAvatar : avatar;
    }

    public boolean hasIdOrUid(String idOrUid) {
        return TextUtils.equals(String.valueOf(id), idOrUid) || TextUtils.equals(uid, idOrUid);
    }

    public boolean isOneself(Context context) {
        return id == AccountUtils.getUserId(context);
    }


    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }
        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public User() {}

    protected User(Parcel in) {
        introduction = in.readString();
        avatar = in.readString();
        gender = in.readString();
        id = in.readLong();
        type = in.readString();
        //noinspection deprecation
        largeAvatar = in.readString();
        location = in.readParcelable(Location.class.getClassLoader());
        name = in.readString();
        uid = in.readString();
        uri = in.readString();
        url = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(introduction);
        dest.writeString(avatar);
        dest.writeString(gender);
        dest.writeLong(id);
        dest.writeString(type);
        //noinspection deprecation
        dest.writeString(largeAvatar);
        dest.writeParcelable(location, flags);
        dest.writeString(name);
        dest.writeString(uid);
        dest.writeString(uri);
        dest.writeString(url);
    }
}
