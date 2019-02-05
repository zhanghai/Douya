/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util;

import android.app.Activity;
import com.google.android.material.snackbar.Snackbar;
import android.view.View;

public class SnackbarUtils {

    public static void show(View view, CharSequence text) {
        Snackbar.make(view, text, Snackbar.LENGTH_SHORT).show();
    }

    public static void show(View view, int textRes) {
        show(view, view.getResources().getText(textRes));
    }

    public static void show(CharSequence text, Activity activity) {
        show(activity.findViewById(android.R.id.content), text);
    }

    public static void show(int textRes, Activity activity) {
        show(activity.getText(textRes), activity);
    }
}
