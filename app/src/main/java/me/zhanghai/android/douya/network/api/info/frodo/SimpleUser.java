/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import me.zhanghai.android.douya.account.util.AccountUtils;
import me.zhanghai.android.douya.network.api.info.UrlGettable;

// FIXME: Frodo API change.
public class SimpleUser implements UrlGettable, Parcelable {

    public String avatar;

    public long id;

    @SerializedName("kind")
    public String type;

    @SerializedName("loc")
    public Location location;

    public String name;

    // The value of this field is always "user", so we are using "kind" as "type" instead.
    //public String type;

    /**
     * @deprecated Use {@link #getIdOrUid()} instead.
     */
    public String uid;

    public String uri;

    public String url;

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
        return id == AccountUtils.getUserId();
    }

    @Override
    public String getUrl() {
        return url;
    }

    public static final Creator<SimpleUser> CREATOR = new Creator<SimpleUser>() {
        @Override
        public SimpleUser createFromParcel(Parcel source) {
            return new SimpleUser(source);
        }
        @Override
        public SimpleUser[] newArray(int size) {
            return new SimpleUser[size];
        }
    };

    public SimpleUser() {}

    protected SimpleUser(Parcel in) {
        avatar = in.readString();
        id = in.readLong();
        type = in.readString();
        location = in.readParcelable(Location.class.getClassLoader());
        name = in.readString();
        //noinspection deprecation
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
        dest.writeString(avatar);
        dest.writeLong(id);
        dest.writeString(type);
        dest.writeParcelable(location, flags);
        dest.writeString(name);
        //noinspection deprecation
        dest.writeString(uid);
        dest.writeString(uri);
        dest.writeString(url);
    }
}
