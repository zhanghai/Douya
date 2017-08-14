/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;

import me.zhanghai.android.douya.R;

public class DropDownPreference extends android.support.v7.preference.DropDownPreference {

    public DropDownPreference(Context context) {
        super(context);
    }

    public DropDownPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DropDownPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public DropDownPreference(Context context, AttributeSet attrs, int defStyleAttr,
                              int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected ArrayAdapter<String> createAdapter() {
        return new ArrayAdapter<>(getContext(), R.layout.dropdown_preference_item);
    }
}
