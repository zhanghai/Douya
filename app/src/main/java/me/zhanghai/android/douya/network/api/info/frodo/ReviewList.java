/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;

import java.util.ArrayList;

public class ReviewList extends BaseList {

    public ArrayList<Review> reviews = new ArrayList<>();


    public static final Creator<ReviewList> CREATOR = new Creator<ReviewList>() {
        @Override
        public ReviewList createFromParcel(Parcel source) {
            return new ReviewList(source);
        }
        @Override
        public ReviewList[] newArray(int size) {
            return new ReviewList[size];
        }
    };

    public ReviewList() {}

    protected ReviewList(Parcel in) {
        super(in);

        reviews = in.createTypedArrayList(Review.CREATOR);
    }

    @Override
    public int describeContents() {
        return super.describeContents();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeTypedList(reviews);
    }
}
