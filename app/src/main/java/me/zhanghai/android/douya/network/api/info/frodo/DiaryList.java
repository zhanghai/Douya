/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class DiaryList extends BaseList {

    @SerializedName("notes")
    public ArrayList<Diary> diaries = new ArrayList<>();


    public static final Creator<DiaryList> CREATOR = new Creator<DiaryList>() {
        @Override
        public DiaryList createFromParcel(Parcel source) {
            return new DiaryList(source);
        }
        @Override
        public DiaryList[] newArray(int size) {
            return new DiaryList[size];
        }
    };

    public DiaryList() {}

    protected DiaryList(Parcel in) {
        super(in);

        diaries = in.createTypedArrayList(Diary.CREATOR);
    }

    @Override
    public int describeContents() {
        return super.describeContents();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeTypedList(diaries);
    }
}
