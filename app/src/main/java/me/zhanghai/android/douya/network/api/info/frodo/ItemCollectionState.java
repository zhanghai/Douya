/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.content.Context;
import android.text.TextUtils;

import me.zhanghai.android.douya.R;

public enum ItemCollectionState {

    // TODO: Support attend.
    TODO("mark", R.string.item_todo_format),
    DOING("doing", R.string.item_doing_format),
    DONE("done", R.string.item_done_format);

    private String mApiString;
    private int mFormatRes;

    ItemCollectionState(String apiString, int formatRes) {
        mApiString = apiString;
        mFormatRes = formatRes;
    }

    public static ItemCollectionState ofString(String apiString, ItemCollectionState defaultValue) {
        for (ItemCollectionState state : ItemCollectionState.values()) {
            if (TextUtils.equals(apiString, state.mApiString)) {
                return state;
            }
        }
        return defaultValue;
    }

    public static ItemCollectionState ofString(String apiString) {
        return ofString(apiString, null);
    }

    public String getApiString() {
        return mApiString;
    }

    public int getFormatRes() {
        return mFormatRes;
    }

    public String getFormat(Context context) {
        return context.getString(mFormatRes);
    }

    public String getString(String action, Context context) {
        return context.getString(mFormatRes, action);
    }

    public String getString(CollectableItem.Type type, Context context) {
        return getString(type.getAction(context), context);
    }
}
