/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * {@code Interest} in Frodo.
 */
public class ItemCollection implements Parcelable {

    @SerializedName("attend_time")
    public String attendAt;

    public String comment;

    @SerializedName("create_time")
    public String createdAt;

    public long id;

    @SerializedName("done_index")
    public int index;

    @SerializedName("index")
    public int indexAll;

    @SerializedName("is_voted")
    public boolean isVoted;

    public ArrayList<GamePlatform> platforms = new ArrayList<>();

    @SerializedName("popular_tags")
    public ArrayList<String> popularTags = new ArrayList<>();

    public SimpleRating rating;

    @SerializedName("sharing_text")
    public String sharingText;

    @SerializedName("sharing_url")
    public String sharingUrl;

    /**
     * @deprecated Use {@link #getState()} instead.
     */
    @SerializedName("status")
    public String state;

    @SerializedName("subject")
    public CollectableItem item;

    public ArrayList<String> tags = new ArrayList<>();

    public String uri;

    public SimpleUser user;

    @SerializedName("vote_count")
    public int voteCount;

    public ItemCollectionState getState() {
        //noinspection deprecation
        return ItemCollectionState.ofString(state);
    }


    public static final Creator<ItemCollection> CREATOR = new Creator<ItemCollection>() {
        @Override
        public ItemCollection createFromParcel(Parcel source) {
            return new ItemCollection(source);
        }
        @Override
        public ItemCollection[] newArray(int size) {
            return new ItemCollection[size];
        }
    };

    public ItemCollection() {}

    protected ItemCollection(Parcel in) {
        attendAt = in.readString();
        comment = in.readString();
        createdAt = in.readString();
        id = in.readLong();
        index = in.readInt();
        indexAll = in.readInt();
        isVoted = in.readByte() != 0;
        platforms = in.createTypedArrayList(GamePlatform.CREATOR);
        popularTags = in.createStringArrayList();
        rating = in.readParcelable(SimpleRating.class.getClassLoader());
        sharingText = in.readString();
        sharingUrl = in.readString();
        //noinspection deprecation
        state = in.readString();
        item = in.readParcelable(CollectableItem.class.getClassLoader());
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
        dest.writeString(attendAt);
        dest.writeString(comment);
        dest.writeString(createdAt);
        dest.writeLong(id);
        dest.writeInt(index);
        dest.writeInt(indexAll);
        dest.writeByte(isVoted ? (byte) 1 : (byte) 0);
        dest.writeTypedList(platforms);
        dest.writeStringList(popularTags);
        dest.writeParcelable(rating, flags);
        dest.writeString(sharingText);
        dest.writeString(sharingUrl);
        //noinspection deprecation
        dest.writeString(state);
        dest.writeParcelable(item, flags);
        dest.writeStringList(tags);
        dest.writeString(uri);
        dest.writeParcelable(user, flags);
        dest.writeInt(voteCount);
    }
}
