/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;
import android.os.Parcelable;

public class SimpleAwardCategory implements Parcelable {

    public String title;


    public static final Parcelable.Creator<SimpleAwardCategory> CREATOR =
            new Parcelable.Creator<SimpleAwardCategory>() {
                @Override
                public SimpleAwardCategory createFromParcel(Parcel source) {
                    return new SimpleAwardCategory(source);
                }
                @Override
                public SimpleAwardCategory[] newArray(int size) {
                    return new SimpleAwardCategory[size];
                }
            };

    public SimpleAwardCategory() {}

    protected SimpleAwardCategory(Parcel in) {
        title = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
    }
}
