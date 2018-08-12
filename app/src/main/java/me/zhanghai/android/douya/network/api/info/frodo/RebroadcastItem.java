/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.content.Context;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import me.zhanghai.android.douya.R;

public class RebroadcastItem implements Parcelable {

    public SimpleUser author;

    /**
     * @deprecated Use {@link #getText(Context)} instead.
     */
    public String text;

    @SerializedName("create_time")
    public String createTime;

    public String uri;


    public String getText(Context context) {
        //noinspection deprecation
        return hasBroadcast() ? text : context.getString(
                R.string.broadcast_rebroadcasts_simple_rebroadcast_text);
    }

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
        //noinspection deprecation
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
        //noinspection deprecation
        dest.writeString(text);
        dest.writeString(createTime);
        dest.writeString(uri);
    }
}
