/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.app;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import me.zhanghai.android.douya.util.FragmentUtils;

public class TargetedAtParentFragment extends Fragment {

    public static final int REQUEST_CODE_INVALID = -1;

    private static final String KEY_PREFIX = TargetedAtParentFragment.class.getName() + '.';

    private static final String EXTRA_REQUEST_CODE = KEY_PREFIX + "request_code";

    private int mRequestCode = REQUEST_CODE_INVALID;

    public int getRequestCode() {
        return mRequestCode;
    }

    protected void putRequestCode(int requestCode) {
        FragmentUtils.getArgumentsBuilder(this)
                .putInt(EXTRA_REQUEST_CODE, requestCode);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRequestCode = getArguments().getInt(EXTRA_REQUEST_CODE);
    }

    protected Fragment getTarget() {
        return getParentFragment();
    }
}
