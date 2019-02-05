/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.TintTypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;

public class PreferenceCheckBox extends AppCompatCheckBox {

    private static final int[] STYLEABLE = {
            android.R.attr.key,
            android.R.attr.defaultValue,
            android.R.attr.persistent
    };
    private static final int STYLEABLE_ANDROID_KEY = 0;
    private static final int STYLEABLE_ANDROID_DEFAULT_VALUE = 1;
    private static final int STYLEABLE_ANDROID_PERSISTENT = 2;

    private String mKey;
    private boolean mHasDefaultValue;
    private boolean mDefaultValue;
    private boolean mPersistent;

    private SharedPreferences mSharedPreferences;

    private boolean mCheckedSet;

    public PreferenceCheckBox(Context context) {
        super(context);

        init(null, 0);
    }

    public PreferenceCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(attrs, 0);
    }

    public PreferenceCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(attrs, defStyleAttr);
    }

    @SuppressLint("RestrictedApi")
    private void init(AttributeSet attrs, int defStyleAttr) {

        Context context = getContext();
        TintTypedArray a = TintTypedArray.obtainStyledAttributes(context, attrs, STYLEABLE,
                defStyleAttr, 0);
        mKey = a.getString(STYLEABLE_ANDROID_KEY);
        mHasDefaultValue = a.hasValue(STYLEABLE_ANDROID_DEFAULT_VALUE);
        mDefaultValue = a.getBoolean(STYLEABLE_ANDROID_DEFAULT_VALUE, false);
        mPersistent = a.getBoolean(STYLEABLE_ANDROID_PERSISTENT, true);
        a.recycle();

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        dispatchSetInitialValue();
    }

    public boolean hasKey() {
        return !TextUtils.isEmpty(mKey);
    }

    public String getKey() {
        return mKey;
    }

    public void setKey(String key) {
        mKey = key;
    }

    public boolean hasDefaultValue() {
        return mHasDefaultValue;
    }

    public boolean getDefaultValue() {
        return mDefaultValue;
    }

    public void setDefaultValue(boolean defaultValue) {
        mDefaultValue = defaultValue;
        mHasDefaultValue = true;
    }

    public boolean isPersistent() {
        return mPersistent;
    }

    public void setPersistent(boolean persistent) {
        mPersistent = persistent;
    }

    public SharedPreferences getSharedPreferences() {
        return mSharedPreferences;
    }

    protected boolean shouldPersist() {
        return getSharedPreferences() != null && isPersistent() && hasKey();
    }

    private void dispatchSetInitialValue() {
        boolean shouldPersist = shouldPersist();
        if (!shouldPersist || !getSharedPreferences().contains(mKey)) {
            if (mHasDefaultValue) {
                onSetInitialValue(false, mDefaultValue);
            }
        } else {
            onSetInitialValue(true, false);
        }
    }

    protected void onSetInitialValue(boolean restorePersistedValue, boolean defaultValue) {
        setChecked(restorePersistedValue ? getPersistedBoolean(isChecked()) : defaultValue);
    }

    @Override
    public void setChecked(boolean checked) {
        // Always persist/notify the first time; don't assume the field's default of false.
        boolean changed = isChecked() != checked;
        if (changed || !mCheckedSet) {
            super.setChecked(checked);
            mCheckedSet = true;
            persistBoolean(checked);
        }
    }

    protected boolean getPersistedBoolean(boolean defaultReturnValue) {
        if (!shouldPersist()) {
            return defaultReturnValue;
        }
        return getSharedPreferences().getBoolean(mKey, defaultReturnValue);
    }

    protected boolean persistBoolean(boolean value) {
        if (!shouldPersist()) {
            return false;
        }
        if (value == getPersistedBoolean(!value)) {
            // It's already there, so the same as persisting
            return true;
        }
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean(mKey, value);
        tryCommit(editor);
        return true;
    }

    private void tryCommit(@NonNull SharedPreferences.Editor editor) {
        editor.apply();
    }
}
