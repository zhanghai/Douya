/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class AwardCategory extends SimpleAwardCategory {

    @SerializedName("results")
    public ArrayList<AwardWinner> winners = new ArrayList<>();


    public static final Creator<AwardCategory> CREATOR = new Creator<AwardCategory>() {
        @Override
        public AwardCategory createFromParcel(Parcel source) {
            return new AwardCategory(source);
        }
        @Override
        public AwardCategory[] newArray(int size) {
            return new AwardCategory[size];
        }
    };

    public AwardCategory() {}

    protected AwardCategory(Parcel in) {
        super(in);

        winners = in.createTypedArrayList(AwardWinner.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeTypedList(winners);
    }
}
