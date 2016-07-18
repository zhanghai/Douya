/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.apiv2;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import me.zhanghai.android.douya.util.TimeUtils;

public class Comment implements Parcelable {

    public User author;

    public String content;

    @SerializedName("created")
    public String createdAt;

    public ArrayList<Entity> entities = new ArrayList<>();

    public long id;

    public String source;

    public boolean isAuthorOneself(Context context) {
        return author.isOneself(context);
    }

    public CharSequence getContentWithEntities(Context context) {
        return Entity.applyEntities(content, entities, context);
    }

    public String getClipboardLabel() {
        return author.name;
    }

    public String getClipboardText(Context context) {
        return author.name + ' ' + TimeUtils.formatDoubanDateTime(createdAt, context) + '\n'
                + getContentWithEntities(context);
    }


    public static final Creator<Comment> CREATOR = new Creator<Comment>() {
        public Comment createFromParcel(Parcel source) {
            return new Comment(source);
        }
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };

    public Comment() {}

    protected Comment(Parcel in) {
        author = in.readParcelable(User.class.getClassLoader());
        content = in.readString();
        createdAt = in.readString();
        entities = in.createTypedArrayList(Entity.CREATOR);
        id = in.readLong();
        source = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(author, 0);
        dest.writeString(content);
        dest.writeString(createdAt);
        dest.writeTypedList(entities);
        dest.writeLong(id);
        dest.writeString(source);
    }
}
