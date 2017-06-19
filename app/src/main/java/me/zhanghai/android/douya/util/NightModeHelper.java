/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;

import java.util.HashSet;
import java.util.Set;

import me.zhanghai.android.douya.settings.info.Settings;

public class NightModeHelper {

    private static ActivityHelper sActivityHelper = new ActivityHelper();

    private NightModeHelper() {}

    public static void setup(Application application) {
        application.registerActivityLifecycleCallbacks(sActivityHelper);
    }

    public static void syncDefaultNightMode() {
        setDefaultNightMode(Settings.NIGHT_MODE.getEnumValue());
    }

    private static void setDefaultNightMode(Settings.NightMode nightMode) {
        int nightModeValue = nightMode.getValue();
        if (AppCompatDelegate.getDefaultNightMode() == nightModeValue) {
            return;
        }
        AppCompatDelegate.setDefaultNightMode(nightModeValue);
        sActivityHelper.markActivitiesAsStale();
    }

    public static void updateNightMode(AppCompatActivity activity) {
        activity.getDelegate().applyDayNight();
        sActivityHelper.markActivityAsFresh(activity);
    }

    private static class ActivityHelper implements Application.ActivityLifecycleCallbacks {

        private Set<Activity> mCreatedActivities = new HashSet<>();
        private Set<Activity> mStaleActivities = new HashSet<>();

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            mCreatedActivities.add(activity);
        }

        @Override
        public void onActivityStarted(Activity activity) {
            if (mStaleActivities.remove(activity)) {
                activity.recreate();
            }
        }

        @Override
        public void onActivityResumed(Activity activity) {}

        @Override
        public void onActivityPaused(Activity activity) {}

        @Override
        public void onActivityStopped(Activity activity) {}

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}

        @Override
        public void onActivityDestroyed(Activity activity) {
            mCreatedActivities.remove(activity);
        }

        public void markActivitiesAsStale() {
            mStaleActivities.addAll(mCreatedActivities);
        }

        public void markActivityAsFresh(Activity activity) {
            mStaleActivities.remove(activity);
        }
    }
}
