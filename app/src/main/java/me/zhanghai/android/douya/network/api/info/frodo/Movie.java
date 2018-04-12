/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import me.zhanghai.android.douya.util.CollectionUtils;

public class Movie extends SimpleMovie {

    @SerializedName("aka")
    public ArrayList<String> alternativeTitles = new ArrayList<>();

    public ArrayList<String> countries = new ArrayList<>();

    @SerializedName("cover")
    public Photo poster;

    public ArrayList<String> durations = new ArrayList<>();

    @SerializedName("episodes_count")
    public int episodeCount;

    @SerializedName("honor_infos")
    public ArrayList<Honor> honors = new ArrayList<>();

    @SerializedName("info_url")
    public String informationUrl;

    @SerializedName("is_released")
    public boolean isReleased;

    @SerializedName("is_tv")
    public boolean isTv;

    public ArrayList<String> languages = new ArrayList<>();

    @SerializedName("lineticket_url")
    public String ticketUrl;

    @SerializedName("original_title")
    public String originalTitle;

    @SerializedName("ticket_price_info")
    public String ticketPriceInformation;

    public MovieTrailer trailer;

    public String getEpisodeCountString() {
        return episodeCount != 0 ? episodeCount + "集" : null;
    }

    public String getDurationString() {
        return CollectionUtils.firstOrNull(durations);
    }


    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }
        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public Movie() {}

    protected Movie(Parcel in) {
        super(in);

        alternativeTitles = in.createStringArrayList();
        countries = in.createStringArrayList();
        poster = in.readParcelable(Photo.class.getClassLoader());
        durations = in.createStringArrayList();
        episodeCount = in.readInt();
        honors = in.createTypedArrayList(Honor.CREATOR);
        informationUrl = in.readString();
        isReleased = in.readByte() != 0;
        isTv = in.readByte() != 0;
        languages = in.createStringArrayList();
        ticketUrl = in.readString();
        originalTitle = in.readString();
        ticketPriceInformation = in.readString();
        trailer = in.readParcelable(MovieTrailer.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeStringList(alternativeTitles);
        dest.writeStringList(countries);
        dest.writeParcelable(poster, flags);
        dest.writeStringList(durations);
        dest.writeInt(episodeCount);
        dest.writeTypedList(honors);
        dest.writeString(informationUrl);
        dest.writeByte(isReleased ? (byte) 1 : (byte) 0);
        dest.writeByte(isTv ? (byte) 1 : (byte) 0);
        dest.writeStringList(languages);
        dest.writeString(ticketUrl);
        dest.writeString(originalTitle);
        dest.writeString(ticketPriceInformation);
        dest.writeParcelable(trailer, flags);
    }
}
