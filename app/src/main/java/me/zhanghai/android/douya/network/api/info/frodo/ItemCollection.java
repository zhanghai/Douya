/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * {@code Interest} in Frodo.
 */
public class ItemCollection extends SimpleItemCollection {

    public static final int MAX_COMMENT_LENGTH = 140;

    @SerializedName("attend_time")
    public String attendAt;

    @SerializedName("done_index")
    public int index;

    @SerializedName("index")
    public int indexAll;

    @SerializedName("popular_tags")
    public ArrayList<String> popularTags = new ArrayList<>();

    @SerializedName("subject")
    public CollectableItem item;


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
        super(in);

        attendAt = in.readString();
        index = in.readInt();
        indexAll = in.readInt();
        popularTags = in.createStringArrayList();
        item = in.readParcelable(CollectableItem.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeString(attendAt);
        dest.writeInt(index);
        dest.writeInt(indexAll);
        dest.writeStringList(popularTags);
        dest.writeParcelable(item, flags);
    }
}
