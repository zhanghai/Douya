/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.content.Context;
import android.os.Parcel;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import me.zhanghai.android.douya.functional.Functional;

public class SimpleGame extends CollectableItem {

    public ArrayList<String> genres = new ArrayList<>();

    public ArrayList<GamePlatform> platforms = new ArrayList<>();

    @SerializedName("release_date")
    public String releaseDate;

    public List<String> getPlatformNames() {
        return Functional.map(platforms, platform -> platform.name);
    }

    public String getYearMonth(Context context) {
        return CollectableItem.getYearMonth(releaseDate, context);
    }


    public static final Creator<SimpleGame> CREATOR = new Creator<SimpleGame>() {
        @Override
        public SimpleGame createFromParcel(Parcel source) {
            return new SimpleGame(source);
        }
        @Override
        public SimpleGame[] newArray(int size) {
            return new SimpleGame[size];
        }
    };

    public SimpleGame() {}

    protected SimpleGame(Parcel in) {
        super(in);

        genres = in.createStringArrayList();
        platforms = in.createTypedArrayList(GamePlatform.CREATOR);
        releaseDate = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeStringList(genres);
        dest.writeTypedList(platforms);
        dest.writeString(releaseDate);
    }
}
