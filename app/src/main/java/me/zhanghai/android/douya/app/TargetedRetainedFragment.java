/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentAccessor;
import android.text.TextUtils;

import me.zhanghai.android.douya.util.FragmentUtils;

public class TargetedRetainedFragment extends RetainedFragment {

    public static final int REQUEST_CODE_INVALID = -1;

    private static final String KEY_PREFIX = TargetedRetainedFragment.class.getName() + '.';

    private static final String EXTRA_TARGET_FRAGMENT_WHO = KEY_PREFIX + "target_fragment_who";
    private static final String EXTRA_REQUEST_CODE = KEY_PREFIX + "request_code";

    private Fragment mTargetFragment;
    private int mRequestCode = REQUEST_CODE_INVALID;

    /**
     * Should be called in {@link Fragment#onDestroy()}.
     */
    public void detach() {

        // TODO: Safe transaction: delay until onResumed() or some where safe?
        // In case we did not reach onActivityCreated().
        if (getActivity() != null) {
            findTargetFragmentIf();
        }

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

    protected void targetAt(Fragment fragment, int requestCode) {
        Bundle arguments = FragmentUtils.ensureArguments(this);
        arguments.putString(EXTRA_TARGET_FRAGMENT_WHO, FragmentAccessor.getWho(fragment));
        arguments.putInt(EXTRA_REQUEST_CODE, requestCode);
    }

    protected void targetAt(Fragment fragment) {
        targetAt(fragment, REQUEST_CODE_INVALID);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRequestCode = getArguments().getInt(EXTRA_REQUEST_CODE);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Must be after onCreate() so that all child fragment managers has restored their state.
        findTargetFragmentIf();
    }

    // Don't use get/setTargetFragment(); It can only target at fragments under the same fragment
    // manager.
    // The timing of calling this method can be tricky; However in most cases it will work.
    private void findTargetFragmentIf() {

        if (mTargetFragment != null) {
            return;
        }

        String who = getArguments().getString(EXTRA_TARGET_FRAGMENT_WHO);
        if (TextUtils.isEmpty(who)) {
            throw new IllegalStateException("Target fragment not set");
        }
        mTargetFragment = FragmentAccessor.findByWho(getActivity(), who);
        if (mTargetFragment == null) {
            throw new IllegalStateException("Target fragment not found");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mTargetFragment = null;
    }

    protected Fragment getTarget() {
        findTargetFragmentIf();
        return mTargetFragment;
    }

    protected boolean hasRequestCode() {
        return mRequestCode != REQUEST_CODE_INVALID;
    }

    protected int getRequestCode() {
        return mRequestCode;
    }
}
