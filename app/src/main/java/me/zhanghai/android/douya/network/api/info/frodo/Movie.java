/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Movie extends CollectableItem {

    public List<Celebrity> actors = new ArrayList<>();

    @SerializedName("aka")
    public List<String> alternativeNames = new ArrayList<>();

    public List<String> countries = new ArrayList<>();

    @SerializedName("cover")
    public Photo poster;

    public List<Celebrity> directors = new ArrayList<>();

    public List<String> durations = new ArrayList<>();

    @SerializedName("episodes_count")
    public int episodeCount;

    public List<String> genres = new ArrayList<>();

    @SerializedName("has_linewatch")
    public boolean hasOnlineSource;

    @SerializedName("in_blacklist")
    public boolean isInBlacklist;

    @SerializedName("info_url")
    public String informationUrl;

    @SerializedName("is_released")
    public boolean isReleased;

    @SerializedName("is_tv")
    public boolean isTv;

    public List<String> languages = new ArrayList<>();

    @SerializedName("lineticket_url")
    public String ticketUrl;

    @SerializedName("original_title")
    public String originalTitle;

    @SerializedName("pubdate")
    public List<String> releaseDates = new ArrayList<>();

    @SerializedName("ticket_price_info")
    public String ticketPriceInformation;

    public MovieTrailer trailer;

    public String year;


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

        actors = in.createTypedArrayList(Celebrity.CREATOR);
        alternativeNames = in.createStringArrayList();
        countries = in.createStringArrayList();
        poster = in.readParcelable(Photo.class.getClassLoader());
        directors = in.createTypedArrayList(Celebrity.CREATOR);
        durations = in.createStringArrayList();
        episodeCount = in.readInt();
        genres = in.createStringArrayList();
        hasOnlineSource = in.readByte() != 0;
        isInBlacklist = in.readByte() != 0;
        informationUrl = in.readString();
        isReleased = in.readByte() != 0;
        isTv = in.readByte() != 0;
        languages = in.createStringArrayList();
        ticketUrl = in.readString();
        originalTitle = in.readString();
        releaseDates = in.createStringArrayList();
        ticketPriceInformation = in.readString();
        trailer = in.readParcelable(MovieTrailer.class.getClassLoader());
        year = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeTypedList(actors);
        dest.writeStringList(alternativeNames);
        dest.writeStringList(countries);
        dest.writeParcelable(poster, flags);
        dest.writeTypedList(directors);
        dest.writeStringList(durations);
        dest.writeInt(episodeCount);
        dest.writeStringList(genres);
        dest.writeByte(hasOnlineSource ? (byte) 1 : (byte) 0);
        dest.writeByte(isInBlacklist ? (byte) 1 : (byte) 0);
        dest.writeString(informationUrl);
        dest.writeByte(isReleased ? (byte) 1 : (byte) 0);
        dest.writeByte(isTv ? (byte) 1 : (byte) 0);
        dest.writeStringList(languages);
        dest.writeString(ticketUrl);
        dest.writeString(originalTitle);
        dest.writeStringList(releaseDates);
        dest.writeString(ticketPriceInformation);
        dest.writeParcelable(trailer, flags);
        dest.writeString(year);
    }
}
