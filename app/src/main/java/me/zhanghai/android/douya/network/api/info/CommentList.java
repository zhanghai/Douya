/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class CommentList implements Parcelable {

    public ArrayList<Comment> comments = new ArrayList<>();

    public int count;

    public int start;


    public static final Parcelable.Creator<CommentList> CREATOR =
            new Parcelable.Creator<CommentList>() {
                public CommentList createFromParcel(Parcel source) {
                    return new CommentList(source);
                }

                public CommentList[] newArray(int size) {
                    return new CommentList[size];
                }
            };

    public CommentList() {}

    protected CommentList(Parcel in) {
        this.comments = in.createTypedArrayList(Comment.CREATOR);
        this.count = in.readInt();
        this.start = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(comments);
        dest.writeInt(this.count);
        dest.writeInt(this.start);
    }
}
