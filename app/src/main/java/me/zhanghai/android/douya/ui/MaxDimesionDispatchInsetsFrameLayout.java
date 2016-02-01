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
public class MaxDimesionDispatchInsetsFrameLayout extends DispatchInsetsFrameLayout {

    private MaxDimensionHelper mMaxDimensionHelper = new MaxDimensionHelper(
            new MaxDimensionHelper.Delegate() {
                @SuppressLint("WrongCall")
                @Override
                public void superOnMeasure(int widthSpec, int heightSpec) {
                    MaxDimesionDispatchInsetsFrameLayout.super.onMeasure(widthSpec, heightSpec);
                }
            });

    public MaxDimesionDispatchInsetsFrameLayout(Context context) {
        super(context);

        init(context, null, 0);
    }

    public MaxDimesionDispatchInsetsFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs, 0);
    }

    public MaxDimesionDispatchInsetsFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        mMaxDimensionHelper.onInit(context, attrs, defStyleAttr, 0);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        mMaxDimensionHelper.onMeasure(widthSpec, heightSpec);
    }
}
