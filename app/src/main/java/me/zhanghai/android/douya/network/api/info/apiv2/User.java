/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.apiv2;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import me.zhanghai.android.douya.account.util.AccountUtils;

public class User implements Parcelable {

    private static final String LARGE_AVATAR_DEFAULT =
            "https://img3.doubanio.com/icon/user_large.jpg";

    public enum Type {

        USER("user"),
        SITE("site");

        private String apiString;

        Type(String apiString) {
            this.apiString = apiString;
        }

        public static Type ofString(String apiString, Type defaultValue) {
            for (Type type : Type.values()) {
                if (TextUtils.equals(type.apiString, apiString)) {
                    return type;
                }
            }
            return defaultValue;
        }

        public static Type ofString(String apiString) {
            return ofString(apiString, USER);
        }
    }

    public String alt;

    public String avatar;

    /**
     * @deprecated Use {@link #getIdOrUid()} instead.
     */
    public long id;

    @SerializedName("is_suicide")
    public boolean isSuicided;

    /**
     * @deprecated Use {@link #getLargeAvatarOrAvatar()} instead.
     */
    @SerializedName("large_avatar")
    public String largeAvatar;

    public String name;

    /**
     * @deprecated Use {@link #getType()} instead.
     */
    public String type;

    /**
     * @deprecated Use {@link #getIdOrUid()} instead.
     */
    public String uid;

    public String getLargeAvatarOrAvatar() {
        //noinspection deprecation
        return !TextUtils.isEmpty(largeAvatar)
                && !TextUtils.equals(largeAvatar, LARGE_AVATAR_DEFAULT) ? largeAvatar : avatar;
    }

    public String getIdOrUid() {
        // Some Frodo API does not recognize uid, e.g. 'user/*/notes'.
        //noinspection deprecation
        return String.valueOf(id);
    }

    public boolean hasIdOrUid(String idOrUid) {
        //noinspection deprecation
        return TextUtils.equals(String.valueOf(id), idOrUid) || TextUtils.equals(uid, idOrUid);
    }

    public boolean isOneself(Context context) {
        //noinspection deprecation
        return id == AccountUtils.getUserId(context);
    }

    public Type getType() {
        //noinspection deprecation
        return Type.ofString(type);
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
        //noinspection deprecation
        id = in.readLong();
        isSuicided = in.readByte() != 0;
        //noinspection deprecation
        largeAvatar = in.readString();
        name = in.readString();
        type = in.readString();
        //noinspection deprecation
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
        //noinspection deprecation
        dest.writeLong(id);
        dest.writeByte(isSuicided ? (byte) 1 : (byte) 0);
        //noinspection deprecation
        dest.writeString(largeAvatar);
        dest.writeString(name);
        dest.writeString(type);
        //noinspection deprecation
        dest.writeString(uid);
    }
}
