/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class UserItemListItem implements Parcelable {

    @SerializedName("status")
    public CollectedItem.State state;

    @SerializedName("subjects")
    public ArrayList<Item> items = new ArrayList<>();

    public int total;

    public Item.Type type;


    public static final Parcelable.Creator<UserItemListItem> CREATOR =
            new Parcelable.Creator<UserItemListItem>() {
                @Override
                public UserItemListItem createFromParcel(Parcel source) {
                    return new UserItemListItem(source);
                }
                @Override
                public UserItemListItem[] newArray(int size) {
                    return new UserItemListItem[size];
                }
            };

    public UserItemListItem() {}

    protected UserItemListItem(Parcel in) {
        int tmpState = in.readInt();
        state = tmpState == -1 ? null : CollectedItem.State.values()[tmpState];
        items = in.createTypedArrayList(Item.CREATOR);
        total = in.readInt();
        int tmpType = in.readInt();
        type = tmpType == -1 ? null : Item.Type.values()[tmpType];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(state == null ? -1 : state.ordinal());
        dest.writeTypedList(items);
        dest.writeInt(total);
        dest.writeInt(type == null ? -1 : type.ordinal());
    }
}
