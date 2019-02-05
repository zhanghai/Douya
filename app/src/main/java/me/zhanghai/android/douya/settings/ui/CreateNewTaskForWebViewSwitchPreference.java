/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.settings.ui;

import android.content.Context;
import android.os.Build;
import androidx.preference.SwitchPreferenceCompat;
import android.util.AttributeSet;

import me.zhanghai.android.douya.R;

public class CreateNewTaskForWebViewSwitchPreference extends SwitchPreferenceCompat {

    public CreateNewTaskForWebViewSwitchPreference(Context context) {
        super(context);

        init();
    }

    public CreateNewTaskForWebViewSwitchPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public CreateNewTaskForWebViewSwitchPreference(Context context, AttributeSet attrs,
                                                   int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    public CreateNewTaskForWebViewSwitchPreference(Context context, AttributeSet attrs,
                                                   int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init();
    }

    private void init() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            setEnabled(false);
            setSummary(R.string.settings_create_new_task_for_webview_summary_below_lollipop);
        }
    }
}
