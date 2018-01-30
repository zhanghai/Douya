/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util;

import android.support.v7.widget.TooltipCompat;
import android.view.View;

public class TooltipUtils {

    private TooltipUtils() {}

    public static void setup(View view) {
        TooltipCompat.setTooltipText(view, view.getContentDescription());
    }
}
