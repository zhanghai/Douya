/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Broadcast implements Parcelable {

    @SerializedName("activity")
    public String action;

    @SerializedName("ad_info")
    public BroadcastAdInfo adInfo;

    public SimpleUser author;

    public BroadcastCard card;

    @SerializedName("comments_count")
    public int commentsCount;

    @SerializedName("create_time")
    public String createdAt;

    public ArrayList<TextEntity> entities = new ArrayList<>();

    @SerializedName("forbid_reshare_and_comment")
    public boolean isRebroadcastAndCommentForbidden;

    public String id;

    public ArrayList<SizedImage> images = new ArrayList<>();

    @SerializedName("is_status_ad")
    public boolean isStatusAd;

    @SerializedName("is_subscription")
    public boolean isSubscription;

    @SerializedName("like_count")
    public int likeCount;

    @SerializedName("liked")
    public boolean isLiked;

    @SerializedName("parent_id")
    public String parentBroadcastId;

    @SerializedName("parent_status")
    public Broadcast parentBroadcast;

    @SerializedName("reshare_id")
    public String rebroadcastId;

    @SerializedName("reshared_status")
    public Broadcast rebroadcastedBroadcast;

    @SerializedName("resharers_count")
    public int rebroadcasterCount;

    @SerializedName("reshares_count")
    public int rebroadcastCount;

    @SerializedName("sharing_url")
    public String shareUrl;

    @SerializedName("subscription_text")
    public String subscriptionText;

    public String text;

    public String uri;


    public static final Creator<Broadcast> CREATOR = new Creator<Broadcast>() {
        @Override
        public Broadcast createFromParcel(Parcel source) {
            return new Broadcast(source);
        }
        @Override
        public Broadcast[] newArray(int size) {
            return new Broadcast[size];
        }
    };

    public Broadcast() {}

    protected Broadcast(Parcel in) {
        action = in.readString();
        adInfo = in.readParcelable(BroadcastAdInfo.class.getClassLoader());
        author = in.readParcelable(SimpleUser.class.getClassLoader());
        card = in.readParcelable(BroadcastCard.class.getClassLoader());
        commentsCount = in.readInt();
        createdAt = in.readString();
        entities = in.createTypedArrayList(TextEntity.CREATOR);
        isRebroadcastAndCommentForbidden = in.readByte() != 0;
        id = in.readString();
        images = in.createTypedArrayList(SizedImage.CREATOR);
        isStatusAd = in.readByte() != 0;
        isSubscription = in.readByte() != 0;
        likeCount = in.readInt();
        isLiked = in.readByte() != 0;
        parentBroadcastId = in.readString();
        parentBroadcast = in.readParcelable(Broadcast.class.getClassLoader());
        rebroadcastId = in.readString();
        rebroadcastedBroadcast = in.readParcelable(Broadcast.class.getClassLoader());
        rebroadcasterCount = in.readInt();
        rebroadcastCount = in.readInt();
        shareUrl = in.readString();
        subscriptionText = in.readString();
        text = in.readString();
        uri = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(action);
        dest.writeParcelable(adInfo, flags);
        dest.writeParcelable(author, flags);
        dest.writeParcelable(card, flags);
        dest.writeInt(commentsCount);
        dest.writeString(createdAt);
        dest.writeTypedList(entities);
        dest.writeByte(isRebroadcastAndCommentForbidden ? (byte) 1 : (byte) 0);
        dest.writeString(id);
        dest.writeTypedList(images);
        dest.writeByte(isStatusAd ? (byte) 1 : (byte) 0);
        dest.writeByte(isSubscription ? (byte) 1 : (byte) 0);
        dest.writeInt(likeCount);
        dest.writeByte(isLiked ? (byte) 1 : (byte) 0);
        dest.writeString(parentBroadcastId);
        dest.writeParcelable(parentBroadcast, flags);
        dest.writeString(rebroadcastId);
        dest.writeParcelable(rebroadcastedBroadcast, flags);
        dest.writeInt(rebroadcasterCount);
        dest.writeInt(rebroadcastCount);
        dest.writeString(shareUrl);
        dest.writeString(subscriptionText);
        dest.writeString(text);
        dest.writeString(uri);
    }
}
