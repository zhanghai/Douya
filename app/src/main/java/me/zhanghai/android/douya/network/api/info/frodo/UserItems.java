/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class UserItems implements Parcelable {

    /**
     * @deprecated Use {@link #getState()} instead.
     */
    @SerializedName("status")
    public String state;

    @SerializedName("subjects")
    public ArrayList<Item> items = new ArrayList<>();

    public int total;

    /**
     * @deprecated Use {@link #getType()} instead.
     */
    public String type;

    public CollectedItem.State getState() {
        // FIXME: Correct to use null?
        //noinspection deprecation
        return CollectedItem.State.ofString(state, null);
    }

    public Item.Type getType() {
        // FIXME: Correct to use null?
        //noinspection deprecation
        return Item.Type.ofString(type, null);
    }


    public static final Parcelable.Creator<UserItems> CREATOR =
            new Parcelable.Creator<UserItems>() {
                @Override
                public UserItems createFromParcel(Parcel source) {
                    return new UserItems(source);
                }
                @Override
                public UserItems[] newArray(int size) {
                    return new UserItems[size];
                }
            };

    public UserItems() {}

    protected UserItems(Parcel in) {
        //noinspection deprecation
        state = in.readString();
        items = in.createTypedArrayList(Item.CREATOR);
        total = in.readInt();
        //noinspection deprecation
        type = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //noinspection deprecation
        dest.writeString(state);
        dest.writeTypedList(items);
        dest.writeInt(total);
        //noinspection deprecation
        dest.writeString(type);
    }
}
