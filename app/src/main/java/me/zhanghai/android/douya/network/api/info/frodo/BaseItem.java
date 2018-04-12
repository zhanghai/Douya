/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import me.zhanghai.android.douya.network.api.info.UrlGettable;
import me.zhanghai.android.douya.util.StringCompat;

/**
 * {@code BaseFeedableitem} in Frodo.
 */
public abstract class BaseItem implements UrlGettable, Parcelable {

    @SerializedName("abstract")
    public String abstract_;

    @SerializedName("cover_url")
    public String coverUrl;

    public long id;

    @SerializedName("sharing_url")
    public String shareUrl;

    public String title;

    public String type;

    public String uri;

    public String url;

    @Override
    public String getUrl() {
        return url;
    }

    public BaseItem() {}

    protected BaseItem(Parcel in) {
        abstract_ = in.readString();
        coverUrl = in.readString();
        id = in.readLong();
        shareUrl = in.readString();
        title = in.readString();
        type = in.readString();
        uri = in.readString();
        url = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(abstract_);
        dest.writeString(coverUrl);
        dest.writeLong(id);
        dest.writeString(shareUrl);
        dest.writeString(title);
        dest.writeString(type);
        dest.writeString(uri);
        dest.writeString(url);
    }
}
