/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * {@code AwardChannel} in Frodo.
 */
public class AwardType implements Parcelable {

    public ArrayList<AwardCategory> categories = new ArrayList<>();

    public String title;


    public static final Parcelable.Creator<AwardType> CREATOR =
            new Parcelable.Creator<AwardType>() {
                @Override
                public AwardType createFromParcel(Parcel source) {
                    return new AwardType(source);
                }
                @Override
                public AwardType[] newArray(int size) {
                    return new AwardType[size];
                }
            };

    public AwardType() {}

    protected AwardType(Parcel in) {
        categories = in.createTypedArrayList(AwardCategory.CREATOR);
        title = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(categories);
        dest.writeString(title);
    }
}
