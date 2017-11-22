/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * {@code Ceremony} in Frodo.
 */
public class Award extends SimpleAward {

    @SerializedName("album")
    public PhotoList photos;

    public ArrayList<AwardCategory> categories = new ArrayList<>();

    @SerializedName("ceremonies")
    public ArrayList<SimpleAward> awards = new ArrayList<>();

    @SerializedName("channels")
    public ArrayList<AwardType> types = new ArrayList<>();

    @SerializedName("end_time")
    public String endTime;

    @SerializedName("header_bg_color")
    public String themeColor;

    @SerializedName("intro")
    public String introduction;

    public String location;

    @SerializedName("start_time")
    public String startTime;


    public static final Creator<Award> CREATOR = new Creator<Award>() {
        @Override
        public Award createFromParcel(Parcel source) {
            return new Award(source);
        }
        @Override
        public Award[] newArray(int size) {
            return new Award[size];
        }
    };

    public Award() {}

    protected Award(Parcel in) {
        super(in);

        photos = in.readParcelable(PhotoList.class.getClassLoader());
        categories = in.createTypedArrayList(AwardCategory.CREATOR);
        awards = in.createTypedArrayList(SimpleAward.CREATOR);
        types = in.createTypedArrayList(AwardType.CREATOR);
        endTime = in.readString();
        themeColor = in.readString();
        introduction = in.readString();
        location = in.readString();
        startTime = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeParcelable(photos, flags);
        dest.writeTypedList(categories);
        dest.writeTypedList(awards);
        dest.writeTypedList(types);
        dest.writeString(endTime);
        dest.writeString(themeColor);
        dest.writeString(introduction);
        dest.writeString(location);
        dest.writeString(startTime);
    }
}
