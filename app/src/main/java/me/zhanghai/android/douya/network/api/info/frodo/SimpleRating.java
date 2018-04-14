/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import me.zhanghai.android.douya.R;

public class SimpleRating implements Parcelable {

    public int count;

    public int max;

    public float value;

    public boolean hasRating() {
        return count > 0;
    }

    public String getRatingString(Context context) {
        if (!hasRating()) {
            throw new IllegalStateException("getRatingString() called when no rating is available");
        }
        float rating = (float) Math.round(value / max * 10 * 10) / 10;
        return context.getString(rating == 10 ? R.string.item_rating_format_ten
                : R.string.item_rating_format, rating);
    }

    public float getRatingBarRating() {
        return (float) Math.round(value / max * 10) / 2;
    }

    public String getRatingCountString(Context context) {
        return context.getString(R.string.item_rating_count_format, count);
    }


    public static final Parcelable.Creator<SimpleRating> CREATOR =
            new Parcelable.Creator<SimpleRating>() {
                @Override
                public SimpleRating createFromParcel(Parcel source) {
                    return new SimpleRating(source);
                }
                @Override
                public SimpleRating[] newArray(int size) {
                    return new SimpleRating[size];
                }
            };

    public SimpleRating() {}

    protected SimpleRating(Parcel in) {
        count = in.readInt();
        max = in.readInt();
        value = in.readFloat();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(count);
        dest.writeInt(max);
        dest.writeFloat(value);
    }
}
