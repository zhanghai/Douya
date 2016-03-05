/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;

/**
 * @see MaxDimensionHelper
 * @see DispatchInsetsFrameLayout
 */
public class MaxDimensionDispatchInsetsFrameLayout extends DispatchInsetsFrameLayout {

    private MaxDimensionHelper mMaxDimensionHelper = new MaxDimensionHelper(
            new MaxDimensionHelper.Delegate() {
                @SuppressLint("WrongCall")
                @Override
                public void superOnMeasure(int widthSpec, int heightSpec) {
                    MaxDimensionDispatchInsetsFrameLayout.super.onMeasure(widthSpec, heightSpec);
                }
            });

    public MaxDimensionDispatchInsetsFrameLayout(Context context) {
        super(context);

        init(getContext(), null, 0, 0);
    }

    public MaxDimensionDispatchInsetsFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(getContext(), attrs, 0, 0);
    }

    public MaxDimensionDispatchInsetsFrameLayout(Context context, AttributeSet attrs,
                                                 int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(getContext(), attrs, defStyleAttr, 0);
    }

    public MaxDimensionDispatchInsetsFrameLayout(Context context, AttributeSet attrs,
                                                 int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init(getContext(), attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        mMaxDimensionHelper.onInit(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        mMaxDimensionHelper.onMeasure(widthSpec, heightSpec);
    }
}
