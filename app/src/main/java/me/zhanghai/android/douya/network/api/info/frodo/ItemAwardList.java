/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;

import java.util.ArrayList;

public class ItemAwardList extends BaseList<ItemAwardItem> {

    public ArrayList<ItemAwardItem> awards = new ArrayList<>();

    @Override
    public ArrayList<ItemAwardItem> getList() {
        return awards;
    }


    public static final Creator<ItemAwardList> CREATOR = new Creator<ItemAwardList>() {
        @Override
        public ItemAwardList createFromParcel(Parcel source) {
            return new ItemAwardList(source);
        }
        @Override
        public ItemAwardList[] newArray(int size) {
            return new ItemAwardList[size];
        }
    };

    public ItemAwardList() {}

    protected ItemAwardList(Parcel in) {
        super(in);

        awards = in.createTypedArrayList(ItemAwardItem.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeTypedList(awards);
    }
}
