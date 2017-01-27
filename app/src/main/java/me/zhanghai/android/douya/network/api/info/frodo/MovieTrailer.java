/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class MovieTrailer implements Parcelable {

    @SerializedName("cover_url")
    public String coverUrl;

    public String id;

    @SerializedName("runtime")
    public String duration;

    public String title;

    @SerializedName("video_url")
    public String videoUrl;


    public static final Parcelable.Creator<MovieTrailer> CREATOR = new Parcelable.Creator<MovieTrailer>() {
        @Override
        public MovieTrailer createFromParcel(Parcel source) {
            return new MovieTrailer(source);
        }
        @Override
        public MovieTrailer[] newArray(int size) {
            return new MovieTrailer[size];
        }
    };

    public MovieTrailer() {}

    protected MovieTrailer(Parcel in) {
        coverUrl = in.readString();
        id = in.readString();
        duration = in.readString();
        title = in.readString();
        videoUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(coverUrl);
        dest.writeString(id);
        dest.writeString(duration);
        dest.writeString(title);
        dest.writeString(videoUrl);
    }
}
