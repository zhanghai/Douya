/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util;

import android.app.Activity;
import android.app.Application;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.app.NightModeAccessor;

import java.util.HashMap;
import java.util.Map;

import me.zhanghai.android.douya.settings.info.Settings;

public class NightModeHelper {

    private static ActivityHelper sActivityHelper = new ActivityHelper();

    private NightModeHelper() {}

    public static void setup(Application application) {
        syncDefaultNightMode();
        application.registerActivityLifecycleCallbacks(sActivityHelper);
    }

    private static void syncDefaultNightMode() {
        setDefaultNightMode(Settings.NIGHT_MODE.getEnumValue());
    }

    private static int getDefaultNightMode() {
        return AppCompatDelegate.getDefaultNightMode();
    }

    private static void setDefaultNightMode(Settings.NightMode nightMode) {
        int nightModeValue = nightMode.getValue();
        if (AppCompatDelegate.getDefaultNightMode() == nightModeValue) {
            return;
        }
        AppCompatDelegate.setDefaultNightMode(nightModeValue);
    }

    public static void updateNightMode(AppCompatActivity activity) {
        syncDefaultNightMode();
        sActivityHelper.onActivityStarted(activity);
    }

    // Should be called as:
    // super.onConfigurationChanged(NightModeHelper.onConfigurationChanged(newConfig, this));
    // See also AppCompatDelegateImplV14#updateForNightMode(int) .
    @CheckResult
    public static Configuration onConfigurationChanged(Configuration newConfig,
                                                       AppCompatActivity activity) {
        boolean isInNightMode = sActivityHelper.isActivityInNightMode(activity);
        Configuration newConfigWithNightMode = new Configuration(newConfig);
        int uiModeNight = isInNightMode ? Configuration.UI_MODE_NIGHT_YES
                : Configuration.UI_MODE_NIGHT_NO;
        newConfigWithNightMode.uiMode = uiModeNight | (newConfigWithNightMode.uiMode
                & ~Configuration.UI_MODE_NIGHT_MASK);
        Resources resources = activity.getResources();
        //noinspection deprecation
        resources.updateConfiguration(newConfigWithNightMode, resources.getDisplayMetrics());
        NightModeAccessor.flushResources(resources);
        return newConfigWithNightMode;
    }

    // AppCompatDelegateImplV14.updateForNightMode() won't update when multiple Activities share a
    // Resources and a Configuration instance. We do this ourselves.
    private static class ActivityHelper implements Application.ActivityLifecycleCallbacks {

        private Map<Activity, Boolean> mActivityNightModeMap = new HashMap<>();

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            // This runs after AppCompatActivity calls AppCompatDelegate.applyDayNight().
            int uiModeNight = activity.getResources().getConfiguration().uiMode
                    & Configuration.UI_MODE_NIGHT_MASK;
            boolean isInNightMode = uiModeNight == Configuration.UI_MODE_NIGHT_YES;
            // Night mode cannot change once an Activity is created.
            mActivityNightModeMap.put(activity, isInNightMode);
        }

        @Override
        public void onActivityStarted(Activity activity) {
            if (!(activity instanceof AppCompatActivity)) {
                return;
            }
            AppCompatActivity appCompatActivity = (AppCompatActivity) activity;
            // This runs before AppCompatDelegateImplV14.onStart() calls
            // AppCompatDelegate.applyDayNight().
            // And we don't care about things below V14 where this is a no-op returning false.
            if (appCompatActivity.getDelegate().applyDayNight()) {
                return;
            }
            boolean isInNightMode = mActivityNightModeMap.get(activity);
            int nightMode = NightModeAccessor.mapNightMode(appCompatActivity.getDelegate(),
                    // We don't use local night mode.
                    getDefaultNightMode());
            if (nightMode == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) {
                // Let our future system handle it.
                return;
            }
            boolean shouldBeInNightMode = nightMode == AppCompatDelegate.MODE_NIGHT_YES;
            if (isInNightMode != shouldBeInNightMode) {
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
            mActivityNightModeMap.remove(activity);
        }

        public boolean isActivityInNightMode(Activity activity) {
            return mActivityNightModeMap.get(activity);
        }
    }
}
