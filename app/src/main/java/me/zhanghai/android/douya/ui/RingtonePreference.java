/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.Settings;
import androidx.core.util.ObjectsCompat;
import androidx.preference.Preference;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompat;

/**
 * A {@link Preference} that allows the user to choose a ringtone from those on the device.
 * The chosen ringtone's URI will be persisted as a string.
 * <p>
 * If the user chooses the "Default" item, the saved string will be one of
 * {@link Settings.System#DEFAULT_RINGTONE_URI},
 * {@link Settings.System#DEFAULT_NOTIFICATION_URI}, or
 * {@link Settings.System#DEFAULT_ALARM_ALERT_URI}. If the user chooses the "Silent"
 * item, the saved string will be an empty string.
 *
 * @attr ref android.R.styleable#RingtonePreference_ringtoneType
 * @attr ref android.R.styleable#RingtonePreference_showDefault
 * @attr ref android.R.styleable#RingtonePreference_showSilent
 */
public class RingtonePreference extends Preference {

    private static final int[] COM_ANDROID_INTERNAL_R_STYLEABLE_RINGTONE_PREFERENCE = {
            android.R.attr.ringtoneType,
            android.R.attr.showDefault,
            android.R.attr.showSilent
    };
    private static final int COM_ANDROID_INTERNAL_R_STYLEABLE_RINGTONE_PREFERENCE_RINGTONE_TYPE = 0;
    private static final int COM_ANDROID_INTERNAL_R_STYLEABLE_RINGTONE_PREFERENCE_SHOW_DEFAULT = 1;
    private static final int COM_ANDROID_INTERNAL_R_STYLEABLE_RINGTONE_PREFERENCE_SHOW_SILENT = 2;

    private int mRingtoneType = RingtoneManager.TYPE_RINGTONE;
    private boolean mShowDefault = true;
    private boolean mShowSilent = true;

    private Uri mRingtoneUri;
    private boolean mRingtoneSet;

    static {
        PreferenceFragmentCompat.registerPreferenceFragment(RingtonePreference.class,
                RingtonePreferenceActivityFragmentCompat.class);
    }

    public RingtonePreference(Context context, AttributeSet attrs, int defStyleAttr,
                              int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init(attrs, defStyleAttr, defStyleRes);
    }

