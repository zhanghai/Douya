/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.preference.DialogPreference;

import me.zhanghai.android.douya.util.FragmentUtils;

public class RingtonePreferenceActivityFragmentCompat extends Fragment {

    private static final String KEY_PREFIX =
            RingtonePreferenceActivityFragmentCompat.class.getName() + '.';

    private static final String KEY_PREFERENCE_KEY = KEY_PREFIX + "PREFERENCE_KEY";
    private static final String KEY_PICKER_INTENT = KEY_PREFIX + "PICKER_INTENT";

    private static final int REQUEST_CODE_PICKER = 1;

    private String mPreferenceKey;
    private Intent mPickerIntent;

    private boolean mShouldStartPicker;

    public static RingtonePreferenceActivityFragmentCompat newInstance(String preferenceKey,
                                                                       Intent pickerIntent) {
        RingtonePreferenceActivityFragmentCompat fragment =
                new RingtonePreferenceActivityFragmentCompat();
        Bundle arguments = FragmentUtils.ensureArguments(fragment);
        arguments.putString(KEY_PREFERENCE_KEY, preferenceKey);
        arguments.putParcelable(KEY_PICKER_INTENT, pickerIntent);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        mPreferenceKey = arguments.getString(KEY_PREFERENCE_KEY);
        mPickerIntent = arguments.getParcelable(KEY_PICKER_INTENT);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState == null) {
            mShouldStartPicker = true;
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mShouldStartPicker) {
            startActivityForResult(mPickerIntent, REQUEST_CODE_PICKER);
            mShouldStartPicker = false;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICKER) {
            if (data != null) {
                DialogPreference.TargetFragment targetFragment = (DialogPreference.TargetFragment)
                        getTargetFragment();
                RingtonePreference preference = (RingtonePreference) targetFragment.findPreference(
                        mPreferenceKey);
                if (preference != null) {
                    Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                    if (preference.callChangeListener(uri)) {
                        preference.setRingtoneUri(uri);
                    }
                }
            }
            FragmentUtils.remove(this);
        }
    }
}
