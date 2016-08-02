/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Review implements Parcelable {

    public enum VoteState {

        NONE,
        USEFUL,
        USELESS;

        public static VoteState ofApiInt(int apiInt, VoteState defaultValue) {
            if (apiInt >= 0 && apiInt < values().length) {
                return values()[apiInt];
            } else {
                return defaultValue;
            }
        }

        public static VoteState ofApiInt(int apiInt) {
            return ofApiInt(apiInt, NONE);
        }
    }


    @SerializedName("abstract")
    public String abstract_;

    @SerializedName("allow_donate")
    public boolean allowDonate;

    @SerializedName("comments_count")
    public int commentCount;

    public String content;

    @SerializedName("cover_url")
    public String cover;

    @SerializedName("create_time")
    public String createdAt;

    @SerializedName("donate_count")
    public int donationCount;

    public long id;

    @SerializedName("is_donated")
    public boolean isDonated;

    @SerializedName("is_original")
    public boolean isOriginal;

    public ArrayList<SizedPhoto> photos = new ArrayList<>();

    public List<GamePlatform> platforms = new ArrayList<>();

    public Rating rating;

    @SerializedName("rtype")
    public String rType;

    @SerializedName("sharing_url")
    public String shareUrl;

    @SerializedName("spoiler")
    public boolean isSpoiler;

    @SerializedName("subject")
    public Item item;

    public String title;

    public String type;

    @SerializedName("type_name")
    public String typeName;

    public String uri;

    public String url;

    @SerializedName("useful_count")
    public int usefulCount;

    @SerializedName("useless_count")
    public int uselessCount;

    @SerializedName("user")
    public User author;

    /**
     * @deprecated Use {@link #getVoteState()} instead.
     */
    @SerializedName("vote_status")
    public int voteState;

    public VoteState getVoteState() {
        //noinspection deprecation
        return VoteState.ofApiInt(voteState);
    }


    public static final Parcelable.Creator<Review> CREATOR = new Parcelable.Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel source) {
            return new Review(source);
        }
        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    public Review() {}

    protected Review(Parcel in) {
        abstract_ = in.readString();
        allowDonate = in.readByte() != 0;
        commentCount = in.readInt();
        content = in.readString();
        cover = in.readString();
        createdAt = in.readString();
        donationCount = in.readInt();
        id = in.readLong();
        isDonated = in.readByte() != 0;
        isOriginal = in.readByte() != 0;
        photos = in.createTypedArrayList(SizedPhoto.CREATOR);
        platforms = in.createTypedArrayList(GamePlatform.CREATOR);
        rating = in.readParcelable(Rating.class.getClassLoader());
        rType = in.readString();
        shareUrl = in.readString();
        isSpoiler = in.readByte() != 0;
        item = in.readParcelable(Item.class.getClassLoader());
        title = in.readString();
        type = in.readString();
        typeName = in.readString();
        uri = in.readString();
        url = in.readString();
        usefulCount = in.readInt();
        uselessCount = in.readInt();
        author = in.readParcelable(User.class.getClassLoader());
        //noinspection deprecation
        voteState = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(abstract_);
        dest.writeByte(allowDonate ? (byte) 1 : (byte) 0);
        dest.writeInt(commentCount);
        dest.writeString(content);
        dest.writeString(cover);
        dest.writeString(createdAt);
        dest.writeInt(donationCount);
        dest.writeLong(id);
        dest.writeByte(isDonated ? (byte) 1 : (byte) 0);
        dest.writeByte(isOriginal ? (byte) 1 : (byte) 0);
        dest.writeTypedList(photos);
        dest.writeTypedList(platforms);
        dest.writeParcelable(rating, flags);
        dest.writeString(rType);
        dest.writeString(shareUrl);
        dest.writeByte(isSpoiler ? (byte) 1 : (byte) 0);
        dest.writeParcelable(item, flags);
        dest.writeString(title);
        dest.writeString(type);
        dest.writeString(typeName);
        dest.writeString(uri);
        dest.writeString(url);
        dest.writeInt(usefulCount);
        dest.writeInt(uselessCount);
        dest.writeParcelable(author, flags);
        //noinspection deprecation
        dest.writeInt(voteState);
    }
}
