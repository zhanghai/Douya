/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.content.Context;
import android.os.Parcel;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Game extends CollectableItem {

    public ArrayList<String> aliases = new ArrayList<>();

    public ArrayList<String> developers = new ArrayList<>();

    public ArrayList<String> genres = new ArrayList<>();

    @SerializedName("info_url")
    public String informationUrl;

    public ArrayList<GamePlatform> platforms = new ArrayList<>();

    public ArrayList<String> publishers = new ArrayList<>();

    @SerializedName("release_date")
    public String releaseDate;

    public String getYearMonth(Context context) {
        List<String> releaseDates = TextUtils.isEmpty(releaseDate) ? Collections.EMPTY_LIST
                : Collections.singletonList(releaseDate);
        return CollectableItem.getYearMonth(releaseDates, context);
    }


    public static final Creator<Game> CREATOR = new Creator<Game>() {
        @Override
        public Game createFromParcel(Parcel source) {
            return new Game(source);
        }
        @Override
        public Game[] newArray(int size) {
            return new Game[size];
        }
    };

    public Game() {}

    protected Game(Parcel in) {
        super(in);

        aliases = in.createStringArrayList();
        developers = in.createStringArrayList();
        genres = in.createStringArrayList();
        informationUrl = in.readString();
        platforms = in.createTypedArrayList(GamePlatform.CREATOR);
        publishers = in.createStringArrayList();
        releaseDate = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeStringList(aliases);
        dest.writeStringList(developers);
        dest.writeStringList(genres);
        dest.writeString(informationUrl);
        dest.writeTypedList(platforms);
        dest.writeStringList(publishers);
        dest.writeString(releaseDate);
    }
}
