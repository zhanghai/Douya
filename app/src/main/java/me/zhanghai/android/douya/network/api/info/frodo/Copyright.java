/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Copyright implements Parcelable {

    @SerializedName("author_name")
    public String authorName;

    @SerializedName("author_uri")
    public String authorUri;

    public String title;

    @SerializedName("type_cn")
    public String type;

    public String uri;


    public static final Parcelable.Creator<Copyright> CREATOR =
            new Parcelable.Creator<Copyright>() {
                @Override
                public Copyright createFromParcel(Parcel source) {
                    return new Copyright(source);
                }
                @Override
                public Copyright[] newArray(int size) {
                    return new Copyright[size];
                }
            };

    public Copyright() {}

    protected Copyright(Parcel in) {
        authorName = in.readString();
        authorUri = in.readString();
        title = in.readString();
        type = in.readString();
        uri = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(authorName);
        dest.writeString(authorUri);
        dest.writeString(title);
        dest.writeString(type);
        dest.writeString(uri);
    }
}
