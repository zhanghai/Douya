/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.zhanghai.android.douya.R;

public class NotYetImplementedFragment extends Fragment {

    public static NotYetImplementedFragment newInstance() {
        //noinspection deprecation
        return new NotYetImplementedFragment();
    }

    /**
     * @deprecated Use {@link #newInstance()} instead.
     */
    public NotYetImplementedFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.not_yet_implemented_fragment, container, false);
    }
}
