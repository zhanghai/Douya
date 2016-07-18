/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class NotificationList implements Parcelable {

    public int count;

    public ArrayList<Notification> notifications = new ArrayList<>();

    public int start;


    public static final Creator<NotificationList> CREATOR = new Creator<NotificationList>() {

        public NotificationList createFromParcel(Parcel source) {
            return new NotificationList(source);
        }

        public NotificationList[] newArray(int size) {
            return new NotificationList[size];
        }
    };

    public NotificationList() {}

    protected NotificationList(Parcel in) {
        count = in.readInt();
        notifications = in.createTypedArrayList(Notification.CREATOR);
        start = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(count);
        dest.writeTypedList(notifications);
        dest.writeInt(start);
    }
}
