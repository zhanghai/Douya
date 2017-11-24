/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Celebrity extends SimpleCelebrity {

    @SerializedName("album")
    public PhotoList photos;

    public ArrayList<CelebrityAwardItem> awards = new ArrayList<>();

    @SerializedName("awards_count")
    public int awardCount;

    @SerializedName("body_bg_color")
    public String backgroundColor;

    @SerializedName("fans_count")
    public int fanCount;

    @SerializedName("has_fanned")
    public boolean isFanned;

    @SerializedName("header_bg_color")
    public String themeColor;

    public String info;

    @SerializedName("intro")
    public String introduction;

    @SerializedName("known_for")
    public ArrayList<SimpleMovie> worksByVote = new ArrayList<>();

    @SerializedName("latest_works")
    public ArrayList<SimpleMovie> latestWorks = new ArrayList<>();

    @SerializedName("nominations_count")
    public int nominationCount;

    @SerializedName("related_celebrities")
    public ArrayList<CelebrityRelationship> relatedCelebrities = new ArrayList<>();

    @SerializedName("related_tag")
    public Tag relatedTag;

    @SerializedName("works_count")
    public int workCount;


    public static final Creator<Celebrity> CREATOR = new Creator<Celebrity>() {
        @Override
        public Celebrity createFromParcel(Parcel source) {
            return new Celebrity(source);
        }
        @Override
        public Celebrity[] newArray(int size) {
            return new Celebrity[size];
        }
    };

    public Celebrity() {}

    protected Celebrity(Parcel in) {
        super(in);

        photos = in.readParcelable(PhotoList.class.getClassLoader());
        awards = in.createTypedArrayList(CelebrityAwardItem.CREATOR);
        awardCount = in.readInt();
        backgroundColor = in.readString();
        fanCount = in.readInt();
        isFanned = in.readByte() != 0;
        themeColor = in.readString();
        info = in.readString();
        introduction = in.readString();
        worksByVote = in.createTypedArrayList(SimpleMovie.CREATOR);
        latestWorks = in.createTypedArrayList(SimpleMovie.CREATOR);
        nominationCount = in.readInt();
        relatedCelebrities = in.createTypedArrayList(CelebrityRelationship.CREATOR);
        relatedTag = in.readParcelable(Tag.class.getClassLoader());
        workCount = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeParcelable(photos, flags);
        dest.writeTypedList(awards);
        dest.writeInt(awardCount);
        dest.writeString(backgroundColor);
        dest.writeInt(fanCount);
        dest.writeByte(isFanned ? (byte) 1 : (byte) 0);
        dest.writeString(themeColor);
        dest.writeString(info);
        dest.writeString(introduction);
        dest.writeTypedList(worksByVote);
        dest.writeTypedList(latestWorks);
        dest.writeInt(nominationCount);
        dest.writeTypedList(relatedCelebrities);
        dest.writeParcelable(relatedTag, flags);
        dest.writeInt(workCount);
    }
}
