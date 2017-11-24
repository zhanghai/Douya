/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ItemAwardCategoryItem implements Parcelable {

    public SimpleAwardCategory category;

    public ArrayList<SimpleCelebrity> celebrities = new ArrayList<>();

    @SerializedName("is_won")
    public boolean isWon;


    public static final Parcelable.Creator<ItemAwardCategoryItem> CREATOR =
            new Parcelable.Creator<ItemAwardCategoryItem>() {
                @Override
                public ItemAwardCategoryItem createFromParcel(Parcel source) {
                    return new ItemAwardCategoryItem(source);
                }
                @Override
                public ItemAwardCategoryItem[] newArray(int size) {
                    return new ItemAwardCategoryItem[size];
                }
            };

    public ItemAwardCategoryItem() {}

    protected ItemAwardCategoryItem(Parcel in) {
        category = in.readParcelable(SimpleAwardCategory.class.getClassLoader());
        celebrities = in.createTypedArrayList(SimpleCelebrity.CREATOR);
        isWon = in.readByte() != 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(category, flags);
        dest.writeTypedList(celebrities);
        dest.writeByte(isWon ? (byte) 1 : (byte) 0);
    }
}
