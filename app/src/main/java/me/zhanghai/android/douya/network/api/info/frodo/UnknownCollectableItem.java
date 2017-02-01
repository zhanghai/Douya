/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;

public class UnknownCollectableItem extends CollectableItem {

    public static final Creator<UnknownCollectableItem> CREATOR =
            new Creator<UnknownCollectableItem>() {
                @Override
                public UnknownCollectableItem createFromParcel(Parcel source) {
                    return new UnknownCollectableItem(source);
                }
                @Override
                public UnknownCollectableItem[] newArray(int size) {
                    return new UnknownCollectableItem[size];
                }
            };

    public UnknownCollectableItem() {}

    protected UnknownCollectableItem(Parcel in) {
        super(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }
}
