/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;

public class UserInfo extends User {

    public String created;

    @SerializedName("desc")
    public String description;

    @SerializedName("is_banned")
    public boolean isBanned;

    @SerializedName("loc_id")
    public String locationId;

    @SerializedName("loc_name")
    public String locationName;

    public String signature;

    public String status;


    public static final Creator<UserInfo> CREATOR = new Creator<UserInfo>() {
        public UserInfo createFromParcel(Parcel source) {
            return new UserInfo(source);
        }
        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };

    public UserInfo() {}

    protected UserInfo(Parcel in) {
        super(in);

        created = in.readString();
        description = in.readString();
        isBanned = in.readByte() != 0;
        locationId = in.readString();
        locationName = in.readString();
        signature = in.readString();
        status = in.readString();
    }

    @Override
    public int describeContents() {
        return super.describeContents();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeString(created);
        dest.writeString(description);
        dest.writeByte(isBanned ? (byte) 1 : (byte) 0);
        dest.writeString(locationId);
        dest.writeString(locationName);
        dest.writeString(signature);
        dest.writeString(status);
    }
}
