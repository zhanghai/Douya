/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class UserItemList implements Parcelable {

    @SerializedName("itemlist")
    public ArrayList<UserItems> list = new ArrayList<>();


    public static final Parcelable.Creator<UserItemList> CREATOR =
            new Parcelable.Creator<UserItemList>() {
                @Override
                public UserItemList createFromParcel(Parcel source) {
                    return new UserItemList(source);
                }
                @Override
                public UserItemList[] newArray(int size) {
                    return new UserItemList[size];
                }
            };

    public UserItemList() {}

    protected UserItemList(Parcel in) {
        list = in.createTypedArrayList(UserItems.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(list);
    }
}
