/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class SimpleMovie extends CollectableItem {

    public List<Celebrity> actors = new ArrayList<>();

    public List<Celebrity> directors = new ArrayList<>();

    public List<String> genres = new ArrayList<>();

    @SerializedName("has_linewatch")
    public boolean hasOnlineSource;

    @SerializedName("pubdate")
    public List<String> releaseDates = new ArrayList<>();

    public String year;


    public static final Creator<SimpleMovie> CREATOR = new Creator<SimpleMovie>() {
        @Override
        public SimpleMovie createFromParcel(Parcel source) {
            return new SimpleMovie(source);
        }
        @Override
        public SimpleMovie[] newArray(int size) {
            return new SimpleMovie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeTypedList(actors);
        dest.writeTypedList(directors);
        dest.writeStringList(genres);
        dest.writeByte(hasOnlineSource ? (byte) 1 : (byte) 0);
        dest.writeStringList(releaseDates);
        dest.writeString(year);
    }

    public SimpleMovie() {}

    protected SimpleMovie(Parcel in) {
        super(in);

        actors = in.createTypedArrayList(Celebrity.CREATOR);
        directors = in.createTypedArrayList(Celebrity.CREATOR);
        genres = in.createStringArrayList();
        hasOnlineSource = in.readByte() != 0;
        releaseDates = in.createStringArrayList();
        year = in.readString();
    }
}
