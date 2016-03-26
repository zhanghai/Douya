/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.app;

import android.support.v4.app.Fragment;

public class TargetedRetainedFragment extends RetainedFragment {

    public static final int REQUEST_CODE_INVALID = -1;

    private boolean mTargetedAtActivity;
    private int mActivityRequestCode = REQUEST_CODE_INVALID;

    protected void targetAtActivity(int requestCode) {
        mTargetedAtActivity = true;
        mActivityRequestCode = requestCode;
    }

    protected void targetAtActivity() {
        targetAtActivity(REQUEST_CODE_INVALID);
    }

    @Override
    public void setTargetFragment(Fragment fragment, int requestCode) {
        throw new UnsupportedOperationException("Target fragment is managed within this fragment");
    }

    protected void targetAtFragment(Fragment fragment, int requestCode) {
        mTargetedAtActivity = false;
        super.setTargetFragment(fragment, requestCode);
    }

    protected void targetAtFragment(Fragment fragment) {
        targetAtFragment(fragment, REQUEST_CODE_INVALID);
    }

    protected Object getTarget() {
        if (mTargetedAtActivity) {
            return getActivity();
        } else {
            return getTargetFragment();
        }
    }

    protected boolean hasRequestCode() {
        return getRequestCode() != REQUEST_CODE_INVALID;
    }

    protected int getRequestCode() {
        if (mTargetedAtActivity) {
            return mActivityRequestCode;
        } else {
            return getTargetRequestCode();
        }
    }
}
