/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class SizedPhoto implements Parcelable {

    public String description;

    public long id;

    public SizedImage image;

    @SerializedName("tag_name")
    public String tag;


    public static final Parcelable.Creator<SizedPhoto> CREATOR =
            new Parcelable.Creator<SizedPhoto>() {
                @Override
                public SizedPhoto createFromParcel(Parcel source) {
                    return new SizedPhoto(source);
                }
                @Override
                public SizedPhoto[] newArray(int size) {
                    return new SizedPhoto[size];
                }
            };

    public SizedPhoto() {}

    protected SizedPhoto(Parcel in) {
        description = in.readString();
        id = in.readLong();
        image = in.readParcelable(SizedImage.class.getClassLoader());
        tag = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(description);
        dest.writeLong(id);
        dest.writeParcelable(image, flags);
        dest.writeString(tag);
    }
}
