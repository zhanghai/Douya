/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.fabric;

import android.content.Context;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import me.zhanghai.android.douya.BuildConfig;

public class FabricUtils {

    private FabricUtils() {}

    public static void init(Context context) {
        if (BuildConfig.DEBUG) {
            return;
        }
        Fabric.with(context, new Crashlytics());
    }
}
