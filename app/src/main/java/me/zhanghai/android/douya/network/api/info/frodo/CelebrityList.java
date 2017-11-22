/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class CelebrityList implements Parcelable {

    public ArrayList<Celebrity> actors = new ArrayList<>();

    public ArrayList<Celebrity> directors = new ArrayList<>();

    public int total;


    public static final Parcelable.Creator<CelebrityList> CREATOR =
            new Parcelable.Creator<CelebrityList>() {
                @Override
                public CelebrityList createFromParcel(Parcel source) {
                    return new CelebrityList(source);
                }
                @Override
                public CelebrityList[] newArray(int size) {
                    return new CelebrityList[size];
                }
            };

    public CelebrityList() {}

    protected CelebrityList(Parcel in) {
        actors = in.createTypedArrayList(Celebrity.CREATOR);
        directors = in.createTypedArrayList(Celebrity.CREATOR);
        total = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(actors);
        dest.writeTypedList(directors);
        dest.writeInt(total);
    }
}
