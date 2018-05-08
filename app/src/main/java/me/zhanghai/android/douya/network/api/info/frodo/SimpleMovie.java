/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.content.Context;
import android.os.Parcel;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.DateTimeParseException;

import java.util.ArrayList;

import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.util.CollectionUtils;
import me.zhanghai.android.douya.util.TimeUtils;

public class SimpleMovie extends CollectableItem {

    public ArrayList<SimpleCelebrity> actors = new ArrayList<>();

    public ArrayList<SimpleCelebrity> directors = new ArrayList<>();

    public ArrayList<String> genres = new ArrayList<>();

    @SerializedName("has_linewatch")
    public boolean hasOnlineSource;

    @SerializedName("release_date")
    public String releaseDate;

    @SerializedName("pubdate")
    public ArrayList<String> releaseDates = new ArrayList<>();

    public String year;

    public String getYearMonth(Context context) {
        String yearMoth = CollectableItem.getYearMonth(releaseDates, context);
        if (!TextUtils.isEmpty(yearMoth)) {
            return yearMoth;
        }
        if (!TextUtils.isEmpty(year)) {
            return year + "年";
        }
        return null;
    }


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
        dest.writeString(releaseDate);
        dest.writeStringList(releaseDates);
        dest.writeString(year);
    }

    public SimpleMovie() {}

    protected SimpleMovie(Parcel in) {
        super(in);

        actors = in.createTypedArrayList(SimpleCelebrity.CREATOR);
        directors = in.createTypedArrayList(SimpleCelebrity.CREATOR);
        genres = in.createStringArrayList();
        hasOnlineSource = in.readByte() != 0;
        releaseDate = in.readString();
        releaseDates = in.createStringArrayList();
        year = in.readString();
    }
}
