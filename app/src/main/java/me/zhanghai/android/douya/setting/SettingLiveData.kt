/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.setting

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import androidx.annotation.ArrayRes
import androidx.annotation.BoolRes
import androidx.annotation.DimenRes
import androidx.annotation.IntegerRes
import androidx.annotation.StringRes
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.preference.PreferenceManager
import me.zhanghai.android.douya.appContext
import me.zhanghai.android.douya.util.getBoolean
import me.zhanghai.android.douya.util.getFloat
import me.zhanghai.android.douya.util.getInteger
import me.zhanghai.android.douya.util.getStringArray
import kotlin.reflect.KClass

abstract class SettingLiveData<T>(
    @StringRes keyRes: Int,
    private val defaultValue: T
) : LiveData<T>(), OnSharedPreferenceChangeListener {

    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(appContext)

    private val key = appContext.getString(keyRes)

    init {
        loadValue()
        // Only a weak reference is stored so we don't need to worry about unregistering.
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key == this.key) {
            loadValue()
        }
    }

    private fun loadValue() {
        value = readValue(sharedPreferences, key, defaultValue)
    }

    protected abstract fun readValue(
        sharedPreferences: SharedPreferences,
        key: String,
        defaultValue: T
    ): T

    fun putValue(value: T) {
        writeValue(sharedPreferences, key, value)
    }

    protected abstract fun writeValue(sharedPreferences: SharedPreferences, key: String, value: T)
}

class NullableStringSettingLiveData(
    @StringRes keyRes: Int,
    @StringRes defaultValueRes: Int? = null
) : SettingLiveData<String?>(
    keyRes, if (defaultValueRes != null) appContext.getString(defaultValueRes) else null
) {

    override fun readValue(
        sharedPreferences: SharedPreferences,
        key: String,
        defaultValue: String?
    ) = sharedPreferences.getString(key, defaultValue)

    override fun writeValue(
        sharedPreferences: SharedPreferences,
        key: String,
        value: String?
    ) = sharedPreferences.edit { putString(key, value) }
}

class StringSettingLiveData(
    @StringRes keyRes: Int,
    @StringRes defaultValueRes: Int
) : SettingLiveData<String>(keyRes, appContext.getString(defaultValueRes)) {

    override fun readValue(
        sharedPreferences: SharedPreferences,
        key: String,
        defaultValue: String
    ) = sharedPreferences.getString(key, defaultValue)!!

    override fun writeValue(
        sharedPreferences: SharedPreferences,
        key: String,
        value: String
    ) = sharedPreferences.edit { putString(key, value) }
}

class StringSetSettingLiveData(
    @StringRes keyRes: Int,
    @ArrayRes defaultValueRes: Int
) : SettingLiveData<Set<String?>>(keyRes, appContext.getStringArray(defaultValueRes).toSet()) {

    override fun readValue(
        sharedPreferences: SharedPreferences,
        key: String,
        defaultValue: Set<String?>
    ): Set<String> = sharedPreferences.getStringSet(key, defaultValue)!!

    override fun writeValue(
        sharedPreferences: SharedPreferences,
        key: String,
        value: Set<String?>
    ) = sharedPreferences.edit { putStringSet(key, value) }
}

class IntegerSettingLiveData(
    @StringRes keyRes: Int,
    @IntegerRes defaultValueRes: Int
) : SettingLiveData<Int>(keyRes, appContext.getInteger(defaultValueRes)) {

    override fun readValue(sharedPreferences: SharedPreferences, key: String, defaultValue: Int) =
        sharedPreferences.getInt(key, defaultValue)

    override fun writeValue(sharedPreferences: SharedPreferences, key: String, value: Int) =
        sharedPreferences.edit { putInt(key, value) }
}

class LongSettingLiveData(
    @StringRes keyRes: Int,
    @StringRes defaultValueRes: Int
) : SettingLiveData<Long>(keyRes, appContext.getString(defaultValueRes).toLong()) {

    override fun readValue(sharedPreferences: SharedPreferences, key: String, defaultValue: Long) =
        sharedPreferences.getLong(key, defaultValue)

    override fun writeValue(sharedPreferences: SharedPreferences, key: String, value: Long) =
        sharedPreferences.edit { putLong(key, value) }
}

class FloatSettingLiveData(
    @StringRes keyRes: Int, @DimenRes defaultValueRes: Int
) : SettingLiveData<Float>(keyRes, appContext.getFloat(defaultValueRes)) {

    override fun readValue(sharedPreferences: SharedPreferences, key: String, defaultValue: Float) =
        sharedPreferences.getFloat(key, defaultValue)

    override fun writeValue(sharedPreferences: SharedPreferences, key: String, value: Float) =
        sharedPreferences.edit { putFloat(key, value) }
}

class BooleanSettingLiveData(
    @StringRes keyRes: Int,
    @BoolRes defaultValueRes: Int
) : SettingLiveData<Boolean>(keyRes, appContext.getBoolean(defaultValueRes)) {

    override fun readValue(
        sharedPreferences: SharedPreferences,
        key: String,
        defaultValue: Boolean
    ) = sharedPreferences.getBoolean(key, defaultValue)

    override fun writeValue(sharedPreferences: SharedPreferences, key: String, value: Boolean) =
        sharedPreferences.edit { putBoolean(key, value) }
}

// Use string resource for default value so that we can support ListPreference.
class EnumSettingLiveData<E : Enum<E>>(
    @StringRes keyRes: Int,
    @StringRes defaultValueRes: Int,
    enumClass: KClass<E>
) : SettingLiveData<E>(
    keyRes, enumClass.java.enumConstants!![appContext.getString(defaultValueRes).toInt()]
) {

    private val enumValues = enumClass.java.enumConstants!!

    override fun readValue(sharedPreferences: SharedPreferences, key: String, defaultValue: E): E {
        val ordinal = sharedPreferences.getString(key, null)?.toInt() ?: return defaultValue
        return if (ordinal in enumValues.indices) enumValues[ordinal] else defaultValue
    }

    override fun writeValue(
        sharedPreferences: SharedPreferences, key: String,
        value: E
    ) = sharedPreferences.edit {
        putString(key, value.ordinal.toString())
    }
}
