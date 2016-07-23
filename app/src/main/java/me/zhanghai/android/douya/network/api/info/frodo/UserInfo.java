/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;

public class UserInfo extends User {

    public String birthday;

    @SerializedName("collected_subjects_count")
    public int collectedSubjectCount;

    @SerializedName("followed")
    public boolean isFollowed;

    @SerializedName("followers_count")
    public int followerCount;

    @SerializedName("following_count")
    public int followingCount;

    @SerializedName("following_doulist_count")
    public int followingDoulistCount;

    @SerializedName("group_chat_count")
    public int groupChatCount;

    @SerializedName("in_blacklist")
    public boolean isInBlacklist;

    // The same as "abstract" which is "introduction" in User.
    //public String intro;

    @SerializedName("is_normal")
    public boolean hasEmailOrPhone;

    @SerializedName("joined_group_count")
    public int joinedGroupCount;

    @SerializedName("notes_count")
    public int diaryCount;

    @SerializedName("owned_doulist_count")
    public int doulistCount;

    @SerializedName("photo_albums_count")
    public int albumCount;

    @SerializedName("profile_banner")
    public String profileCover;

    @SerializedName("reg_time")
    public String registrationTime;

    @SerializedName("remark")
    public String comment;

    @SerializedName("seti_channel_count")
    public int setiChannelCount;

    @SerializedName("statuses_count")
    public int broadcastCount;

    @SerializedName("updated_profile")
    public boolean hasUpdatedProfile;


    public static final Creator<UserInfo> CREATOR = new Creator<UserInfo>() {
        @Override
        public UserInfo createFromParcel(Parcel source) {
            return new UserInfo(source);
        }
        @Override
        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };

    public UserInfo() {}

    protected UserInfo(Parcel in) {
        super(in);

        birthday = in.readString();
        collectedSubjectCount = in.readInt();
        isFollowed = in.readByte() != 0;
        followerCount = in.readInt();
        followingCount = in.readInt();
        followingDoulistCount = in.readInt();
        groupChatCount = in.readInt();
        isInBlacklist = in.readByte() != 0;
        hasEmailOrPhone = in.readByte() != 0;
        joinedGroupCount = in.readInt();
        diaryCount = in.readInt();
        doulistCount = in.readInt();
        albumCount = in.readInt();
        profileCover = in.readString();
        registrationTime = in.readString();
        comment = in.readString();
        setiChannelCount = in.readInt();
        broadcastCount = in.readInt();
        hasUpdatedProfile = in.readByte() != 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeString(birthday);
        dest.writeInt(collectedSubjectCount);
        dest.writeByte(isFollowed ? (byte) 1 : (byte) 0);
        dest.writeInt(followerCount);
        dest.writeInt(followingCount);
        dest.writeInt(followingDoulistCount);
        dest.writeInt(groupChatCount);
        dest.writeByte(isInBlacklist ? (byte) 1 : (byte) 0);
        dest.writeByte(hasEmailOrPhone ? (byte) 1 : (byte) 0);
        dest.writeInt(joinedGroupCount);
        dest.writeInt(diaryCount);
        dest.writeInt(doulistCount);
        dest.writeInt(albumCount);
        dest.writeString(profileCover);
        dest.writeString(registrationTime);
        dest.writeString(comment);
        dest.writeInt(setiChannelCount);
        dest.writeInt(broadcastCount);
        dest.writeByte(hasUpdatedProfile ? (byte) 1 : (byte) 0);
    }
}
