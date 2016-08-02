/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;
import android.os.Parcelable;

public class SizedImageItem implements Parcelable {

    public int height;

    public String url;

    public int width;


    public static final Parcelable.Creator<SizedImageItem> CREATOR =
            new Parcelable.Creator<SizedImageItem>() {
                @Override
                public SizedImageItem createFromParcel(Parcel source) {
                    return new SizedImageItem(source);
                }
                @Override
                public SizedImageItem[] newArray(int size) {
                    return new SizedImageItem[size];
                }
            };

    public SizedImageItem() {}

    protected SizedImageItem(Parcel in) {
        height = in.readInt();
        url = in.readString();
        width = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(height);
        dest.writeString(url);
        dest.writeInt(width);
    }
}
