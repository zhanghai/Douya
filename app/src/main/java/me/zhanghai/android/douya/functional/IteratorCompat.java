/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.functional;

import java.util.Iterator;

import me.zhanghai.android.douya.functional.compat.Consumer;

public class IteratorCompat {

    private IteratorCompat() {}

    public static <T> void forEachRemaining(Iterator<T> iterator, Consumer<T> consumer) {
        while (iterator.hasNext()) {
            T t = iterator.next();
            consumer.accept(t);
        }
    }
}
