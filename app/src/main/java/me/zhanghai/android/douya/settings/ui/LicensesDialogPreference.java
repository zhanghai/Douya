/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.settings.ui;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.preference.DialogPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.AttributeSet;

import de.psdev.licensesdialog.LicensesDialogFragment;
import me.zhanghai.android.douya.R;

public class LicensesDialogPreference extends DialogPreference {

    // As in PreferenceFragmentCompat, because we want to ensure that at most one dialog is showing.
    private static final String DIALOG_FRAGMENT_TAG =
            "android.support.v7.preference.PreferenceFragment.DIALOG";

    /**
     * Users should override {@link PreferenceFragmentCompat#onDisplayPreferenceDialog(Preference)}
     * and check the return value of this method, only call through to super implementation if
     * {@code false} is returned.
     *
     * @param preferenceFragment The preference fragment
     * @param preference The preference, as in
     * {@link PreferenceFragmentCompat#onDisplayPreferenceDialog(Preference)}
     * @return Whether the call has been handled by this method.
     */
    public static boolean onDisplayPreferenceDialog(PreferenceFragmentCompat preferenceFragment,
                                                    Preference preference) {

        if (preference instanceof LicensesDialogPreference) {
            // getChildFragmentManager() will lead to looking for target fragment in the child
            // fragment manager.
            FragmentManager fragmentManager = preferenceFragment.getFragmentManager();
            if(fragmentManager.findFragmentByTag(DIALOG_FRAGMENT_TAG) == null) {
                LicensesDialogFragment dialogFragment =
                        new LicensesDialogFragment.Builder(preferenceFragment.getActivity())
                                .setNotices(R.raw.licenses)
                                .setUseAppCompat(true)
                                .build();
                dialogFragment.setTargetFragment(preferenceFragment, 0);
                dialogFragment.show(fragmentManager, DIALOG_FRAGMENT_TAG);
            }
            return true;
        }

        return false;
    }

    public LicensesDialogPreference(Context context) {
        super(context);
    }

    public LicensesDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LicensesDialogPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public LicensesDialogPreference(Context context, AttributeSet attrs, int defStyleAttr,
                                    int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
