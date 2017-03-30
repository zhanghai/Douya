/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.os.Build;

public class AlarmManagerCompat {

    private AlarmManagerCompat() {}

    public static void setExact(AlarmManager alarmManager, int type, long triggerAtMillis,
                                PendingIntent operation) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(type, triggerAtMillis, operation);
        } else {
            alarmManager.set(type, triggerAtMillis, operation);
        }
    }
}
