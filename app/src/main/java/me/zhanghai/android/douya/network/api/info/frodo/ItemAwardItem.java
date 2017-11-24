/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ItemAwardItem implements Parcelable {

    public ArrayList<ItemAwardCategoryItem> categories = new ArrayList<>();

    @SerializedName("ceremony")
    public Award award;


    public static final Parcelable.Creator<ItemAwardItem> CREATOR =
            new Parcelable.Creator<ItemAwardItem>() {
                @Override
                public ItemAwardItem createFromParcel(Parcel source) {
                    return new ItemAwardItem(source);
                }
                @Override
                public ItemAwardItem[] newArray(int size) {
                    return new ItemAwardItem[size];
                }
            };

    public ItemAwardItem() {}

    protected ItemAwardItem(Parcel in) {
        categories = in.createTypedArrayList(ItemAwardCategoryItem.CREATOR);
        award = in.readParcelable(Award.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(categories);
        dest.writeParcelable(award, flags);
    }
}
