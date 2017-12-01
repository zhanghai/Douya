/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class PhotoList extends BaseList<Photo> implements Parcelable {

    public ArrayList<Photo> photos = new ArrayList<>();

    @Override
    public ArrayList<Photo> getList() {
        return photos;
    }


    public static final Creator<PhotoList> CREATOR =
            new Creator<PhotoList>() {
                public PhotoList createFromParcel(Parcel source) {
                    return new PhotoList(source);
                }
                public PhotoList[] newArray(int size) {
                    return new PhotoList[size];
                }
            };

    public PhotoList() {}

    protected PhotoList(Parcel in) {
        super(in);

        photos = in.createTypedArrayList(Photo.CREATOR);
    }

    @Override
    public int describeContents() {
        return super.describeContents();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeTypedList(photos);
    }
}
