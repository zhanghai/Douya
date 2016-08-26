/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * In order to retain instance, this fragment should always be attached to the host activity
 * directly instead of being attached to any child fragment manager.
 */
public class RetainedFragment extends Fragment {

    private List<Runnable> mPendingRunnables = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
        setUserVisibleHint(false);
    }

    @Override
    public void onResume() {
        super.onResume();

        Iterator<Runnable> iterator = mPendingRunnables.iterator();
        while (iterator.hasNext()) {
            Runnable runnable = iterator.next();
            iterator.remove();
            runnable.run();
        }
    }

    protected void postOnResumed(Runnable runnable) {
        if (isResumed()) {
            runnable.run();
        } else {
            mPendingRunnables.add(runnable);
        }
    }
}
