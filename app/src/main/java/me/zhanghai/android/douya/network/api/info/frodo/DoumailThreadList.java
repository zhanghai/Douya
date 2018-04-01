/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * {@code ChatList} in Frodo.
 */
public class DoumailThreadList extends BaseList<DoumailThread> {

    @SerializedName("results")
    public ArrayList<DoumailThread> doumailThreads = new ArrayList<>();

    @SerializedName("group_chat_total")
    public int groupChatCount;

    //@SerializedName("sync_id")
    //public SyncMeta syncMeta;

    @Override
    public ArrayList<DoumailThread> getList() {
        return doumailThreads;
    }


    public static final Creator<DoumailThreadList> CREATOR = new Creator<DoumailThreadList>() {
        @Override
        public DoumailThreadList createFromParcel(Parcel source) {
            return new DoumailThreadList(source);
        }
        @Override
        public DoumailThreadList[] newArray(int size) {
            return new DoumailThreadList[size];
        }
    };

    public DoumailThreadList() {}

    protected DoumailThreadList(Parcel in) {
        super(in);

        doumailThreads = in.createTypedArrayList(DoumailThread.CREATOR);
        groupChatCount = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeTypedList(doumailThreads);
        dest.writeInt(groupChatCount);
    }
}
