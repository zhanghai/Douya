/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class RebroadcastList extends BaseList<RebroadcastItem> {

    @SerializedName("items")
    public ArrayList<RebroadcastItem> rebroadcasts = new ArrayList<>();

    @Override
    public ArrayList<RebroadcastItem> getList() {
        return rebroadcasts;
    }


    public static final Creator<RebroadcastList> CREATOR = new Creator<RebroadcastList>() {
        @Override
        public RebroadcastList createFromParcel(Parcel source) {
            return new RebroadcastList(source);
        }
        @Override
        public RebroadcastList[] newArray(int size) {
            return new RebroadcastList[size];
        }
    };

    public RebroadcastList() {}

    protected RebroadcastList(Parcel in) {
        super(in);

        rebroadcasts = in.createTypedArrayList(RebroadcastItem.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeTypedList(rebroadcasts);
    }
}
