/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util;

import android.text.TextUtils;

import java.util.Locale;

public class StringUtils {

    private StringUtils() {}

    public static boolean equalsIgnoreCase(String s1, String s2) {
        //noinspection StringEquality
        if (s1 == s2) {
            return true;
        } else if (s1 != null) {
            return s1.equalsIgnoreCase(s2);
        } else {
            return false;
        }
    }

    public static String formatUs(String format, Object... args) {
        return String.format(Locale.US, format, args);
    }

    public static String joinNonEmpty(CharSequence delimiter, CharSequence... elements) {
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (CharSequence element : elements) {
            if (TextUtils.isEmpty(element)) {
                continue;
            }
            if (first) {
                first = false;
            } else {
                builder.append(delimiter);
            }
            builder.append(element);
        }
        return builder.toString();
    }

    public static String joinNonEmpty(CharSequence delimiter,
                                      Iterable<? extends CharSequence> elements) {
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (CharSequence element : elements) {
            if (TextUtils.isEmpty(element)) {
                continue;
            }
            if (first) {
                first = false;
            } else {
                builder.append(delimiter);
            }
            builder.append(element);
        }
        return builder.toString();
    }
}
