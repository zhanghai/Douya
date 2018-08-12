/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

public class RebroadcastItem implements Parcelable {

    public SimpleUser author;

    public String text;

    @SerializedName("create_time")
    public String createTime;

    public String uri;


    public boolean hasBroadcast() {
        return !TextUtils.isEmpty(uri);
    }

    public long getBroadcastId() {
        return Long.parseLong(Uri.parse(uri).getLastPathSegment());
    }


    public static final Creator<RebroadcastItem> CREATOR = new Creator<RebroadcastItem>() {
        @Override
        public RebroadcastItem createFromParcel(Parcel source) {
            return new RebroadcastItem(source);
        }
        @Override
        public RebroadcastItem[] newArray(int size) {
            return new RebroadcastItem[size];
        }
    };

    public RebroadcastItem() {}

    protected RebroadcastItem(Parcel in) {
        author = in.readParcelable(SimpleUser.class.getClassLoader());
        text = in.readString();
        createTime = in.readString();
        uri = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(author, flags);
        dest.writeString(text);
        dest.writeString(createTime);
        dest.writeString(uri);
    }
}
