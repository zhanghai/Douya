/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;
import android.os.Parcelable;

public class UploadedImage implements Parcelable {

    public String id;

    public String url;


    public static final Creator<UploadedImage> CREATOR = new Creator<UploadedImage>() {
        @Override
        public UploadedImage createFromParcel(Parcel source) {
            return new UploadedImage(source);
        }
        @Override
        public UploadedImage[] newArray(int size) {
            return new UploadedImage[size];
        }
    };

    public UploadedImage() {}

    protected UploadedImage(Parcel in) {
        id = in.readString();
        url = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(url);
    }
}
