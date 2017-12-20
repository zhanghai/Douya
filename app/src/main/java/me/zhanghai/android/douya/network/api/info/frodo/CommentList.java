/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;

import java.util.ArrayList;

public class CommentList extends BaseList<Comment> {

    public ArrayList<Comment> comments = new ArrayList<>();

    @Override
    public ArrayList<Comment> getList() {
        return comments;
    }


    public static final Creator<CommentList> CREATOR = new Creator<CommentList>() {
        @Override
        public CommentList createFromParcel(Parcel source) {
            return new CommentList(source);
        }
        @Override
        public CommentList[] newArray(int size) {
            return new CommentList[size];
        }
    };

    public CommentList() {}

    protected CommentList(Parcel in) {
        super(in);

        comments = in.createTypedArrayList(Comment.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeTypedList(comments);
    }
}
