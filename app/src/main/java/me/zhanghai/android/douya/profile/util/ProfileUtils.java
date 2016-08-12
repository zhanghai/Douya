/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.profile.util;

import android.content.Context;
import android.content.res.Resources;

import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.util.CardUtils;
import me.zhanghai.android.douya.util.ViewUtils;

public class ProfileUtils {

    private ProfileUtils() {}

    public static boolean shouldUseWideLayout(Context context) {
        return ViewUtils.hasSw600Dp(context) ? ViewUtils.isInLandscape(context)
                : !CardUtils.isFullWidth(context);
    }

    public static int getAppBarWidth(int width, Context context) {
        if (shouldUseWideLayout(context)) {
            if (CardUtils.getColumnCount(context) == 2) {
                return width * 2 / 5;
            } else {
                Resources resources = context.getResources();
                int cardListHorizontalPadding = resources
                        .getDimensionPixelOffset(R.dimen.card_list_horizontal_padding);
                int cardHorizontalMargin =
                        resources.getDimensionPixelOffset(R.dimen.card_horizontal_margin);
                int cardShadowHorizontalMargin =
                        resources.getDimensionPixelOffset(R.dimen.card_shadow_horizontal_margin);
                return (width - 2 * cardListHorizontalPadding) / 3 + cardListHorizontalPadding
                        + cardHorizontalMargin - cardShadowHorizontalMargin;
            }
        } else {
            return width;
        }
    }
}
