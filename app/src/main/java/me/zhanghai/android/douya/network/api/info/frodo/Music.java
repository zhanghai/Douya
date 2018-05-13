/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Music extends SimpleMusic {

    @SerializedName("discs")
    public ArrayList<String> discCounts = new ArrayList<>();

    @SerializedName("intro_url")
    public String informationUrl;

    public ArrayList<String> media = new ArrayList<>();

    @SerializedName("publisher")
    public ArrayList<String> publishers = new ArrayList<>();

    @SerializedName("songs")
    public ArrayList<Track> tracks = new ArrayList<>();

    @SerializedName("tracks_url")
    public String tracksUrl;

    @SerializedName("version")
    public ArrayList<String> types = new ArrayList<>();


    public static final Creator<Music> CREATOR = new Creator<Music>() {
        @Override
        public Music createFromParcel(Parcel source) {
            return new Music(source);
        }
        @Override
        public Music[] newArray(int size) {
            return new Music[size];
        }
    };

    public Music() {}

    protected Music(Parcel in) {
        super(in);

        discCounts = in.createStringArrayList();
        informationUrl = in.readString();
        media = in.createStringArrayList();
        publishers = in.createStringArrayList();
        tracks = in.createTypedArrayList(Track.CREATOR);
        tracksUrl = in.readString();
        types = in.createStringArrayList();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeStringList(discCounts);
        dest.writeString(informationUrl);
        dest.writeStringList(media);
        dest.writeStringList(publishers);
        dest.writeTypedList(tracks);
        dest.writeString(tracksUrl);
        dest.writeStringList(types);
    }


    /**
     * {@code Songs.Song} in Frodo.
     */
    public static class Track implements Parcelable {

        @SerializedName("cover_url")
        public String coverUrl;

        public int duration;

        @SerializedName("preview_url")
        public String previewUrl;

        public String title;


        public static final Creator<Track> CREATOR = new Creator<Track>() {
            @Override
            public Track createFromParcel(Parcel source) {
                return new Track(source);
            }
            @Override
            public Track[] newArray(int size) {
                return new Track[size];
            }
        };

        public Track() {}

        protected Track(Parcel in) {
            coverUrl = in.readString();
            duration = in.readInt();
            previewUrl = in.readString();
            title = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(coverUrl);
            dest.writeInt(duration);
            dest.writeString(previewUrl);
            dest.writeString(title);
        }
    }
}
