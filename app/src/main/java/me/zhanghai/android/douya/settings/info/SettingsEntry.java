/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.settings.info;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import me.zhanghai.android.douya.DouyaApplication;
import me.zhanghai.android.douya.util.SharedPrefsUtils;

public abstract class SettingsEntry<T> implements SharedPrefsUtils.Entry<T> {

    private final int mKeyResId;
    private final int mDefaultValueResId;

    public SettingsEntry(@StringRes int keyResId, int defaultValueResId) {
        mKeyResId = keyResId;
        mDefaultValueResId = defaultValueResId;
    }

    @NonNull
    @Override
    public String getKey() {
        return DouyaApplication.getInstance().getString(mKeyResId);
    }

    protected int getDefaultValueResId() {
        return mDefaultValueResId;
    }

    @Nullable
    public abstract T getValue();

    public abstract void putValue(@Nullable T value);

    public void remove() {
        SharedPrefsUtils.remove(this);
    }
}
