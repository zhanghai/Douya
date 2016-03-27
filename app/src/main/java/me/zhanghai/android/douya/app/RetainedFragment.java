/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

/**
 * In order to retain instance, this fragment should always be attached to the host activity
 * directly instead of being attached to any child fragment manager.
 */
public class RetainedFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
        setUserVisibleHint(false);
    }

    public static <T extends RetainedFragment> T findByTag(FragmentActivity activity, String tag) {
        //noinspection unchecked
        return (T) activity.getSupportFragmentManager().findFragmentByTag(tag);
    }

    public void addTo(FragmentActivity activity, String tag) {
        activity.getSupportFragmentManager().beginTransaction()
                .add(this, tag)
                .commit();
    }

    public void remove() {

        if (isRemoving()) {
            return;
        }

        getActivity().getSupportFragmentManager().beginTransaction()
                .remove(this)
                .commit();
    }
}
