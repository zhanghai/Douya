/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ItemCollectionList extends BaseList<SimpleItemCollection> {

    @SerializedName("interests")
    public ArrayList<SimpleItemCollection> collections = new ArrayList<>();

    @Override
    public ArrayList<SimpleItemCollection> getList() {
        return collections;
    }


    public static final Creator<ItemCollectionList> CREATOR = new Creator<ItemCollectionList>() {
        @Override
        public ItemCollectionList createFromParcel(Parcel source) {
            return new ItemCollectionList(source);
        }
        @Override
        public ItemCollectionList[] newArray(int size) {
            return new ItemCollectionList[size];
        }
    };

    public ItemCollectionList() {}

    protected ItemCollectionList(Parcel in) {
        super(in);

        collections = in.createTypedArrayList(SimpleItemCollection.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeTypedList(collections);
    }
}
