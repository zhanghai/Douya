/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.apiv2;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Attachment implements Parcelable {

    @SerializedName("desc")
    public String description;

    public String href;

    public String image;

    public String title;

    public String type;


    public static final Parcelable.Creator<Attachment> CREATOR =
            new Parcelable.Creator<Attachment>() {
                public Attachment createFromParcel(Parcel source) {
                    return new Attachment(source);
                }
                public Attachment[] newArray(int size) {
                    return new Attachment[size];
                }
            };

    public Attachment() {}

    protected Attachment(Parcel in) {
        description = in.readString();
        href = in.readString();
        image = in.readString();
        title = in.readString();
        type = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(description);
        dest.writeString(href);
        dest.writeString(image);
        dest.writeString(title);
        dest.writeString(type);
    }
}
