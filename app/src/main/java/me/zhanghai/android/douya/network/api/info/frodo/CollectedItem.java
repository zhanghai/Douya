/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class CollectedItem implements Parcelable {

    @SerializedName("attend_time")
    public String attendanceTime;

    public String comment;

    @SerializedName("create_time")
    public String creationTime;

    @SerializedName("done_index")
    public int doneIndex;

    public long id;

    @SerializedName("index")
    public int collectedIndex;

    public ArrayList<GamePlatform> platforms = new ArrayList<>();

    @SerializedName("popular_tags")
    public ArrayList<String> popularTags = new ArrayList<>();

    public SimpleRating rating;

    @SerializedName("sharing_url")
    public String shareUrl;

    /**
     * @deprecated Use {@link #getState()} instead.
     */
    @SerializedName("status")
    public String state;

    @SerializedName("is_voted")
    public boolean isVoted;

    @SerializedName("subject")
    public CollectableItem item;

    public ArrayList<String> tags = new ArrayList<>();

    @SerializedName("vote_count")
    public int voteCount;

    public ItemCollectionState getState() {
        //noinspection deprecation
        return ItemCollectionState.ofString(state);
    }


    public static final Parcelable.Creator<CollectedItem> CREATOR =
            new Parcelable.Creator<CollectedItem>() {
                @Override
                public CollectedItem createFromParcel(Parcel source) {
                    return new CollectedItem(source);
                }
                @Override
                public CollectedItem[] newArray(int size) {
                    return new CollectedItem[size];
                }
            };

    public CollectedItem() {}

    protected CollectedItem(Parcel in) {
        attendanceTime = in.readString();
        comment = in.readString();
        creationTime = in.readString();
        doneIndex = in.readInt();
        id = in.readLong();
        collectedIndex = in.readInt();
        platforms = in.createTypedArrayList(GamePlatform.CREATOR);
        popularTags = in.createStringArrayList();
        rating = in.readParcelable(SimpleRating.class.getClassLoader());
        shareUrl = in.readString();
        //noinspection deprecation
        state = in.readString();
        isVoted = in.readByte() != 0;
        item = in.readParcelable(CollectableItem.class.getClassLoader());
        tags = in.createStringArrayList();
        voteCount = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(attendanceTime);
        dest.writeString(comment);
        dest.writeString(creationTime);
        dest.writeInt(doneIndex);
        dest.writeLong(id);
        dest.writeInt(collectedIndex);
        dest.writeTypedList(platforms);
        dest.writeStringList(popularTags);
        dest.writeParcelable(rating, flags);
        dest.writeString(shareUrl);
        //noinspection deprecation
        dest.writeString(state);
        dest.writeByte(isVoted ? (byte) 1 : (byte) 0);
        dest.writeParcelable(item, flags);
        dest.writeStringList(tags);
        dest.writeInt(voteCount);
    }
}
