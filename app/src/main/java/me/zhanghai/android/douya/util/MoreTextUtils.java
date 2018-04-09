/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util;

import android.text.TextUtils;

public class MoreTextUtils {

    private MoreTextUtils() {}

    /**
     * @deprecated Use {@link TextUtils#equals(CharSequence, CharSequence)} instead.
     */
    public static boolean equalsAny(CharSequence text, CharSequence text1) {
        return TextUtils.equals(text, text1);
    }

    public static boolean equalsAny(CharSequence text, CharSequence text1, CharSequence text2) {
        return TextUtils.equals(text, text1) || TextUtils.equals(text, text2);
    }

    public static boolean equalsAny(CharSequence text, CharSequence text1, CharSequence text2,
                                    CharSequence text3) {
        return TextUtils.equals(text, text1) || TextUtils.equals(text, text2)
                || TextUtils.equals(text, text3);
    }

    public static boolean equalsAny(CharSequence text, CharSequence... array) {
        for (CharSequence element : array) {
            if (TextUtils.equals(text, element)) {
                return true;
            }
        }
        return false;
    }

    public static boolean contains(String text, CharSequence text1) {
        return text != null && text.contains(text1);
    }

    /**
     * @deprecated Use {@link #contains(String, CharSequence)} instead.
     */
    public static boolean containsAny(String text, CharSequence text1) {
        return contains(text, text1);
    }

    public static boolean containsAny(String text, CharSequence text1, CharSequence text2) {
        return contains(text, text1) || contains(text, text2);
    }

    public static boolean containsAny(String text, CharSequence text1, CharSequence text2,
                                      CharSequence text3) {
        return contains(text, text1) || contains(text, text2) || contains(text, text3);
    }

    public static boolean containsAny(String text, CharSequence... array) {
        for (CharSequence element : array) {
            if (contains(text, element)) {
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
