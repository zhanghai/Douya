/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class NotificationCount implements Parcelable {

    @SerializedName("chat")
    public Item doumail;

    public Item group;

    @SerializedName("group_chat")
    public Item groupChat;

    @SerializedName("my_global")
    public Item mineTab;

    @SerializedName("recfeed")
    public Item feed;


    public static class Item implements Parcelable {

        public int count;

        public String version;


        public static final Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>() {
            @Override
            public Item createFromParcel(Parcel source) {
                return new Item(source);
            }

            @Override
            public Item[] newArray(int size) {
                return new Item[size];
            }
        };

        public Item() {}

        protected Item(Parcel in) {
            count = in.readInt();
            version = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(count);
            dest.writeString(version);
        }
    }


    public static final Parcelable.Creator<NotificationCount> CREATOR =
            new Parcelable.Creator<NotificationCount>() {
                @Override
                public NotificationCount createFromParcel(Parcel source) {
                    return new NotificationCount(source);
                }
                @Override
                public NotificationCount[] newArray(int size) {
                    return new NotificationCount[size];
                }
            };

    public NotificationCount() {}

    protected NotificationCount(Parcel in) {
        doumail = in.readParcelable(Item.class.getClassLoader());
        group = in.readParcelable(Item.class.getClassLoader());
        groupChat = in.readParcelable(Item.class.getClassLoader());
        mineTab = in.readParcelable(Item.class.getClassLoader());
        feed = in.readParcelable(Item.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(doumail, flags);
        dest.writeParcelable(group, flags);
        dest.writeParcelable(groupChat, flags);
        dest.writeParcelable(mineTab, flags);
        dest.writeParcelable(feed, flags);
    }
}
