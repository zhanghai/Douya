/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.settings.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;

import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompatDividers;

import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.settings.info.Settings;
import me.zhanghai.android.douya.util.NightModeHelper;
import me.zhanghai.android.douya.util.SharedPrefsUtils;

public class SettingsFragment extends PreferenceFragmentCompatDividers
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreatePreferencesFix(@Nullable Bundle savedInstanceState,
                                       @Nullable String rootKey) {
        addPreferencesFromResource(R.xml.settings);
    }

    @Override
    public void onStart() {
        super.onStart();

        SharedPrefsUtils.getSharedPrefs().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        SharedPrefsUtils.getSharedPrefs().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(@NonNull SharedPreferences sharedPreferences,
                                          @NonNull String key) {
        if (!TextUtils.equals(key, Settings.NIGHT_MODE.getKey())) {
            return;
        }
        NightModeHelper.updateNightMode(requireActivity());
    }
}
