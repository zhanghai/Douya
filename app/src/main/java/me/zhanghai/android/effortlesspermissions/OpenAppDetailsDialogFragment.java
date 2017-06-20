/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.effortlesspermissions;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialogFragment;

import me.zhanghai.android.douya.util.FragmentUtils;

public class OpenAppDetailsDialogFragment extends AppCompatDialogFragment {

    private static final String KEY_PREFIX = OpenAppDetailsDialogFragment.class.getName() + '.';

    private static final String EXTRA_REQUEST_CODE = KEY_PREFIX + "REQUEST_CODE";
    private static final String EXTRA_PACKAGE_NAME = KEY_PREFIX + "PACKAGE_NAME";
    private static final String EXTRA_TITLE = KEY_PREFIX + "TITLE";
    private static final String EXTRA_MESSAGE = KEY_PREFIX + "MESSAGE";
    private static final String EXTRA_POSITIVE_BUTTON_TEXT = KEY_PREFIX + "POSITIVE_BUTTON_TEXT";
    private static final String EXTRA_NEGATIVE_BUTTON_TEXT = KEY_PREFIX + "NEGATIVE_BUTTON_TEXT";
    private static final String EXTRA_CANCELABLE = KEY_PREFIX + "CANCELABLE";

    private static final int REQUEST_CODE_INVALID = -1;

    private int mRequestCode;
    private String mPackageName;
    private CharSequence mTitle;
    private CharSequence mMessage;
    private CharSequence mPositiveButtonText;
    private CharSequence mNegativeButtonText;
    private boolean mCancelable;

    public static OpenAppDetailsDialogFragment newInstance(int requestCode, String packageName,
                                                           CharSequence title,
                                                           CharSequence message,
                                                           CharSequence positiveButtonText,
                                                           CharSequence negativeButtonText,
                                                           boolean cancelable) {
        //noinspection deprecation
        OpenAppDetailsDialogFragment fragment = new OpenAppDetailsDialogFragment();
        Bundle arguments = FragmentUtils.ensureArguments(fragment);
        arguments.putInt(EXTRA_REQUEST_CODE, requestCode);
        arguments.putString(EXTRA_PACKAGE_NAME, packageName);
        arguments.putCharSequence(EXTRA_TITLE, title);
        arguments.putCharSequence(EXTRA_MESSAGE, message);
        arguments.putCharSequence(EXTRA_POSITIVE_BUTTON_TEXT, positiveButtonText);
        arguments.putCharSequence(EXTRA_NEGATIVE_BUTTON_TEXT, negativeButtonText);
        arguments.putBoolean(EXTRA_CANCELABLE, cancelable);
        return fragment;
    }

    /**
     * @deprecated Use
     * {@link #newInstance(int, String, CharSequence, CharSequence, CharSequence, CharSequence, boolean)}
     * instead.
     */
    public OpenAppDetailsDialogFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        mRequestCode = arguments.getInt(EXTRA_REQUEST_CODE);
        mPackageName = arguments.getString(EXTRA_PACKAGE_NAME);
        mTitle = arguments.getCharSequence(EXTRA_TITLE);
        mMessage = arguments.getCharSequence(EXTRA_MESSAGE);
        mPositiveButtonText = arguments.getCharSequence(EXTRA_POSITIVE_BUTTON_TEXT);
        mNegativeButtonText = arguments.getCharSequence(EXTRA_NEGATIVE_BUTTON_TEXT);
        mCancelable = arguments.getBoolean(EXTRA_CANCELABLE);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Activity activity = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, getTheme());
        if (mTitle != null) {
            builder.setTitle(mTitle);
        }
        if (mMessage != null) {
            builder.setMessage(mMessage);
        }
        CharSequence positiveButtonText = mPositiveButtonText;
        if (positiveButtonText == null) {
            positiveButtonText = activity.getText(android.R.string.ok);
        }
        builder.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openAppDetails();
            }
        });
        if (mNegativeButtonText != null) {
            builder.setNegativeButton(mNegativeButtonText, null);
        }
        builder.setCancelable(mCancelable);
        return builder.create();
    }

    @Override
    public void setTargetFragment(Fragment fragment, int requestCode) {
        super.setTargetFragment(fragment, requestCode);

        mRequestCode = requestCode;
        getArguments().putInt(EXTRA_REQUEST_CODE, requestCode);
    }

    private void openAppDetails() {
        Activity activity = getActivity();
        String packageName = mPackageName != null ? mPackageName : activity.getPackageName();
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                .setData(Uri.fromParts("package", packageName, null));
        Fragment targetFragment = getTargetFragment();
        if (targetFragment != null) {
            if (mRequestCode != REQUEST_CODE_INVALID) {
                targetFragment.startActivityForResult(intent, mRequestCode);
            } else {
                targetFragment.startActivity(intent);
            }
            return;
        }
        Fragment parentFragment = getParentFragment();
        if (parentFragment != null) {
            if (mRequestCode != REQUEST_CODE_INVALID) {
                parentFragment.startActivityForResult(intent, mRequestCode);
            } else {
                parentFragment.startActivity(intent);
            }
            return;
        }
        if (mRequestCode != REQUEST_CODE_INVALID) {
            activity.startActivityForResult(intent, mRequestCode);
        } else {
            activity.startActivity(intent);
        }
    }

    public static void show(CharSequence title, CharSequence message,
                            CharSequence positiveButtonText, CharSequence negativeButtonText,
                            Fragment fragment) {
        OpenAppDetailsDialogFragment.newInstance(REQUEST_CODE_INVALID, null, title, message,
                positiveButtonText, negativeButtonText, false)
                .show(fragment.getChildFragmentManager(), null);
    }

    public static void show(@StringRes int titleRes, @StringRes int messageRes,
                            @StringRes int positiveButtonTextRes,
                            @StringRes int negativeButtonTextRes, Fragment fragment) {
        show(fragment.getText(titleRes), fragment.getText(messageRes),
                fragment.getText(positiveButtonTextRes), fragment.getText(negativeButtonTextRes),
                fragment);
    }

    public static void show(@StringRes int messageRes, @StringRes int positiveButtonTextRes,
                            Fragment fragment) {
        show(null, fragment.getText(messageRes), fragment.getText(positiveButtonTextRes),
                fragment.getText(android.R.string.cancel), fragment);
    }

    public static void show(CharSequence title, CharSequence message,
                            CharSequence positiveButtonText, CharSequence negativeButtonText,
                            AppCompatActivity activity) {
        OpenAppDetailsDialogFragment.newInstance(REQUEST_CODE_INVALID, null, title, message,
                positiveButtonText, negativeButtonText, false)
                .show(activity.getSupportFragmentManager(), null);
    }

    public static void show(@StringRes int titleRes, @StringRes int messageRes,
                            @StringRes int positiveButtonTextRes,
                            @StringRes int negativeButtonTextRes, AppCompatActivity activity) {
        show(activity.getText(titleRes), activity.getText(messageRes),
                activity.getText(positiveButtonTextRes), activity.getText(negativeButtonTextRes),
                activity);
    }

    public static void show(@StringRes int messageRes, @StringRes int positiveButtonTextRes,
                            AppCompatActivity activity) {
        show(null, activity.getText(messageRes), activity.getText(positiveButtonTextRes),
                activity.getText(android.R.string.cancel), activity);
    }
}
