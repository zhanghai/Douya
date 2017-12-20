/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;

public class UnknownTimelineItem extends BaseTimelineItem {


    public static final Creator<UnknownTimelineItem> CREATOR = new Creator<UnknownTimelineItem>() {
        @Override
        public UnknownTimelineItem createFromParcel(Parcel source) {
            return new UnknownTimelineItem(source);
        }
        @Override
        public UnknownTimelineItem[] newArray(int size) {
            return new UnknownTimelineItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    public UnknownTimelineItem() {}

    protected UnknownTimelineItem(Parcel in) {
        super(in);
    }
}
