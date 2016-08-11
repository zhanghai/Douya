/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util;

import android.content.Context;

public class CardUtils {

    private CardUtils() {}

    public static boolean isFullWidth(Context context) {
        return !ViewUtils.hasW600Dp(context);
    }

    public static int getColumnCount(Context context) {
        return ViewUtils.hasSw600Dp(context) ?
                (ViewUtils.hasW960Dp(context) ? 3 : 2)
                : ViewUtils.hasW600Dp(context) ? 2
                : 1;
    }
}
