/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

// Not parcelable because items may not be of the same type.
public class UserItems {

    /**
     * @deprecated Use {@link #getState()} instead.
     */
    @SerializedName("status")
    public String state;

    @SerializedName("subjects")
    public ArrayList<CollectableItem> items = new ArrayList<>();

    public int total;

    /**
     * @deprecated Use {@link #getType()} instead.
     */
    public String type;

    public ItemCollectionState getState() {
        // FIXME: Correct to use null?
        //noinspection deprecation
        return ItemCollectionState.ofString(state, null);
    }

    public CollectableItem.Type getType() {
        // FIXME: Correct to use null?
        //noinspection deprecation
        return CollectableItem.Type.ofApiString(type, null);
    }
}
