/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.apiv2;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Comment implements Parcelable {

    public SimpleUser author;

    public String content;

    @SerializedName("created")
    public String createTime;

    public ArrayList<TextEntity> entities = new ArrayList<>();

    public long id;

    public String source;

    public boolean isAuthorOneself() {
        return author.isOneself();
    }

    public CharSequence getContentWithEntities() {
        return TextEntity.applyEntities(content, entities);
    }

    public String getClipboardLabel() {
        return author.name;
    }

    public String getClipboardText() {
        return getContentWithEntities().toString();
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
        author = in.readParcelable(SimpleUser.class.getClassLoader());
        content = in.readString();
        createTime = in.readString();
        entities = in.createTypedArrayList(TextEntity.CREATOR);
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
        dest.writeString(createTime);
        dest.writeTypedList(entities);
        dest.writeLong(id);
        dest.writeString(source);
    }
}
