/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;
import android.os.Parcelable;

public class AwardWinner implements Parcelable {

    public Movie movie;

    public BaseItem winner;


    public static final Parcelable.Creator<AwardWinner> CREATOR =
            new Parcelable.Creator<AwardWinner>() {
                @Override
                public AwardWinner createFromParcel(Parcel source) {
                    return new AwardWinner(source);
                }
                @Override
                public AwardWinner[] newArray(int size) {
                    return new AwardWinner[size];
                }
            };

    public AwardWinner() {}

    protected AwardWinner(Parcel in) {
        movie = in.readParcelable(Movie.class.getClassLoader());
        winner = in.readParcelable(BaseItem.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(movie, flags);
        dest.writeParcelable(winner, flags);
    }
}
