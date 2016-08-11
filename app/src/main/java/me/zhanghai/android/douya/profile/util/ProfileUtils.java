/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.profile.util;

import android.content.Context;

import me.zhanghai.android.douya.util.CardUtils;
import me.zhanghai.android.douya.util.ViewUtils;

public class ProfileUtils {

    private ProfileUtils() {}

    public static boolean shouldUseWideLayout(Context context) {
        return ViewUtils.hasSw600Dp(context) ? ViewUtils.isInLandscape(context)
                : !CardUtils.isFullWidth(context);
    }
}
