/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ItemCollection implements Parcelable {

    public String comment;

    @SerializedName("create_time")
    public String creationTime;

    public long id;

    public Rating rating;

    /**
     * @deprecated Use {@link #getState()} instead.
     */
    @SerializedName("status")
    public String state;

    public ArrayList<String> tags = new ArrayList<>();

    public ItemCollectionState getState() {
        //noinspection deprecation
        return ItemCollectionState.ofString(state);
    }


    public static final Parcelable.Creator<ItemCollection> CREATOR =
            new Parcelable.Creator<ItemCollection>() {
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
        comment = in.readString();
        creationTime = in.readString();
        id = in.readLong();
        rating = in.readParcelable(Rating.class.getClassLoader());
        //noinspection deprecation
        state = in.readString();
        tags = in.createStringArrayList();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(comment);
        dest.writeString(creationTime);
        dest.writeLong(id);
        dest.writeParcelable(rating, flags);
        //noinspection deprecation
        dest.writeString(state);
        dest.writeStringList(tags);
    }
}
