/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.scalpel;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.scalpel.ScalpelFrameLayout;

public class ScalpelUtils {

    private ScalpelUtils() {}

    public static void inject(Activity activity) {
        ViewGroup contentLayout = findContentLayout(activity);
        ScalpelFrameLayout scalpelLayout = new ScalpelFrameLayout(activity);
        while (contentLayout.getChildCount() > 0) {
            View view = contentLayout.getChildAt(0);
            contentLayout.removeViewAt(0);
            scalpelLayout.addView(view);
        }
        contentLayout.addView(scalpelLayout);
    }

    public static void setEnabled(Activity activity, boolean enabled) {
        findScalpelLayout(activity).setLayerInteractionEnabled(enabled);
    }

    private static ViewGroup findContentLayout(Activity activity) {
        return (ViewGroup) activity.findViewById(android.R.id.content);
    }

    private static ScalpelFrameLayout findScalpelLayout(Activity activity) {
        return (ScalpelFrameLayout) findContentLayout(activity).getChildAt(0);
    }
}
