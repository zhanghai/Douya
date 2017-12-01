/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ItemForumTopicList extends BaseList<SimpleItemForumTopic> {

    public ItemEpisode episode;

    @SerializedName("forum_topics")
    public ArrayList<SimpleItemForumTopic> forumTopics = new ArrayList<>();

    @Override
    public ArrayList<SimpleItemForumTopic> getList() {
        return forumTopics;
    }


    public static final Creator<ItemForumTopicList> CREATOR = new Creator<ItemForumTopicList>() {
        @Override
        public ItemForumTopicList createFromParcel(Parcel source) {
            return new ItemForumTopicList(source);
        }
        @Override
        public ItemForumTopicList[] newArray(int size) {
            return new ItemForumTopicList[size];
        }
    };

    public ItemForumTopicList() {}

    protected ItemForumTopicList(Parcel in) {
        super(in);

        episode = in.readParcelable(ItemEpisode.class.getClassLoader());
        forumTopics = in.createTypedArrayList(SimpleItemForumTopic.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeParcelable(episode, flags);
        dest.writeTypedList(forumTopics);
    }
}
