/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class BroadcastTimelineItem extends BaseTimelineItem implements Parcelable {

    public ArrayList<Comment> comments = new ArrayList<>();

    @SerializedName("status")
    public Broadcast broadcast;


    public static final Creator<BroadcastTimelineItem> CREATOR =
            new Creator<BroadcastTimelineItem>() {
                @Override
                public BroadcastTimelineItem createFromParcel(Parcel source) {
                    return new BroadcastTimelineItem(source);
                }
                @Override
                public BroadcastTimelineItem[] newArray(int size) {
                    return new BroadcastTimelineItem[size];
                }
            };

    public BroadcastTimelineItem() {}

    protected BroadcastTimelineItem(Parcel in) {
        super(in);

        comments = in.createTypedArrayList(Comment.CREATOR);
        broadcast = in.readParcelable(Broadcast.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeTypedList(comments);
        dest.writeParcelable(broadcast, flags);
    }
}
