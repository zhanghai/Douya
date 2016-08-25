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

        init(null, 0, 0);
    }

    public MaxDimensionDispatchInsetsFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(attrs, 0, 0);
    }

    public MaxDimensionDispatchInsetsFrameLayout(Context context, AttributeSet attrs,
                                                 int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(attrs, defStyleAttr, 0);
    }

    public MaxDimensionDispatchInsetsFrameLayout(Context context, AttributeSet attrs,
                                                 int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init(attrs, defStyleAttr, defStyleRes);
    }

    private void init(AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        mMaxDimensionHelper.onInit(getContext(), attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        mMaxDimensionHelper.onMeasure(widthSpec, heightSpec);
    }
}
