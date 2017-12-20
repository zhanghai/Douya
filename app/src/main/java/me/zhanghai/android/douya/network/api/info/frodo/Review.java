/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Review extends SimpleReview {


    @SerializedName("allow_donate")
    public boolean allowDonate;

    @SerializedName("censor_info")
    public CensorshipInfo censorshipInfo;

    public String content;

    public Copyright copyright;

    @SerializedName("donate_count")
    public int donationCount;

    @SerializedName("donate_money")
    public float donatedMoney;

    @SerializedName("donate_user_count")
    public int donatedUserCont;

    @SerializedName("is_donated")
    public boolean isDonated;

    @SerializedName("is_in_user_hot_module")
    public boolean isInHotModule;

    @SerializedName("is_original")
    public boolean isOriginal;

    @SerializedName("is_recommended")
    public boolean isRecommended;

    @SerializedName("liked")
    public boolean isLiked;

    public ArrayList<SizedPhoto> photos = new ArrayList<>();

    public ArrayList<GamePlatform> platforms = new ArrayList<>();

    public ArrayList<Video> videos = new ArrayList<>();


    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel source) {
            return new Review(source);
        }
        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    public Review() {}

    protected Review(Parcel in) {
        super(in);

        allowDonate = in.readByte() != 0;
        censorshipInfo = in.readParcelable(CensorshipInfo.class.getClassLoader());
        content = in.readString();
        copyright = in.readParcelable(Copyright.class.getClassLoader());
        donationCount = in.readInt();
        donatedMoney = in.readFloat();
        donatedUserCont = in.readInt();
        isDonated = in.readByte() != 0;
        isInHotModule = in.readByte() != 0;
        isOriginal = in.readByte() != 0;
        isRecommended = in.readByte() != 0;
        isLiked = in.readByte() != 0;
        photos = in.createTypedArrayList(SizedPhoto.CREATOR);
        platforms = in.createTypedArrayList(GamePlatform.CREATOR);
        videos = in.createTypedArrayList(Video.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeByte(allowDonate ? (byte) 1 : (byte) 0);
        dest.writeParcelable(censorshipInfo, flags);
        dest.writeString(content);
        dest.writeParcelable(copyright, flags);
        dest.writeInt(donationCount);
        dest.writeFloat(donatedMoney);
        dest.writeInt(donatedUserCont);
        dest.writeByte(isDonated ? (byte) 1 : (byte) 0);
        dest.writeByte(isInHotModule ? (byte) 1 : (byte) 0);
        dest.writeByte(isOriginal ? (byte) 1 : (byte) 0);
        dest.writeByte(isRecommended ? (byte) 1 : (byte) 0);
        dest.writeByte(isLiked ? (byte) 1 : (byte) 0);
        dest.writeTypedList(photos);
        dest.writeTypedList(platforms);
        dest.writeTypedList(videos);
    }
}
