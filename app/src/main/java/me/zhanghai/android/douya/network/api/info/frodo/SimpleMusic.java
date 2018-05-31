/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import me.zhanghai.android.douya.functional.Functional;

public class SimpleMusic extends CollectableItem {

    public ArrayList<String> genres = new ArrayList<>();

    @SerializedName("pubdate")
    public ArrayList<String> releaseDates = new ArrayList<>();

    @SerializedName("singer")
    public ArrayList<Artist> artists = new ArrayList<>();

    public String getReleaseDate() {
        return CollectableItem.getReleaseDate(releaseDates);
    }

    public String getYearMonth(Context context) {
        return CollectableItem.getYearMonth(releaseDates, context);
    }

    public List<String> getArtistNames() {
        return Functional.map(artists, artist -> artist.name);
    }


    public static final Creator<SimpleMusic> CREATOR = new Creator<SimpleMusic>() {
        @Override
        public SimpleMusic createFromParcel(Parcel source) {
            return new SimpleMusic(source);
        }
        @Override
        public SimpleMusic[] newArray(int size) {
            return new SimpleMusic[size];
        }
    };

    public SimpleMusic() {}

    protected SimpleMusic(Parcel in) {
        super(in);

        genres = in.createStringArrayList();
        releaseDates = in.createStringArrayList();
        artists = in.createTypedArrayList(Artist.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeStringList(genres);
        dest.writeStringList(releaseDates);
        dest.writeTypedList(artists);
    }


    /**
     * {@code Music.Singer} in Frodo.
     */
    public static class Artist implements Parcelable {

        public String id;

        public String name;

        public String url;


        public static final Creator<Artist> CREATOR = new Creator<Artist>() {
            @Override
            public Artist createFromParcel(Parcel source) {
                return new Artist(source);
            }
            @Override
            public Artist[] newArray(int size) {
                return new Artist[size];
            }
        };

        public Artist() {}

        protected Artist(Parcel in) {
            id = in.readString();
            name = in.readString();
            url = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(id);
            dest.writeString(name);
            dest.writeString(url);
        }
    }
}
