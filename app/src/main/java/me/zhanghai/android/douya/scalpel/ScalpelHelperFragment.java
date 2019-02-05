/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.scalpel;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import me.zhanghai.android.douya.app.RetainedFragment;
import me.zhanghai.android.douya.eventbus.EventBusUtils;

public class ScalpelHelperFragment extends RetainedFragment {

    private static final String FRAGMENT_TAG = ScalpelHelperFragment.class.getName();

    private boolean mEnabled;

    private boolean mActivityCreated;
    private boolean mInjected;

    /**
     * @deprecated Use {@link #attachToActivity(Fragment)} instead.
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

    public static ScalpelHelperFragment attachToActivity(Fragment fragment) {
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

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onSetEnabled(SetEnabledEvent event) {
        setEnabledForActivity(event.enabled);
    }

    private static class SetEnabledEvent {

        public boolean enabled;

        public SetEnabledEvent(boolean enabled) {
            this.enabled = enabled;
        }
    }
}
