/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.settings.info;

import android.content.Context;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import me.zhanghai.android.douya.util.LogUtils;
import me.zhanghai.android.douya.util.SharedPrefsUtils;

public class SettingsEntries {

    public static class StringSettingsEntry extends SettingsEntry<String> {

        public StringSettingsEntry(int keyResId, int defaultValueResId) {
            super(keyResId, defaultValueResId);
        }

        @Override
        public String getDefaultValue(Context context) {
            return context.getString(getDefaultValueResId());
        }

        @Override
        public String getValue(Context context) {
            return SharedPrefsUtils.getString(this, context);
        }

        @Override
        public void putValue(String value, Context context) {
            SharedPrefsUtils.putString(this, value, context);
        }
    }

    public static class StringSetSettingsEntry extends SettingsEntry<Set<String>> {

        public StringSetSettingsEntry(int keyResId, int defaultValueResId) {
            super(keyResId, defaultValueResId);
        }

        @Override
        public Set<String> getDefaultValue(Context context) {
            Set<String> stringSet = new HashSet<>();
            Collections.addAll(stringSet,
                    context.getResources().getStringArray(getDefaultValueResId()));
            return stringSet;
        }

        @Override
        public Set<String> getValue(Context context) {
            return SharedPrefsUtils.getStringSet(this, context);
        }

        @Override
        public void putValue(Set<String> value, Context context) {
            SharedPrefsUtils.putStringSet(this, value, context);
        }
    }

    public static class IntegerSettingsEntry extends SettingsEntry<Integer> {

        public IntegerSettingsEntry(int keyResId, int defaultValueResId) {
            super(keyResId, defaultValueResId);
        }

        @Override
        public Integer getDefaultValue(Context context) {
            return context.getResources().getInteger(getDefaultValueResId());
        }

        @Override
        public Integer getValue(Context context) {
            return SharedPrefsUtils.getInt(this, context);
        }

        @Override
        public void putValue(Integer value, Context context) {
            SharedPrefsUtils.putInt(this, value, context);
        }
    }

    public static class LongSettingsEntry extends SettingsEntry<Long> {

        public LongSettingsEntry(int keyResId, int defaultValueResId) {
            super(keyResId, defaultValueResId);
        }

        @Override
        public Long getDefaultValue(Context context) {
            return Long.valueOf(context.getResources().getString(getDefaultValueResId()));
        }

        @Override
        public Long getValue(Context context) {
            return SharedPrefsUtils.getLong(this, context);
        }

        @Override
        public void putValue(Long value, Context context) {
            SharedPrefsUtils.putLong(this, value, context);
        }
    }

    public static class FloatSettingsEntry extends SettingsEntry<Float> {

        public FloatSettingsEntry(int keyResId, int defaultValueResId) {
            super(keyResId, defaultValueResId);
        }

        @Override
        public Float getDefaultValue(Context context) {
            return Float.valueOf(context.getResources().getString(getDefaultValueResId()));
        }

        @Override
        public Float getValue(Context context) {
            return SharedPrefsUtils.getFloat(this, context);
        }

        @Override
        public void putValue(Float value, Context context) {
            SharedPrefsUtils.putFloat(this, value, context);
        }
    }

    public static class BooleanSettingsEntry extends SettingsEntry<Boolean> {

        public BooleanSettingsEntry(int keyResId, int defaultValueResId) {
            super(keyResId, defaultValueResId);
        }

        @Override
        public Boolean getDefaultValue(Context context) {
            return context.getResources().getBoolean(getDefaultValueResId());
        }

        @Override
        public Boolean getValue(Context context) {
            return SharedPrefsUtils.getBoolean(this, context);
        }

        @Override
        public void putValue(Boolean value, Context context) {
            SharedPrefsUtils.putBoolean(this, value, context);
        }
    }

    public static class EnumSettingsEntry<E extends Enum<E>> extends StringSettingsEntry {

        private E[] mEnumValues;

        public EnumSettingsEntry(int keyResId, int defaultValueResId, Class<E> enumClass) {
            super(keyResId, defaultValueResId);

            mEnumValues = enumClass.getEnumConstants();
        }

        public E getDefaultEnumValue(Context context) {
            return mEnumValues[Integer.parseInt(getDefaultValue(context))];
        }

        public E getEnumValue(Context context) {
            int ordinal = Integer.parseInt(getValue(context));
            if (ordinal < 0 || ordinal >= mEnumValues.length) {
                LogUtils.w("Invalid ordinal " + ordinal + ", with key=" + getKey(context)
                        + ", enum values=" + Arrays.toString(mEnumValues)
                        + ", reverting to default value");
                E enumValue = getDefaultEnumValue(context);
                putEnumValue(enumValue, context);
                return enumValue;
            }
            return mEnumValues[ordinal];
        }

        public void putEnumValue(E value, Context context) {
            putValue(String.valueOf(value.ordinal()), context);
        }
    }
}
