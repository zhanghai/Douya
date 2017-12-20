/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

public class User extends SimpleUser {

    public enum VerificationType {

        NONE,
        OFFICIAL,
        THIRD_PARTY;

        public static VerificationType ofApiInt(int apiInt, VerificationType defaultValue) {
            if (apiInt >= 0 && apiInt < values().length) {
                return values()[apiInt];
            } else {
                return defaultValue;
            }
        }

        public static VerificationType ofApiInt(int apiInt) {
            return ofApiInt(apiInt, NONE);
        }
    }

    @SerializedName("abstract")
    public String introduction;

    @SerializedName("ark_published_count")
    public int arkPublicationCount;

    public String birthday;

    @SerializedName("can_donate")
    public boolean canAcceptDonation;

    @SerializedName("can_set_original")
    public boolean canDeclareOriginal;

    @SerializedName("collected_subjects_count")
    public int collectedItemCount;

    @SerializedName("dramas_count")
    public int collectedDramaCount;

    @SerializedName("followed")
    public boolean isFollowed;

    @SerializedName("followers_count")
    public int followerCount;

    @SerializedName("following_count")
    public int followingCount;

    @SerializedName("following_doulist_count")
    public int followingDoulistCount;

    public String gender;

    @SerializedName("group_chat_count")
    public int groupChatCount;

    @SerializedName("has_user_hot_module")
    public boolean hasUserHotModule;

    @SerializedName("in_blacklist")
    public boolean isInBlacklist;

    // The same as "abstract" which is "introduction" in SimpleUser.
    //public String intro;

    @SerializedName("is_phone_bound")
    public boolean isPhoneBound;

    @SerializedName("is_normal")
    public boolean hasEmailOrPhone;

    @SerializedName("joined_group_count")
    public int joinedGroupCount;

    /**
     * @deprecated Use {@link #getLargeAvatar()} instead.
     */
    @SerializedName("large_avatar")
    public String largeAvatar;

    @SerializedName("notes_count")
    public int diaryCount;

    @SerializedName("owned_doulist_count")
    public int doulistCount;

    @SerializedName("photo_albums_count")
    public int albumCount;

    @SerializedName("profile_banner")
    public Image profileBackdrop;

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

    /**
     * @deprecated Use {@link #getVerificationType()} instead.
     */
    @SerializedName("verify_type")
    public int verificationType;

    @SerializedName("verify_reason")
    public String verificationReason;

    public String getLargeAvatar() {
        //noinspection deprecation
        return !TextUtils.isEmpty(largeAvatar) ? largeAvatar : avatar;
    }

    public VerificationType getVerificationType() {
        //noinspection deprecation
        return VerificationType.ofApiInt(verificationType);
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
        super(in);

        introduction = in.readString();
        arkPublicationCount = in.readInt();
        birthday = in.readString();
        canAcceptDonation = in.readByte() != 0;
        canDeclareOriginal = in.readByte() != 0;
        collectedItemCount = in.readInt();
        collectedDramaCount = in.readInt();
        isFollowed = in.readByte() != 0;
        followerCount = in.readInt();
        followingCount = in.readInt();
        followingDoulistCount = in.readInt();
        gender = in.readString();
        groupChatCount = in.readInt();
        hasUserHotModule = in.readByte() != 0;
        isInBlacklist = in.readByte() != 0;
        isPhoneBound = in.readByte() != 0;
        hasEmailOrPhone = in.readByte() != 0;
        joinedGroupCount = in.readInt();
        //noinspection deprecation
        largeAvatar = in.readString();
        diaryCount = in.readInt();
        doulistCount = in.readInt();
        albumCount = in.readInt();
        profileBackdrop = in.readParcelable(Image.class.getClassLoader());
        registrationTime = in.readString();
        comment = in.readString();
        setiChannelCount = in.readInt();
        broadcastCount = in.readInt();
        hasUpdatedProfile = in.readByte() != 0;
        //noinspection deprecation
        verificationType = in.readInt();
        verificationReason = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeString(introduction);
        dest.writeInt(arkPublicationCount);
        dest.writeString(birthday);
        dest.writeByte(canAcceptDonation ? (byte) 1 : (byte) 0);
        dest.writeByte(canDeclareOriginal ? (byte) 1 : (byte) 0);
        dest.writeInt(collectedItemCount);
        dest.writeInt(collectedDramaCount);
        dest.writeByte(isFollowed ? (byte) 1 : (byte) 0);
        dest.writeInt(followerCount);
        dest.writeInt(followingCount);
        dest.writeInt(followingDoulistCount);
        dest.writeString(gender);
        dest.writeInt(groupChatCount);
        dest.writeByte(hasUserHotModule ? (byte) 1 : (byte) 0);
        dest.writeByte(isInBlacklist ? (byte) 1 : (byte) 0);
        dest.writeByte(isPhoneBound ? (byte) 1 : (byte) 0);
        dest.writeByte(hasEmailOrPhone ? (byte) 1 : (byte) 0);
        dest.writeInt(joinedGroupCount);
        //noinspection deprecation
        dest.writeString(largeAvatar);
        dest.writeInt(diaryCount);
        dest.writeInt(doulistCount);
        dest.writeInt(albumCount);
        dest.writeParcelable(profileBackdrop, flags);
        dest.writeString(registrationTime);
        dest.writeString(comment);
        dest.writeInt(setiChannelCount);
        dest.writeInt(broadcastCount);
        dest.writeByte(hasUpdatedProfile ? (byte) 1 : (byte) 0);
        //noinspection deprecation
        dest.writeInt(verificationType);
        dest.writeString(verificationReason);
    }
}
