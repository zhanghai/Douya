/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class SimpleReview extends BaseItem {

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


    @SerializedName("comments_count")
    public int commentCount;

    @SerializedName("create_time")
    public String createTime;

    @SerializedName("likers_count")
    public int likerCount;

    public SimpleRating rating;

    @SerializedName("rtype")
    public String rType;

    @SerializedName("spoiler")
    public boolean isSpoiler;

    @SerializedName("subject")
    public CollectableItem item;

    @SerializedName("timeline_share_count")
    public int shareCount;

    // TODO
    //public GalleryTopic topic;

    @SerializedName("useful_count")
    public int usefulCount;

    @SerializedName("useless_count")
    public int uselessCount;

    @SerializedName("user")
    public SimpleUser author;

    /**
     * @deprecated Use {@link #getVoteState()} instead.
     */
    @SerializedName("vote_status")
    public int voteState;

    public VoteState getVoteState() {
        //noinspection deprecation
        return VoteState.ofApiInt(voteState);
    }


    public static final Creator<SimpleReview> CREATOR = new Creator<SimpleReview>() {
        @Override
        public SimpleReview createFromParcel(Parcel source) {
            return new SimpleReview(source);
        }
        @Override
        public SimpleReview[] newArray(int size) {
            return new SimpleReview[size];
        }
    };

    public SimpleReview() {}

    protected SimpleReview(Parcel in) {
        super(in);
        commentCount = in.readInt();
        createTime = in.readString();
        likerCount = in.readInt();
        rating = in.readParcelable(SimpleRating.class.getClassLoader());
        rType = in.readString();
        isSpoiler = in.readByte() != 0;
        item = in.readParcelable(CollectableItem.class.getClassLoader());
        shareCount = in.readInt();
        usefulCount = in.readInt();
        uselessCount = in.readInt();
        author = in.readParcelable(SimpleUser.class.getClassLoader());
        //noinspection deprecation
        voteState = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(commentCount);
        dest.writeString(createTime);
        dest.writeInt(likerCount);
        dest.writeParcelable(rating, flags);
        dest.writeString(rType);
        dest.writeByte(isSpoiler ? (byte) 1 : (byte) 0);
        dest.writeParcelable(item, flags);
        dest.writeInt(shareCount);
        dest.writeInt(usefulCount);
        dest.writeInt(uselessCount);
        dest.writeParcelable(author, flags);
        //noinspection deprecation
        dest.writeInt(voteState);
    }


    public static class CensorshipInfo implements Parcelable {

        @SerializedName("info_url")
        public String infoUrl;


        public static final Creator<CensorshipInfo> CREATOR = new Creator<CensorshipInfo>() {
            @Override
            public CensorshipInfo createFromParcel(Parcel source) {
                return new CensorshipInfo(source);
            }
            @Override
            public CensorshipInfo[] newArray(int size) {
                return new CensorshipInfo[size];
            }
        };

        public CensorshipInfo() {}

        protected CensorshipInfo(Parcel in) {
            infoUrl = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(infoUrl);
        }
    }
}
