/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Game extends SimpleGame {

    @SerializedName("aliases")
    public ArrayList<String> alternativeTitles = new ArrayList<>();

    public ArrayList<String> developers = new ArrayList<>();

    @SerializedName("info_url")
    public String informationUrl;

    public ArrayList<String> publishers = new ArrayList<>();


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

        alternativeTitles = in.createStringArrayList();
        developers = in.createStringArrayList();
        informationUrl = in.readString();
        publishers = in.createStringArrayList();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeStringList(alternativeTitles);
        dest.writeStringList(developers);
        dest.writeString(informationUrl);
        dest.writeStringList(publishers);
    }
}
