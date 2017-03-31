/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import me.zhanghai.android.douya.util.StringCompat;

public abstract class BaseItem implements Parcelable {

    public long id;

    @SerializedName("pic")
    public Image cover;

    @SerializedName("sharing_url")
    public String shareUrl;

    public String title;

    public String type;

    public String uri;

    public String url;

    public static String getListAsString(List<String> list) {
        return StringCompat.join(" / ", list);
    }


    public BaseItem() {}

    protected BaseItem(Parcel in) {
        id = in.readLong();
        cover = in.readParcelable(Image.class.getClassLoader());
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
        dest.writeLong(id);
        dest.writeParcelable(cover, flags);
        dest.writeString(shareUrl);
        dest.writeString(title);
        dest.writeString(type);
        dest.writeString(uri);
        dest.writeString(url);
    }
}
