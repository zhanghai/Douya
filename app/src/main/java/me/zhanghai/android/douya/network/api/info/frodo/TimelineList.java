/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import me.zhanghai.android.douya.functional.Functional;
import me.zhanghai.android.douya.functional.ObjectsCompat;
import me.zhanghai.android.douya.util.CollectionUtils;

/**
 * {@code Timeline} in Frodo.
 */
public class TimelineList implements Parcelable {

    public int count;

    @SerializedName("hot_items")
    public ArrayList<TimelineItem> hotItems = new ArrayList<>();

    public ArrayList<TimelineItem> items = new ArrayList<>();

    @SerializedName("new_item_count")
    public int newItemCount;

    public String toast;

    @SerializedName("top_items")
    public ArrayList<TimelineItem> topItems = new ArrayList<>();

    public ArrayList<Broadcast> toBroadcastList() {
        List<TimelineItem> allItems = CollectionUtils.union(topItems, CollectionUtils.union(
                hotItems, items));
        return Functional.filter(Functional.map(allItems, TimelineItem::toBroadcast),
                ObjectsCompat::nonNull);
    }


    public static final Parcelable.Creator<TimelineList> CREATOR =
            new Parcelable.Creator<TimelineList>() {
                @Override
                public TimelineList createFromParcel(Parcel source) {
                    return new TimelineList(source);
                }
                @Override
                public TimelineList[] newArray(int size) {
                    return new TimelineList[size];
                }
            };

    public TimelineList() {}

    protected TimelineList(Parcel in) {
        count = in.readInt();
        in.readList(hotItems, TimelineItem.class.getClassLoader());
        in.readList(items, TimelineItem.class.getClassLoader());
        newItemCount = in.readInt();
        toast = in.readString();
        in.readList(topItems, TimelineItem.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(count);
        dest.writeList(hotItems);
        dest.writeList(items);
        dest.writeInt(newItemCount);
        dest.writeString(toast);
        dest.writeList(topItems);
    }
}
