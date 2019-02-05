/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.content.Context;
import androidx.preference.DialogPreference;
import android.util.AttributeSet;

import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompat;

public class LicensesDialogPreference extends DialogPreference {

    static {
        PreferenceFragmentCompat.registerPreferenceFragment(LicensesDialogPreference.class,
                LicensesDialogFragment.class);
    }

    public LicensesDialogPreference(Context context) {
        super(context);
    }

    public LicensesDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LicensesDialogPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public LicensesDialogPreference(Context context, AttributeSet attrs, int defStyleAttr,
                                    int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
