/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class BroadcastAdInfo implements Parcelable {

    @SerializedName("ad_id")
    public String adId;

    @SerializedName("impression_url")
    public String impressionUrl;

    @SerializedName("monitor_urls")
    public ArrayList<String> monitorUrls = new ArrayList<>();


    public static final Parcelable.Creator<BroadcastAdInfo> CREATOR =
            new Parcelable.Creator<BroadcastAdInfo>() {
                @Override
                public BroadcastAdInfo createFromParcel(Parcel source) {
                    return new BroadcastAdInfo(source);
                }
                @Override
                public BroadcastAdInfo[] newArray(int size) {
                    return new BroadcastAdInfo[size];
                }
            };

    public BroadcastAdInfo() {}

    protected BroadcastAdInfo(Parcel in) {
        adId = in.readString();
        impressionUrl = in.readString();
        monitorUrls = in.createStringArrayList();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(adId);
        dest.writeString(impressionUrl);
        dest.writeStringList(monitorUrls);
    }
}
