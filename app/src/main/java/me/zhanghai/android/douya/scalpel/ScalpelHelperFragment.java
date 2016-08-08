/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.scalpel;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import me.zhanghai.android.douya.app.RetainedFragment;
import me.zhanghai.android.douya.eventbus.EventBusUtils;

public class ScalpelHelperFragment extends RetainedFragment {

    private static final String FRAGMENT_TAG = ScalpelHelperFragment.class.getName();

    private boolean mEnabled;

    private boolean mActivityCreated;
    private boolean mInjected;

    /**
     * @deprecated Use {@link #attachTo(Fragment)} instead.
     */
    public static ScalpelHelperFragment attachTo(FragmentActivity activity) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        ScalpelHelperFragment fragment = (ScalpelHelperFragment) fragmentManager
                .findFragmentByTag(FRAGMENT_TAG);
        if (fragment == null) {
            fragment = new ScalpelHelperFragment();
            fragmentManager.beginTransaction()
                    .add(fragment, FRAGMENT_TAG)
                    .commit();
        }
        return fragment;
    }

    public static ScalpelHelperFragment attachTo(Fragment fragment) {
        //noinspection deprecation
        return attachTo(fragment.getActivity());
    }

    public static void setEnabled(boolean enabled) {
        EventBusUtils.postAsync(new SetEnabledEvent(enabled));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        EventBusUtils.register(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mActivityCreated = true;
        if (mEnabled) {
            enable();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        EventBusUtils.unregister(this);

        mActivityCreated = false;
        mInjected = false;
    }

    private void setEnabledForActivity(boolean enabled) {
        if (mActivityCreated) {
            if (enabled) {
                enable();
            } else if (mInjected) {
                ScalpelUtils.setEnabled(getActivity(), false);
            }
        }
        mEnabled = enabled;
    }

    private void enable() {
        if (!mInjected) {
            ScalpelUtils.inject(getActivity());
            mInjected = true;
        }
        ScalpelUtils.setEnabled(getActivity(), true);
    }

    @Keep
    public void onEventMainThread(SetEnabledEvent event) {
        setEnabledForActivity(event.enabled);
    }

    private static class SetEnabledEvent {

        public boolean enabled;

        public SetEnabledEvent(boolean enabled) {
            this.enabled = enabled;
        }
    }
}
