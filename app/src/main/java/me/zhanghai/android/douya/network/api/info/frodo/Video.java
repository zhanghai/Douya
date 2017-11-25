/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Video implements Parcelable {

    @SerializedName("html5_video_url")
    public String html5VideoUrl;

    @SerializedName("mp4_video_url")
    public String mp4VideoUrl;

    @SerializedName("pic_url")
    public String previewUrl;

    @SerializedName("source_name")
    public String sourceName;

    public String title;

    public String url;

    @SerializedName("youku_video_iframe_url")
    public String youkuVideoIFrameUrl;


    public static final Parcelable.Creator<Video> CREATOR = new Parcelable.Creator<Video>() {
        @Override
        public Video createFromParcel(Parcel source) {
            return new Video(source);
        }
        @Override
        public Video[] newArray(int size) {
            return new Video[size];
        }
    };

    public Video() {}

    protected Video(Parcel in) {
        html5VideoUrl = in.readString();
        mp4VideoUrl = in.readString();
        previewUrl = in.readString();
        sourceName = in.readString();
        title = in.readString();
        url = in.readString();
        youkuVideoIFrameUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(html5VideoUrl);
        dest.writeString(mp4VideoUrl);
        dest.writeString(previewUrl);
        dest.writeString(sourceName);
        dest.writeString(title);
        dest.writeString(url);
        dest.writeString(youkuVideoIFrameUrl);
    }
}
