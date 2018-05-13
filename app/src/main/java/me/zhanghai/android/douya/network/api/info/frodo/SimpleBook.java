/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.content.Context;
import android.os.Parcel;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class SimpleBook extends CollectableItem {

    @SerializedName("author")
    public ArrayList<String> authors = new ArrayList<>();

    @SerializedName("has_ebook")
    public boolean hasEbook;

    @SerializedName("press")
    public ArrayList<String> presses = new ArrayList<>();

    @SerializedName("pubdate")
    public ArrayList<String> releaseDates = new ArrayList<>();

    public String getYearMonth(Context context) {
        return CollectableItem.getYearMonth(releaseDates, context);
    }


    public static final Creator<SimpleBook> CREATOR = new Creator<SimpleBook>() {
        @Override
        public SimpleBook createFromParcel(Parcel source) {
            return new SimpleBook(source);
        }
        @Override
        public SimpleBook[] newArray(int size) {
            return new SimpleBook[size];
        }
    };

    public SimpleBook() {}

    protected SimpleBook(Parcel in) {
        super(in);

        authors = in.createStringArrayList();
        hasEbook = in.readByte() != 0;
        presses = in.createStringArrayList();
        releaseDates = in.createStringArrayList();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeStringList(authors);
        dest.writeByte(hasEbook ? (byte) 1 : (byte) 0);
        dest.writeStringList(presses);
        dest.writeStringList(releaseDates);
    }
}
