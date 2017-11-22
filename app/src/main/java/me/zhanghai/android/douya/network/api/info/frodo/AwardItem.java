/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * {@code Award} in Frodo.
 */
public class AwardItem implements Parcelable {

    public SimpleAwardCategory category;

    @SerializedName("ceremony")
    public Award award;

    @SerializedName("is_won")
    public boolean isWon;

    public SimpleMovie movie;

    public String type;


    public static final Parcelable.Creator<AwardItem> CREATOR =
            new Parcelable.Creator<AwardItem>() {
                @Override
                public AwardItem createFromParcel(Parcel source) {
                    return new AwardItem(source);
                }
                @Override
                public AwardItem[] newArray(int size) {
                    return new AwardItem[size];
                }
            };

    public AwardItem() {}

    protected AwardItem(Parcel in) {
        category = in.readParcelable(SimpleAwardCategory.class.getClassLoader());
        award = in.readParcelable(Award.class.getClassLoader());
        isWon = in.readByte() != 0;
        movie = in.readParcelable(SimpleMovie.class.getClassLoader());
        type = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(category, flags);
        dest.writeParcelable(award, flags);
        dest.writeByte(isWon ? (byte) 1 : (byte) 0);
        dest.writeParcelable(movie, flags);
        dest.writeString(type);
    }
}
