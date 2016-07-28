/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

public class Diary implements Parcelable {

    public enum Visibility {

        PRIVATE("X"),
        PROTECTED("S"),
        PUBLIC("P");

        private String apiString;

        Visibility(String apiString) {
            this.apiString = apiString;
        }

        public static Visibility ofString(String apiString, Visibility defaultValue) {
            for (Visibility visibility : Visibility.values()) {
                if (TextUtils.equals(visibility.apiString, apiString)) {
                    return visibility;
                }
            }
            return defaultValue;
        }

        public static Visibility ofString(String apiString) {
            return ofString(apiString, PUBLIC);
        }
    }

    @SerializedName("abstract")
    public String abstract_;

    @SerializedName("allow_comment")
    public boolean allowComment;

    @SerializedName("allow_donate")
    public boolean allowDonate;

    public User author;

    @SerializedName("comments_count")
    public int commentCount;

    @SerializedName("cover_url")
    public String cover;

    @SerializedName("create_time")
    public String createdAt;

    /**
     * @deprecated Use {@link #getVisibility()} instead.
     */
    @SerializedName("domain")
    public String visibility;

    @SerializedName("donate_count")
    public int donationCount;

    public long id;

    @SerializedName("is_donated")
    public boolean isDonated;

    @SerializedName("is_original")
    public boolean isOriginal;

    @SerializedName("likers_count")
    public int likerCount;

    @SerializedName("sharing_url")
    public String shareUrl;

    public String title;

    public String type;

    @SerializedName("update_time")
    public String updatedAt;

    public String uri;

    public String url;

    public Visibility getVisibility() {
        //noinspection deprecation
        return Visibility.ofString(visibility);
    }


    public static final Creator<Diary> CREATOR = new Creator<Diary>() {
        @Override
        public Diary createFromParcel(Parcel source) {
            return new Diary(source);
        }
        @Override
        public Diary[] newArray(int size) {
            return new Diary[size];
        }
    };

    public Diary() {}

    protected Diary(Parcel in) {
        abstract_ = in.readString();
        allowComment = in.readByte() != 0;
        allowDonate = in.readByte() != 0;
        author = in.readParcelable(User.class.getClassLoader());
        commentCount = in.readInt();
        cover = in.readString();
        createdAt = in.readString();
        //noinspection deprecation
        visibility = in.readString();
        donationCount = in.readInt();
        id = in.readLong();
        isDonated = in.readByte() != 0;
        isOriginal = in.readByte() != 0;
        likerCount = in.readInt();
        shareUrl = in.readString();
        title = in.readString();
        type = in.readString();
        updatedAt = in.readString();
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
        dest.writeByte(allowComment ? (byte) 1 : (byte) 0);
        dest.writeByte(allowDonate ? (byte) 1 : (byte) 0);
        dest.writeParcelable(author, flags);
        dest.writeInt(commentCount);
        dest.writeString(cover);
        dest.writeString(createdAt);
        //noinspection deprecation
        dest.writeString(visibility);
        dest.writeInt(donationCount);
        dest.writeLong(id);
        dest.writeByte(isDonated ? (byte) 1 : (byte) 0);
        dest.writeByte(isOriginal ? (byte) 1 : (byte) 0);
        dest.writeInt(likerCount);
        dest.writeString(shareUrl);
        dest.writeString(title);
        dest.writeString(type);
        dest.writeString(updatedAt);
        dest.writeString(uri);
        dest.writeString(url);
    }
}
