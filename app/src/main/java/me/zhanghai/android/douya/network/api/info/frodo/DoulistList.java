/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;

import java.util.ArrayList;

public class DoulistList extends BaseList<Doulist> {

    public ArrayList<Doulist> doulists = new ArrayList<>();

    public String uri;

    @Override
    public ArrayList<Doulist> getList() {
        return doulists;
    }


    public static final Creator<DoulistList> CREATOR = new Creator<DoulistList>() {
        @Override
        public DoulistList createFromParcel(Parcel source) {
            return new DoulistList(source);
        }
        @Override
        public DoulistList[] newArray(int size) {
            return new DoulistList[size];
        }
    };

    public DoulistList() {}

    protected DoulistList(Parcel in) {
        super(in);

        doulists = in.createTypedArrayList(Doulist.CREATOR);
        uri = in.readString();
    }

    @Override
    public int describeContents() {
        return super.describeContents();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeTypedList(doulists);
        dest.writeString(uri);
    }
}
