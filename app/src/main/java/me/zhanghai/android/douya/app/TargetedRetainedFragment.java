/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.app;

import androidx.fragment.app.Fragment;

import me.zhanghai.android.douya.util.FragmentUtils;

public class TargetedRetainedFragment extends RetainedFragment {

    public static final int REQUEST_CODE_INVALID = -1;

    private Fragment mTargetFragment;
    private int mRequestCode = REQUEST_CODE_INVALID;

    public void setTarget(Fragment targetFragment, int requestCode) {
        mTargetFragment = targetFragment;
        mRequestCode = requestCode;
    }

    public void setTarget(Fragment fragment) {
        setTarget(fragment, REQUEST_CODE_INVALID);
    }

    protected Fragment getTarget() {
        return mTargetFragment;
    }

    protected boolean hasRequestCode() {
        return mRequestCode != REQUEST_CODE_INVALID;
    }

    protected int getRequestCode() {
        return mRequestCode;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mTargetFragment = null;
    }

    /**
     * Should be called in {@link Fragment#onDestroy()}.
     */
    public void detach() {
        Fragment fragment = mTargetFragment;
        // isRemoving() is not set when child fragment is destroyed due to parent removal, so we
        // have to walk through its ancestors.
        while (fragment != null) {
            if (fragment.isRemoving()) {
                FragmentUtils.remove(this);
                break;
            }
            fragment = fragment.getParentFragment();
        }
    }
}
