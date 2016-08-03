/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.apiv2;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;

public class UserInfo extends User {

    @SerializedName("albums_count")
    public int albumCount;

    @SerializedName("blocked")
    public boolean isBlocked;

    @SerializedName("blocking")
    public boolean isBlocking;

    @SerializedName("created")
    public String createdAt;

    @SerializedName("desc")
    public String introduction;

    @SerializedName("followers_count")
    public int followerCount;

    @SerializedName("following")
    public boolean isFollowed;

    @SerializedName("following_count")
    public int followingCount;

    @SerializedName("icon_avatar")
    public String iconAvatar;

    @SerializedName("is_follower")
    public boolean isFollower;

    @SerializedName("loc_id")
    public String locationId;

    @SerializedName("loc_name")
    public String locationName;

    @SerializedName("logged_in")
    public boolean isLoggedIn;

    @SerializedName("notes_count")
    public int diaryCount;

    public String relation;

    public String signature;

    @SerializedName("statuses_count")
    public int broadcastCount;

    public void fixFollowed(boolean followed) {
        if (isFollowed != followed) {
            isFollowed = followed;
            if (isFollowed) {
                ++followerCount;
            } else {
                --followerCount;
            }
        }
    }


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

        albumCount = in.readInt();
        isBlocked = in.readByte() != 0;
        isBlocking = in.readByte() != 0;
        createdAt = in.readString();
        introduction = in.readString();
        followerCount = in.readInt();
        isFollowed = in.readByte() != 0;
        followingCount = in.readInt();
        iconAvatar = in.readString();
        isFollower = in.readByte() != 0;
        locationId = in.readString();
        locationName = in.readString();
        isLoggedIn = in.readByte() != 0;
        diaryCount = in.readInt();
        relation = in.readString();
        signature = in.readString();
        broadcastCount = in.readInt();
    }

    @Override
    public int describeContents() {
        return super.describeContents();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeInt(albumCount);
        dest.writeByte(isBlocked ? (byte) 1 : (byte) 0);
        dest.writeByte(isBlocking ? (byte) 1 : (byte) 0);
        dest.writeString(createdAt);
        dest.writeString(introduction);
        dest.writeInt(followerCount);
        dest.writeByte(isFollowed ? (byte) 1 : (byte) 0);
        dest.writeInt(followingCount);
        dest.writeString(iconAvatar);
        dest.writeByte(isFollower ? (byte) 1 : (byte) 0);
        dest.writeString(locationId);
        dest.writeString(locationName);
        dest.writeByte(isLoggedIn ? (byte) 1 : (byte) 0);
        dest.writeInt(diaryCount);
        dest.writeString(relation);
        dest.writeString(signature);
        dest.writeInt(broadcastCount);
    }
}
