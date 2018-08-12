/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import me.zhanghai.android.douya.network.api.info.ClipboardCopyable;

/**
 * {@code RefAtComment} in Frodo.
 */
public class Comment implements ClipboardCopyable, Parcelable {

    public SimpleUser author;

    @SerializedName("create_time")
    public String createTime;

    public ArrayList<TextEntity> entities;

    @SerializedName("has_ref")
    public boolean hasReference;

    public long id;

    @SerializedName("is_voted")
    public boolean isVoted;

    public ArrayList<SizedPhoto> photos = new ArrayList<>();

    @SerializedName("ref_comment")
    public Comment referencedComment;

    public String text;

    public String uri;

    @SerializedName("vote_count")
    public int voteCount;

    public boolean isAuthorOneself() {
        return author.isOneself();
    }

    public CharSequence getTextWithEntities() {
        return TextEntity.applyEntities(text, entities);
    }

    @Override
    public String getClipboardLabel(Context context) {
        return author.name;
    }

    @Override
    public String getClipboardText(Context context) {
        return getTextWithEntities().toString();
    }

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
        createTime = in.readString();
        entities = in.createTypedArrayList(TextEntity.CREATOR);
        hasReference = in.readByte() != 0;
        id = in.readLong();
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
        dest.writeString(createTime);
        dest.writeTypedList(entities);
        dest.writeByte(hasReference ? (byte) 1 : (byte) 0);
        dest.writeLong(id);
        dest.writeByte(isVoted ? (byte) 1 : (byte) 0);
        dest.writeTypedList(photos);
        dest.writeParcelable(referencedComment, flags);
        dest.writeString(text);
        dest.writeString(uri);
        dest.writeInt(voteCount);
    }
}
