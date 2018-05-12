/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util;

import android.os.Build;

public class LongCompat {

    private LongCompat() {}

    public static int hashCode(long value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Long.hashCode(value);
        } else {
            return Long.valueOf(value).hashCode();
        }
    }
}
