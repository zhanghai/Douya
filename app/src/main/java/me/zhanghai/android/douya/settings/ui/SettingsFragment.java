/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.settings.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.preference.Preference;

import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompatDividers;

import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.ui.LicensesDialogPreference;

public class SettingsFragment extends PreferenceFragmentCompatDividers {

    @Override
    public void onCreatePreferencesFix(@Nullable Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.settings);
    }

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        if (!LicensesDialogPreference.onDisplayPreferenceDialog(this, preference)) {
            super.onDisplayPreferenceDialog(preference);
        }
    }
}
