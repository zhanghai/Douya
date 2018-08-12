/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;

import java.util.ArrayList;

public class LikerList extends BaseList<SimpleUser> {

    public ArrayList<SimpleUser> likers = new ArrayList<>();

    @Override
    public ArrayList<SimpleUser> getList() {
        return likers;
    }


    public static final Creator<LikerList> CREATOR = new Creator<LikerList>() {
        @Override
        public LikerList createFromParcel(Parcel source) {
            return new LikerList(source);
        }
        @Override
        public LikerList[] newArray(int size) {
            return new LikerList[size];
        }
    };

    public LikerList() {}

    protected LikerList(Parcel in) {
        super(in);

        likers = in.createTypedArrayList(SimpleUser.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeTypedList(likers);
    }
}
