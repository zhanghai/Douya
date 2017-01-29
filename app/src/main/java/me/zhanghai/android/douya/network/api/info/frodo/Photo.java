/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;

public class Photo extends BaseItem {

    @SerializedName("allow_comment")
    public boolean isCommentAllowed;

    public SimpleUser author;

    @SerializedName("create_time")
    public String creationTime;

    @SerializedName("comments_count")
    public int commentCount;

    public String description;

    public ImageWithSize image;

    @SerializedName("liked")
    public boolean isLiked;

    @SerializedName("likers_count")
    public int likerCount;

    @SerializedName("owner_uri")
    public String ownerUri;

    public int position;


    public static final Creator<Photo> CREATOR = new Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel source) {
            return new Photo(source);
        }
        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };

    public Photo() {}

    protected Photo(Parcel in) {
        super(in);

        isCommentAllowed = in.readByte() != 0;
        author = in.readParcelable(SimpleUser.class.getClassLoader());
        creationTime = in.readString();
        commentCount = in.readInt();
        description = in.readString();
        image = in.readParcelable(ImageWithSize.class.getClassLoader());
        isLiked = in.readByte() != 0;
        likerCount = in.readInt();
        ownerUri = in.readString();
        position = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeByte(isCommentAllowed ? (byte) 1 : (byte) 0);
        dest.writeParcelable(author, flags);
        dest.writeString(creationTime);
        dest.writeInt(commentCount);
        dest.writeString(description);
        dest.writeParcelable(image, flags);
        dest.writeByte(isLiked ? (byte) 1 : (byte) 0);
        dest.writeInt(likerCount);
        dest.writeString(ownerUri);
        dest.writeInt(position);
    }
}
