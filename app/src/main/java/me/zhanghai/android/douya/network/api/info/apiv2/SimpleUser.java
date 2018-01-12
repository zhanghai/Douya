/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.apiv2;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import me.zhanghai.android.douya.account.util.AccountUtils;
import me.zhanghai.android.douya.network.api.info.UrlGettable;
import me.zhanghai.android.douya.util.DoubanUtils;

public class SimpleUser implements UrlGettable, Parcelable {

    private static final String LARGE_AVATAR_DEFAULT =
            "https://img3.doubanio.com/icon/user_large.jpg";

    public enum Type {

        USER("user"),
        SITE("site");

        private String apiString;

        Type(String apiString) {
            this.apiString = apiString;
        }

        public static Type ofApiString(String apiString, Type defaultValue) {
            for (Type type : Type.values()) {
                if (TextUtils.equals(type.apiString, apiString)) {
                    return type;
                }
            }
            return defaultValue;
        }

        public static Type ofApiString(String apiString) {
            return ofApiString(apiString, USER);
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

    public boolean isIdOrUid(String idOrUid) {
        //noinspection deprecation
        return TextUtils.equals(String.valueOf(id), idOrUid) || TextUtils.equals(uid, idOrUid);
    }

    /**
     * @deprecated Normally you should use {@link #getIdOrUid()} for API.
     */
    public String getUidOrId() {
        //noinspection deprecation
        return !TextUtils.isEmpty(uid) ? uid : String.valueOf(id);
    }

    public boolean isOneself() {
        //noinspection deprecation
        return id == AccountUtils.getUserId();
    }

    public Type getType() {
        //noinspection deprecation
        return Type.ofApiString(type);
    }

    @Override
    public String getUrl() {
        //noinspection deprecation
        return DoubanUtils.makeUserUrl(getUidOrId());
    }

    @SuppressWarnings("deprecation")
    public static SimpleUser fromFrodo(
            me.zhanghai.android.douya.network.api.info.frodo.SimpleUser frodoSimpleUser) {
        SimpleUser simpleUser = new SimpleUser();
        simpleUser.alt = frodoSimpleUser.url;
        simpleUser.avatar = frodoSimpleUser.avatar;
        simpleUser.id = frodoSimpleUser.id;
        simpleUser.name = frodoSimpleUser.name;
        simpleUser.type = frodoSimpleUser.type;
        simpleUser.uid = frodoSimpleUser.uid;
        return simpleUser;
    }

    @SuppressWarnings("deprecation")
    public me.zhanghai.android.douya.network.api.info.frodo.SimpleUser toFrodo() {
        me.zhanghai.android.douya.network.api.info.frodo.SimpleUser simpleUser =
                new me.zhanghai.android.douya.network.api.info.frodo.SimpleUser();
        simpleUser.avatar = avatar;
        simpleUser.id = id;
        simpleUser.type = type;
        simpleUser.name = name;
        simpleUser.uid = uid;
        simpleUser.uri = DoubanUtils.makeUserUri(id);
        simpleUser.url = alt;
        return simpleUser;
    }


    public static final Creator<SimpleUser> CREATOR = new Creator<SimpleUser>() {
        public SimpleUser createFromParcel(Parcel source) {
            return new SimpleUser(source);
        }
        public SimpleUser[] newArray(int size) {
            return new SimpleUser[size];
        }
    };

    public SimpleUser() {}

    protected SimpleUser(Parcel in) {
        alt = in.readString();
        avatar = in.readString();
        //noinspection deprecation
        id = in.readLong();
        isSuicided = in.readByte() != 0;
        //noinspection deprecation
        largeAvatar = in.readString();
        name = in.readString();
        //noinspection deprecation
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
        //noinspection deprecation
        dest.writeString(type);
        //noinspection deprecation
        dest.writeString(uid);
    }
}
