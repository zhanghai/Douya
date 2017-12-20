/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * {@code RefAtComment} in Frodo.
 */
public class Comment implements Parcelable {

    public SimpleUser author;

    @SerializedName("create_time")
    public String createdAt;

    public ArrayList<TextEntity> entities;

    @SerializedName("has_ref")
    public boolean hasReference;

    public String id;

    @SerializedName("is_voted")
    public boolean isVoted;

    public ArrayList<SizedPhoto> photos = new ArrayList<>();

    @SerializedName("ref_comment")
    public Comment referencedComment;

    public String text;

    public String uri;

    @SerializedName("vote_count")
    public int voteCount;


    public static final Parcelable.Creator<Comment> CREATOR = new Parcelable.Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel source) {
            return new Comment(source);
        }
        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };

    public Comment() {}

    protected Comment(Parcel in) {
        author = in.readParcelable(SimpleUser.class.getClassLoader());
        createdAt = in.readString();
        entities = in.createTypedArrayList(TextEntity.CREATOR);
        hasReference = in.readByte() != 0;
        id = in.readString();
        isVoted = in.readByte() != 0;
        photos = in.createTypedArrayList(SizedPhoto.CREATOR);
        referencedComment = in.readParcelable(Comment.class.getClassLoader());
        text = in.readString();
        uri = in.readString();
        voteCount = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(author, flags);
        dest.writeString(createdAt);
        dest.writeTypedList(entities);
        dest.writeByte(hasReference ? (byte) 1 : (byte) 0);
        dest.writeString(id);
        dest.writeByte(isVoted ? (byte) 1 : (byte) 0);
        dest.writeTypedList(photos);
        dest.writeParcelable(referencedComment, flags);
        dest.writeString(text);
        dest.writeString(uri);
        dest.writeInt(voteCount);
    }
}
