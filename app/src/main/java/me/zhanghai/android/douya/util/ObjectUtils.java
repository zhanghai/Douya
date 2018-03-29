/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util;

public class ObjectUtils {

    private ObjectUtils() {};

    public static String toString(Object object) {
        return object != null ? object.toString() : null;
    }

    public static int hashCode(Object object) {
        return object != null ? object.hashCode() : 0;
    }
}
