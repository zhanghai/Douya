/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;

public class EditTextPreference extends com.takisoft.fix.support.v7.preference.EditTextPreference {

    public EditTextPreference(@NonNull Context context) {
        super(context);
    }

    public EditTextPreference(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public EditTextPreference(@NonNull Context context, @Nullable AttributeSet attrs,
                              int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public EditTextPreference(@NonNull Context context, @Nullable AttributeSet attrs,
                              int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Nullable
    @Override
    public CharSequence getSummary() {
        String text = getText();
        if (TextUtils.isEmpty(text)) {
            CharSequence hint = getEditText().getHint();
            if (!TextUtils.isEmpty(hint)) {
                return hint;
            }
        }
        CharSequence summary = super.getSummary();
        if (!TextUtils.isEmpty(summary)) {
            return String.format(summary.toString(), text);
        } else {
            return summary;
        }
    }
}
