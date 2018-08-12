/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Doulist extends BaseItem {

    @SerializedName("create_time")
    public String createTime;

    @SerializedName("desc")
    public String description;

    @SerializedName("followers_count")
    public int followerCount;

    @SerializedName("is_follow")
    public boolean isFollowing;

    @SerializedName("item_abstracts")
    public ArrayList<String> itemAbstracts = new ArrayList<>();

    @SerializedName("items_count")
    public int itemCount;

    @SerializedName("merged_cover_url")
    public String mergedCoverUrl;

    @SerializedName("owner")
    public User author;

    public ArrayList<String> tags = new ArrayList<>();

    @SerializedName("update_time")
    public String updateTime;


    public static final Creator<Doulist> CREATOR = new Creator<Doulist>() {
        @Override
        public Doulist createFromParcel(Parcel source) {
            return new Doulist(source);
        }
        @Override
        public Doulist[] newArray(int size) {
            return new Doulist[size];
        }
    };

    public Doulist() {}

    protected Doulist(Parcel in) {
        super(in);

        createTime = in.readString();
        description = in.readString();
        followerCount = in.readInt();
        isFollowing = in.readByte() != 0;
        itemAbstracts = in.createStringArrayList();
        itemCount = in.readInt();
        mergedCoverUrl = in.readString();
        author = in.readParcelable(User.class.getClassLoader());
        tags = in.createStringArrayList();
        updateTime = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeString(createTime);
        dest.writeString(description);
        dest.writeInt(followerCount);
        dest.writeByte(isFollowing ? (byte) 1 : (byte) 0);
        dest.writeStringList(itemAbstracts);
        dest.writeInt(itemCount);
        dest.writeString(mergedCoverUrl);
        dest.writeParcelable(author, flags);
        dest.writeStringList(tags);
        dest.writeString(updateTime);
    }
}
