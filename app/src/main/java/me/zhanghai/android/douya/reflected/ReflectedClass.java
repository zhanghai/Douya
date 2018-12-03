/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.reflected;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class ReflectedClass {

    @NonNull
    private final String mClassName;

    @Nullable
    private Class<?> mClass;
    @NonNull
    private final Object mClassLock = new Object();

    public ReflectedClass(@NonNull String className) {
        mClassName = className;
    }

    @NonNull
    public Class get() {
        synchronized (mClassLock) {
            if (mClass == null) {
                mClass = ReflectedAccessor.getClass(mClassName);
            }
            return mClass;
        }
    }
}
