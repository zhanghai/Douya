/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import java.util.HashMap;
import java.util.Map;

/**
 * A Fragment that can retain data passed in, and remove them once your instance is recreated.
 */
public class RetainDataFragment extends Fragment {

    private static final String FRAGMENT_TAG = RetainDataFragment.class.getName();

    private Map<String, Object> mData = new HashMap<>();

    public static RetainDataFragment attachTo(FragmentActivity activity) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        RetainDataFragment fragment = (RetainDataFragment) fragmentManager
                .findFragmentByTag(FRAGMENT_TAG);
        if (fragment == null) {
            fragment = new RetainDataFragment();
            fragmentManager.beginTransaction()
                    .add(fragment, FRAGMENT_TAG)
                    .commit();
        }
        return fragment;
    }

    /**
     * This attaches the RetainDataFragment to the host Activity, so that it can retain instance.
     * For this reason you need to differentiate your key with other Fragment and the host Activity.
     */
    public static RetainDataFragment attachTo(Fragment fragment) {
        return attachTo(fragment.getActivity());
    }

    public boolean containsKey(String key) {
        return mData.containsKey(key);
    }

    public <T> T remove(String key) {
        //noinspection unchecked
        return (T) mData.remove(key);
    }

    public boolean removeBoolean(String key, boolean defaultValue) {
        Boolean b = remove(key);
        return b != null ? b : defaultValue;
    }

    public void put(String key, Object value) {
        mData.put(key, value);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
        setUserVisibleHint(false);
    }

    public void onDestroy() {
        super.onDestroy();

        mData.clear();
    }
}
