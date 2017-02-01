/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

// Not parcelable because list is not parcelable.
public class UserItemList {

    @SerializedName("itemlist")
    public ArrayList<UserItems> list = new ArrayList<>();
}
