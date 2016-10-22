/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util;

import android.content.Context;

import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.network.api.info.apiv2.User;

public class DoubanUtils {

    private DoubanUtils() {}

    public static String getAtUserString(String userIdOrUid) {
        return '@' + userIdOrUid + ' ';
    }

    public static String getAtUserString(User user) {
        //noinspection deprecation
        return getAtUserString(user.uid);
    }

    public static String getRatingHint(int rating, Context context) {
        String[] ratingHints = context.getResources().getStringArray(R.array.item_rating_hints);
        if (rating == 0) {
            return "";
        } else if (rating > 0 && rating <= ratingHints.length) {
            return ratingHints[rating - 1];
        } else {
            return context.getString(R.string.item_rating_hint_unknown);
        }
    }
}
