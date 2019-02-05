/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util;

import androidx.annotation.NonNull;

import java.util.Enumeration;
import java.util.Iterator;

public class EnumerationCompat {

    private EnumerationCompat() {}

    @NonNull
    public static <E> Iterator<E> asIterator(@NonNull Enumeration<E> enumeration) {
        return new Iterator<E>() {
            @Override
            public boolean hasNext() {
                return enumeration.hasMoreElements();
            }
            @Override
            public E next() {
                return enumeration.nextElement();
            }
        };
    }
}
