/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FriendlyFragment;
import android.text.TextUtils;

import java.util.List;

import me.zhanghai.android.douya.util.FragmentUtils;

public class TargetedRetainedFragment extends RetainedFragment {

    public static final int REQUEST_CODE_INVALID = -1;

    private static final String KEY_PREFIX = TargetedRetainedFragment.class.getName() + '.';

    public static final String EXTRA_TARGETED_AT_ACTIVITY = KEY_PREFIX + "targeted_at_activity";
    public static final String EXTRA_TARGET_FRAGMENT_TAG = KEY_PREFIX + "target_fragment_tag";
    public static final String EXTRA_REQUEST_CODE = KEY_PREFIX + "request_code";

    private boolean mTargetedAtActivity;
    private String mTargetFragmentTag;
    private int mRequestCode = REQUEST_CODE_INVALID;

    private Fragment mTargetFragment;

    /**
     * Should be called in {@link Fragment#onDestroy()}.
     */
    public void detach() {
        if (!mTargetedAtActivity) {
            // Because this is called inside our target's onDestroy, it cannot be already detached,
            // so we can always find it.
            Fragment targetFragment = getTargetFragmentFriendly();
            // isRemoving() is not set when child fragment is destroyed due to parent removal, so we
            // have to walk through its ancestors.
            while (targetFragment != null) {
                if (targetFragment.isRemoving()) {
                    FragmentUtils.remove(this);
                    break;
                }
                targetFragment = targetFragment.getParentFragment();
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
        String tag = fragment.getTag();
        if (TextUtils.isEmpty(tag)) {
            throw new IllegalArgumentException("Target fragment must have a tag");
        }
        arguments.putString(EXTRA_TARGET_FRAGMENT_TAG, tag);
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
        mTargetFragmentTag = arguments.getString(EXTRA_TARGET_FRAGMENT_TAG);
        mRequestCode = arguments.getInt(EXTRA_REQUEST_CODE);
    }

    // Don't use get/setTargetFragment(); It can only target at fragments under the same fragment
    // manager.
    // The timing of calling this method can be tricky; However in most cases it will work.
    protected Fragment getTargetFragmentFriendly() {
        if (mTargetedAtActivity || mTargetFragmentTag == null) {
            throw new IllegalStateException("Target fragment not set");
        }
        if (mTargetFragment == null) {
            mTargetFragment = findFragmentByTagRecursively(
                    getActivity().getSupportFragmentManager(), mTargetFragmentTag);
        }
        return mTargetFragment;
    }

    private static Fragment findFragmentByTagRecursively(FragmentManager fragmentManager,
                                                         String tag) {
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments == null) {
            return null;
        }
        // Prefer a top-level result.
        for (Fragment fragment : fragments) {
            if (fragment != null && TextUtils.equals(fragment.getTag(), tag)) {
                return fragment;
            }
        }
        for (Fragment fragment : fragments) {
            if (fragment != null && FriendlyFragment.hasChildFragmentManager(fragment)) {
                Fragment result = findFragmentByTagRecursively(fragment.getChildFragmentManager(),
                        tag);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
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
            return getTargetFragmentFriendly();
        }
    }

    protected boolean hasRequestCode() {
        return mRequestCode != REQUEST_CODE_INVALID;
    }

    protected int getRequestCode() {
        return mRequestCode;
    }
}
