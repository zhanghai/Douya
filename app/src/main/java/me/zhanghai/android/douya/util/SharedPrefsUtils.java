/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Set;

public class SharedPrefsUtils {

    private SharedPrefsUtils() {}

    public static SharedPreferences getSharedPrefs(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static String getString(Entry<String> entry, Context context) {
        return getSharedPrefs(context).getString(entry.getKey(context),
                entry.getDefaultValue(context));
    }

    public static Set<String> getStringSet(Entry<Set<String>> entry, Context context) {
        return getSharedPrefs(context).getStringSet(entry.getKey(context),
                entry.getDefaultValue(context));
    }

    public static int getInt(Entry<Integer> entry, Context context) {
        return getSharedPrefs(context).getInt(entry.getKey(context),
                entry.getDefaultValue(context));
    }

    public static long getLong(Entry<Long> entry, Context context) {
        return getSharedPrefs(context).getLong(entry.getKey(context),
                entry.getDefaultValue(context));
    }

    public static float getFloat(Entry<Float> entry, Context context) {
        return getSharedPrefs(context).getFloat(entry.getKey(context),
                entry.getDefaultValue(context));
    }

    public static boolean getBoolean(Entry<Boolean> entry, Context context) {
        return getSharedPrefs(context).getBoolean(entry.getKey(context),
                entry.getDefaultValue(context));
    }

    public static SharedPreferences.Editor getEditor(Context context) {
        return getSharedPrefs(context).edit();
    }

    public static void putString(Entry<String> entry, String value, Context context) {
        getEditor(context).putString(entry.getKey(context), value).apply();
    }

    public static void putStringSet(Entry<Set<String>> entry, Set<String> value, Context context) {
        getEditor(context).putStringSet(entry.getKey(context), value).apply();
    }

    public static void putInt(Entry<Integer> entry, int value, Context context) {
        getEditor(context).putInt(entry.getKey(context), value).apply();
    }

    public static void putLong(Entry<Long> entry, long value, Context context) {
        getEditor(context).putLong(entry.getKey(context), value).apply();
    }

    public static void putFloat(Entry<Float> entry, float value, Context context) {
        getEditor(context).putFloat(entry.getKey(context), value).apply();
    }

    public static void putBoolean(Entry<Boolean> entry, boolean value, Context context) {
        getEditor(context).putBoolean(entry.getKey(context), value).apply();
    }

    public static void remove(Entry<?> entry, Context context) {
        getEditor(context).remove(entry.getKey(context)).apply();
    }

    public static void clear(Context context) {
        getEditor(context).clear().apply();
    }

    public interface Entry<T> {
        String getKey(Context context);
        T getDefaultValue(Context context);
    }
}
