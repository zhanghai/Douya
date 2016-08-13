/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.content.Context;
import android.util.AttributeSet;

@SuppressWarnings("deprecation")
public class NestedRatioHeightRecyclerView extends RatioHeightRecyclerView {

    public NestedRatioHeightRecyclerView(Context context) {
        super(context);

        init();
    }

    public NestedRatioHeightRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public NestedRatioHeightRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init();
    }

    private void init() {
        setFocusableInTouchMode(false);
    }
}
