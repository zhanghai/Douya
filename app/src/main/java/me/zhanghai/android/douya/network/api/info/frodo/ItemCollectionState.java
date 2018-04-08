/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.content.Context;
import android.text.TextUtils;

import me.zhanghai.android.douya.R;

public enum ItemCollectionState {

    // TODO: Support "attend".
    // NOTE: The second api string is for LegacySubject support.
    TODO(new String[] { "mark", "wish" }, R.string.item_todo_format),
    DOING(new String[] { "doing", "do" }, R.string.item_doing_format),
    DONE(new String[] { "done", "collect"}, R.string.item_done_format);

    private String[] mApiStrings;
    private int mFormatRes;

    ItemCollectionState(String[] apiStrings, int formatRes) {
        mApiStrings = apiStrings;
        mFormatRes = formatRes;
    }

    public static ItemCollectionState ofString(String apiString, ItemCollectionState defaultValue) {
        for (ItemCollectionState state : ItemCollectionState.values()) {
            for (String stateApiString : state.mApiStrings) {
                if (TextUtils.equals(apiString, stateApiString)) {
                    return state;
                }
            }
        }
        return defaultValue;
    }

    public static ItemCollectionState ofString(String apiString) {
        return ofString(apiString, null);
    }

    public String getApiString() {
        return mApiStrings[0];
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
