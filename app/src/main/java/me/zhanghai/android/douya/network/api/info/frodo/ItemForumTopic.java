/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import com.google.gson.annotations.SerializedName;

/**
 * {@code SubjectForumTopic} in Frodo.
 */
public class ItemForumTopic extends SimpleItemForumTopic {

    public String content;

    // TODO
    //public Forum forum;

    // TODO: Can this be in SimpleItemForumTopic?
    @SerializedName("liked")
    public boolean isLiked;

    // TODO
    //public ArrayList<GroupTopicPhoto> photos = new ArrayList<>();

    // TODO: Really in API or just set in Frodo Android for convenience?
    public boolean read;

    @SerializedName("subject")
    public CollectableItem item;

    public String text;


    // TODO: Parcelable.
}
