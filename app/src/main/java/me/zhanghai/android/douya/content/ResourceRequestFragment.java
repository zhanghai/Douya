/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.content;

import android.annotation.SuppressLint;
import android.os.Bundle;

import me.zhanghai.android.douya.app.TargetedRetainedFragment;
import me.zhanghai.android.douya.util.FragmentUtils;

public class ResourceRequestFragment extends TargetedRetainedFragment {

    private boolean mRecreated;

    /**
     * @deprecated Use {@link #ResourceRequestFragment(boolean)} instead.
     */
    public ResourceRequestFragment() {
        mRecreated = true;
    }

    @SuppressLint("ValidFragment")
    public ResourceRequestFragment(boolean unused) {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mRecreated) {
            FragmentUtils.remove(this);
        }
    }

    public boolean isRecreated() {
        return mRecreated;
    }
}
