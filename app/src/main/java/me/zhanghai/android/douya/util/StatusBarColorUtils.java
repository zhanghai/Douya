/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.view.Window;

public class StatusBarColorUtils {

    private static final ArgbEvaluator sArgbEvaluator = new ArgbEvaluator();

    public static void animateTo(Window window, int color, int duration) {

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
            return;
        }

        ObjectAnimator animator = ObjectAnimator.ofInt(window, "statusBarColor",
                    window.getStatusBarColor(), color);
        animator.setEvaluator(sArgbEvaluator);
        animator.setDuration(duration);
        animator.setAutoCancel(true);
        animator.start();
    }

    public static void animateTo(int color, Activity activity) {
        animateTo(activity.getWindow(), color, ViewUtils.getShortAnimTime(activity));
    }

    public static void set(Window window, int color) {

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
            return;
        }

        window.setStatusBarColor(color);
    }

    public static void set(int color, Activity activity) {
        set(activity.getWindow(), color);
    }
}
