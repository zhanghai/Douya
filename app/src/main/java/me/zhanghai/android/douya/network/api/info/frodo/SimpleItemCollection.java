/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class SimpleItemCollection implements Parcelable {

    public String comment;

    @SerializedName("create_time")
    public String createTime;

    public long id;

    @SerializedName("is_voted")
    public boolean isVoted;

    public ArrayList<GamePlatform> platforms = new ArrayList<>();

    public SimpleRating rating;

    @SerializedName("sharing_text")
    public String shareText;

    @SerializedName("sharing_url")
    public String shareUrl;

    /**
     * @deprecated Use {@link #getState()} instead.
     */
    @SerializedName("status")
    public String state;

    public ArrayList<String> tags = new ArrayList<>();

    public String uri;

    public SimpleUser user;

    @SerializedName("vote_count")
    public int voteCount;

    public ItemCollectionState getState() {
        //noinspection deprecation
        return ItemCollectionState.ofString(state);
    }

    public static final Creator<SimpleItemCollection> CREATOR =
            new Creator<SimpleItemCollection>() {
                @Override
                public SimpleItemCollection createFromParcel(Parcel source) {
                    return new SimpleItemCollection(source);
                }
                @Override
                public SimpleItemCollection[] newArray(int size) {
                    return new SimpleItemCollection[size];
                }
            };

    public SimpleItemCollection() {}

    protected SimpleItemCollection(Parcel in) {
        comment = in.readString();
        createTime = in.readString();
        id = in.readLong();
        isVoted = in.readByte() != 0;
        platforms = in.createTypedArrayList(GamePlatform.CREATOR);
        rating = in.readParcelable(SimpleRating.class.getClassLoader());
        shareText = in.readString();
        shareUrl = in.readString();
        //noinspection deprecation
        state = in.readString();
        tags = in.createStringArrayList();
        uri = in.readString();
        user = in.readParcelable(SimpleUser.class.getClassLoader());
        voteCount = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(comment);
        dest.writeString(createTime);
        dest.writeLong(id);
        dest.writeByte(isVoted ? (byte) 1 : (byte) 0);
        dest.writeTypedList(platforms);
        dest.writeParcelable(rating, flags);
        dest.writeString(shareText);
        dest.writeString(shareUrl);
        //noinspection deprecation
        dest.writeString(state);
        dest.writeStringList(tags);
        dest.writeString(uri);
        dest.writeParcelable(user, flags);
        dest.writeInt(voteCount);
    }
}
