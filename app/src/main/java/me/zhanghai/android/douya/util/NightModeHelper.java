/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;

import me.zhanghai.android.douya.settings.info.Settings;

public class NightModeHelper {

    private NightModeHelper() {}

    public static void setDefaultNightMode(Settings.NightMode nightMode) {
        AppCompatDelegate.setDefaultNightMode(nightMode.getValue());
    }

    public static void syncDefaultNightMode() {
        setDefaultNightMode(Settings.NIGHT_MODE.getEnumValue());
    }

    public static void updateNightMode(AppCompatActivity activity) {
        activity.getDelegate().applyDayNight();
    }
}
