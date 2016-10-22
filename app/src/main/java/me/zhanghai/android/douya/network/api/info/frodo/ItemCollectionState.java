/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.content.Context;
import android.text.TextUtils;

import me.zhanghai.android.douya.R;

public enum ItemCollectionState {

    TODO("wish", R.string.item_todo_format),
    DOING("do", R.string.item_doing_format),
    DONE("collect", R.string.item_done_format);

    private String apiString;
    private int formatRes;

    ItemCollectionState(String apiString, int formatRes) {
        this.apiString = apiString;
        this.formatRes = formatRes;
    }

    public static ItemCollectionState ofString(String apiString, ItemCollectionState defaultValue) {
        for (ItemCollectionState state : ItemCollectionState.values()) {
            if (TextUtils.equals(state.apiString, apiString)) {
                return state;
            }
        }
        return defaultValue;
    }

    public static ItemCollectionState ofString(String apiString) {
        return ofString(apiString, DONE);
    }

    /**
     * @deprecated HACK-only.
     */
    public String getApiString() {
        return apiString;
    }

    public int getFormatRes() {
        return formatRes;
    }

    public String getFormat(Context context) {
        return context.getString(formatRes);
    }

    public String getString(String action, Context context) {
        return context.getString(formatRes, action);
    }

    public String getString(Item.Type type, Context context) {
        return getString(type.getAction(context), context);
    }
}