    public RingtonePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(attrs, defStyleAttr, 0);
    }

    public RingtonePreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(attrs, 0, 0);
    }

    public RingtonePreference(Context context) {
        super(context);
    }

    private void init(AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray a = getContext().obtainStyledAttributes(attrs,
                COM_ANDROID_INTERNAL_R_STYLEABLE_RINGTONE_PREFERENCE, defStyleAttr, defStyleRes);
        //noinspection ResourceType
        mRingtoneType = a.getInt(COM_ANDROID_INTERNAL_R_STYLEABLE_RINGTONE_PREFERENCE_RINGTONE_TYPE,
                mRingtoneType);
        //noinspection ResourceType
        mShowDefault = a.getBoolean(
                COM_ANDROID_INTERNAL_R_STYLEABLE_RINGTONE_PREFERENCE_SHOW_DEFAULT, mShowDefault);
        //noinspection ResourceType
        mShowSilent = a.getBoolean(COM_ANDROID_INTERNAL_R_STYLEABLE_RINGTONE_PREFERENCE_SHOW_SILENT,
                mShowSilent);
        a.recycle();
    }

    /**
     * Returns the sound type(s) that are shown in the picker.
     *
     * @return The sound type(s) that are shown in the picker.
     * @see #setRingtoneType(int)
     */
    public int getRingtoneType() {
        return mRingtoneType;
    }

    /**
     * Sets the sound type(s) that are shown in the picker.
     *
     * @param type The sound type(s) that are shown in the picker.
     * @see RingtoneManager#EXTRA_RINGTONE_TYPE
     */
    public void setRingtoneType(int type) {
        mRingtoneType = type;
    }

    /**
     * Returns whether to a show an item for the default sound/ringtone.
     *
     * @return Whether to show an item for the default sound/ringtone.
     */
    public boolean getShowDefault() {
        return mShowDefault;
    }

    /**
     * Sets whether to show an item for the default sound/ringtone. The default
     * to use will be deduced from the sound type(s) being shown.
     *
     * @param showDefault Whether to show the default or not.
     * @see RingtoneManager#EXTRA_RINGTONE_SHOW_DEFAULT
     */
    public void setShowDefault(boolean showDefault) {
        mShowDefault = showDefault;
    }

    /**
     * Returns whether to a show an item for 'Silent'.
     *
     * @return Whether to show an item for 'Silent'.
     */
    public boolean getShowSilent() {
        return mShowSilent;
    }

    /**
     * Sets whether to show an item for 'Silent'.
     *
     * @param showSilent Whether to show 'Silent'.
     * @see RingtoneManager#EXTRA_RINGTONE_SHOW_SILENT
     */
    public void setShowSilent(boolean showSilent) {
        mShowSilent = showSilent;
    }

    @Override
    public CharSequence getSummary() {
        CharSequence summary = super.getSummary();
        if (!TextUtils.isEmpty(summary)) {
            Uri ringtoneUri = getRingtoneUri();
            String ringtoneTitle = "";
            if (ringtoneUri != null) {
                Context context = getContext();
                Ringtone ringtone = RingtoneManager.getRingtone(context, ringtoneUri);
                if (ringtone != null) {
                    ringtoneTitle = ringtone.getTitle(context);
                }
            }
            return String.format(summary.toString(), ringtoneTitle);
        } else {
            return summary;
        }
    }

    @Override
    protected void onClick() {
        getPreferenceManager().showDialog(this);
    }

    public Intent makeRingtonePickerIntent() {
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        onPrepareRingtonePickerIntent(intent);
        return intent;
    }

    /**
     * Prepares the intent to launch the ringtone picker. This can be modified
     * to adjust the parameters of the ringtone picker.
     *
     * @param ringtonePickerIntent The ringtone picker intent that can be
     *            modified by putting extras.
     */
    protected void onPrepareRingtonePickerIntent(Intent ringtonePickerIntent) {
        ringtonePickerIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,
                getRingtoneUri());
        ringtonePickerIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, mShowDefault);
        if (mShowDefault) {
            ringtonePickerIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI,
                    RingtoneManager.getDefaultUri(getRingtoneType()));
        }
        ringtonePickerIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, mShowSilent);
        ringtonePickerIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, mRingtoneType);
        ringtonePickerIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, getTitle());
        //ringtonePickerIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_AUDIO_ATTRIBUTES_FLAGS,
        //        AudioAttributes.FLAG_BYPASS_INTERRUPTION_POLICY);
    }

    /**
     * Sets the URI of the ringtone. Can return null to indicate no ringtone.
     *
     * @param ringtoneUri The URI of the ringtone.
     */
    public void setRingtoneUri(Uri ringtoneUri) {
        // Always persist/notify the first time.
        boolean changed = !ObjectsCompat.equals(mRingtoneUri, ringtoneUri);
        if (changed || !mRingtoneSet) {
            mRingtoneUri = ringtoneUri;
            mRingtoneSet = true;
            persistString(ringtoneUri != null ? ringtoneUri.toString() : "");
            if (changed) {
                notifyChanged();
            }
        }
    }

    /**
     * Returns the URI of the ringtone. Can return null to indicate no ringtone.
     *
     * @return The URI of the ringtone
     */
    public Uri getRingtoneUri() {
        return mRingtoneUri;
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            String persistedRingtoneString = getPersistedString(null);
            if (persistedRingtoneString != null) {
                setRingtoneUri(!TextUtils.isEmpty(persistedRingtoneString) ?
                        Uri.parse(persistedRingtoneString) : null);
            } else {
                setRingtoneUri(mRingtoneUri);
            }
        } else {
            String defaultValueString = (String) defaultValue;
            setRingtoneUri(!TextUtils.isEmpty(defaultValueString) ? Uri.parse(defaultValueString)
                    : null);
        }
    }
}
