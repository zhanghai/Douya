/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;

import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.util.ViewUtils;

public class ColorAccentSwipeRefreshLayout extends SwipeRefreshLayout {

    public ColorAccentSwipeRefreshLayout(@NonNull Context context) {
        super(context);

        init();
    }

    public ColorAccentSwipeRefreshLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    private void init() {
        setColorSchemeColors(ViewUtils.getColorFromAttrRes(R.attr.colorAccent, 0, getContext()));
    }
}
