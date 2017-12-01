/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * {@code SubjectEpisode} in Frodo.
 */
public class ItemEpisode implements Parcelable {

    @SerializedName("broadcast_date")
    public String date;

    @SerializedName("last_episode_number")
    public int lastOrdinal;

    @SerializedName("number")
    public int ordinal;

    public String summary;

    public String title;


    public static final Parcelable.Creator<ItemEpisode> CREATOR =
            new Parcelable.Creator<ItemEpisode>() {
                @Override
                public ItemEpisode createFromParcel(Parcel source) {
                    return new ItemEpisode(source);
                }
                @Override
                public ItemEpisode[] newArray(int size) {
                    return new ItemEpisode[size];
                }
            };

    public ItemEpisode() {}

    protected ItemEpisode(Parcel in) {
        date = in.readString();
        lastOrdinal = in.readInt();
        ordinal = in.readInt();
        summary = in.readString();
        title = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(date);
        dest.writeInt(lastOrdinal);
        dest.writeInt(ordinal);
        dest.writeString(summary);
        dest.writeString(title);
    }
}
