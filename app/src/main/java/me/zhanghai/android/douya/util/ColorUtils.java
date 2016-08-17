/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util;

import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;

public class ColorUtils {

    private ColorUtils() {}

    @ColorInt
    public static int blendAlphaComponent(@ColorInt int color,
                                          @IntRange(from = 0x0, to = 0xFF) int alpha) {
        return android.support.v4.graphics.ColorUtils.setAlphaComponent(color,
                Color.alpha(color) * alpha / 0xFF);
    }
}
