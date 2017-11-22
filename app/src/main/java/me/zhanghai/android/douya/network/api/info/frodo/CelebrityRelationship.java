/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class CelebrityRelationship implements Parcelable {

    public SimpleCelebrity celebrity;

    @SerializedName("info")
    public String relationship;


    public static final Parcelable.Creator<CelebrityRelationship> CREATOR =
            new Parcelable.Creator<CelebrityRelationship>() {
                @Override
                public CelebrityRelationship createFromParcel(Parcel source) {
                    return new CelebrityRelationship(source);
                }
                @Override
                public CelebrityRelationship[] newArray(int size) {
                    return new CelebrityRelationship[size];
                }
            };

    public CelebrityRelationship() {}

    protected CelebrityRelationship(Parcel in) {
        celebrity = in.readParcelable(SimpleCelebrity.class.getClassLoader());
        relationship = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(celebrity, flags);
        dest.writeString(relationship);
    }
}
