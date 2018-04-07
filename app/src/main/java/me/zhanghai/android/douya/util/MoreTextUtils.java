/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util;

import android.text.TextUtils;

public class MoreTextUtils {

    private MoreTextUtils() {}

    public static boolean equalsAny(CharSequence text, CharSequence... array) {
        for (CharSequence element : array) {
            if (TextUtils.equals(text, element)) {
                return true;
            }
        }
        return false;
    }

    public static CharSequence emptyToNull(CharSequence text) {
        return text != null && text.length() == 0 ? null : text;
    }

    public static CharSequence nullToEmpty(CharSequence text) {
        return text == null ? "" : text;
    }

    public static String emptyToNull(String text) {
        return text != null && text.isEmpty() ? null : text;
    }

    public static String nullToEmpty(String text) {
        return text == null ? "" : text;
    }
}
