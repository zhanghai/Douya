/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.settings.info;

import android.content.Context;

import me.zhanghai.android.douya.util.SharedPrefsUtils;

public abstract class SettingsEntry<T> implements SharedPrefsUtils.Entry<T> {

    private int mKeyResId;
    private int mDefaultValueResId;

    public SettingsEntry(int keyResId, int defaultValueResId) {
        mKeyResId = keyResId;
        mDefaultValueResId = defaultValueResId;
    }

    @Override
    public String getKey(Context context) {
        return context.getString(mKeyResId);
    }

    protected int getDefaultValueResId() {
        return mDefaultValueResId;
    }

    public abstract T getValue(Context context);

    public abstract void putValue(T value, Context context);

    public void remove(Context context) {
        SharedPrefsUtils.remove(this, context);
    }
}
