/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FriendlyFragment;
import android.text.TextUtils;

import me.zhanghai.android.douya.util.FragmentUtils;

public class TargetedRetainedFragment extends RetainedFragment {

    public static final int REQUEST_CODE_INVALID = -1;

    private static final String KEY_PREFIX = TargetedRetainedFragment.class.getName() + '.';

    public static final String EXTRA_TARGETED_AT_ACTIVITY = KEY_PREFIX + "targeted_at_activity";
    public static final String EXTRA_TARGET_FRAGMENT_WHO = KEY_PREFIX + "target_fragment_who";
    public static final String EXTRA_REQUEST_CODE = KEY_PREFIX + "request_code";

    private boolean mTargetedAtActivity;
    private Fragment mTargetFragment;
    private int mRequestCode = REQUEST_CODE_INVALID;

    /**
     * Should be called in {@link Fragment#onDestroy()}.
     */
    public void detach() {
        if (!mTargetedAtActivity) {
            // Because this is called inside our target's onDestroy, it cannot be already detached,
            // so we can always find it.
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

    protected void targetAtActivity(int requestCode) {
        Bundle arguments = FragmentUtils.ensureArguments(this);
        arguments.putBoolean(EXTRA_TARGETED_AT_ACTIVITY, true);
        arguments.putInt(EXTRA_REQUEST_CODE, requestCode);
    }

    protected void targetAtActivity() {
        targetAtActivity(REQUEST_CODE_INVALID);
    }

    protected void targetAtFragment(Fragment fragment, int requestCode) {
        Bundle arguments = FragmentUtils.ensureArguments(this);
        arguments.putBoolean(EXTRA_TARGETED_AT_ACTIVITY, false);
        arguments.putString(EXTRA_TARGET_FRAGMENT_WHO, FriendlyFragment.getWho(fragment));
        arguments.putInt(EXTRA_REQUEST_CODE, requestCode);
    }

    protected void targetAtFragment(Fragment fragment) {
        targetAtFragment(fragment, REQUEST_CODE_INVALID);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        mTargetedAtActivity = arguments.getBoolean(EXTRA_TARGETED_AT_ACTIVITY);
        mRequestCode = arguments.getInt(EXTRA_REQUEST_CODE);
    }

    // Must be after onCreate() so that all child fragment managers has restored their state.
    @Override
    public void onStart() {
        super.onStart();

        findTargetFragmentIf();
    }

    // Don't use get/setTargetFragment(); It can only target at fragments under the same fragment
    // manager.
    // The timing of calling this method can be tricky; However in most cases it will work.
    private void findTargetFragmentIf() {

        if (mTargetedAtActivity || mTargetFragment != null) {
            return;
        }

        String who = getArguments().getString(EXTRA_TARGET_FRAGMENT_WHO);
        if (TextUtils.isEmpty(who)) {
            throw new IllegalStateException("Target fragment not set");
        }
        mTargetFragment = FriendlyFragment.findByWho(getActivity(), who);
        if (mTargetFragment == null) {
            throw new IllegalStateException("Target fragment not found");
        }
    }

    private void saveTargetFragmentIf() {

        if (mTargetedAtActivity) {
            return;
        }

        getArguments().putString(EXTRA_TARGET_FRAGMENT_WHO,
                FriendlyFragment.getWho(mTargetFragment));
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mTargetFragment = null;
    }

    protected Object getTarget() {
        if (mTargetedAtActivity) {
            return getActivity();
        } else {
            findTargetFragmentIf();
            return mTargetFragment;
        }
    }

    protected boolean hasRequestCode() {
        return mRequestCode != REQUEST_CODE_INVALID;
    }

    protected int getRequestCode() {
        return mRequestCode;
    }
}
