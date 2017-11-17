/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;
import android.os.Parcelable;

public class SimpleRating implements Parcelable {

    public int count;

    public int max;

    public float value;


    public static final Parcelable.Creator<SimpleRating> CREATOR =
            new Parcelable.Creator<SimpleRating>() {
                @Override
                public SimpleRating createFromParcel(Parcel source) {
                    return new SimpleRating(source);
                }
                @Override
                public SimpleRating[] newArray(int size) {
                    return new SimpleRating[size];
                }
            };

    public SimpleRating() {}

    protected SimpleRating(Parcel in) {
        count = in.readInt();
        max = in.readInt();
        value = in.readFloat();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(count);
        dest.writeInt(max);
        dest.writeFloat(value);
    }
}
