/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.apiv2;

import android.os.Parcel;
import android.os.Parcelable;

public abstract class BaseList implements Parcelable {

    public int count;

    public int start;

    public int total;


    public BaseList() {}

    protected BaseList(Parcel in) {
        count = in.readInt();
        start = in.readInt();
        total = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(count);
        dest.writeInt(start);
        dest.writeInt(total);
    }
}
