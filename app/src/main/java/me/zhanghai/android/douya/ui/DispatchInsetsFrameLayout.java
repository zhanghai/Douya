/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.GravityCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.FrameLayout;

/**
 * @see DispatchInsetsHelper
 */
public class DispatchInsetsFrameLayout extends FrameLayout {

    private DispatchInsetsHelper mInsetsHelper = new DispatchInsetsHelper(
            new DispatchInsetsHelper.Delegate() {

                @Override
                public int getGravityFromLayoutParams(ViewGroup.LayoutParams layoutParams) {
                    int gravity = ((LayoutParams) layoutParams).gravity;
                    // See FrameLayout.layoutChildren().
                    return gravity != -1 ? gravity : Gravity.TOP | GravityCompat.START;
                }

                @Override
                public ViewGroup getOwner() {
                    return DispatchInsetsFrameLayout.this;
                }

                @Override
                public void superAddView(View child, int index, ViewGroup.LayoutParams params) {
                    DispatchInsetsFrameLayout.super.addView(child, index, params);
                }

                @Override
                public boolean superAddViewInLayout(View child, int index,
                                                    ViewGroup.LayoutParams params,
                                                    boolean preventRequestLayout) {
                    return DispatchInsetsFrameLayout.super.addViewInLayout(child, index, params,
                            preventRequestLayout);
                }
            });

    public DispatchInsetsFrameLayout(Context context) {
        super(context);
    }

    public DispatchInsetsFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DispatchInsetsFrameLayout(Context context, AttributeSet attrs,
                                     int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DispatchInsetsFrameLayout(Context context, AttributeSet attrs,
                                     int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public WindowInsets dispatchApplyWindowInsets(WindowInsets insets) {
        return mInsetsHelper.dispatchApplyWindowInsets(insets);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        mInsetsHelper.addView(child, index, params);
    }

    @Override
    protected boolean addViewInLayout(View child, int index, ViewGroup.LayoutParams params,
                                      boolean preventRequestLayout) {
        return mInsetsHelper.addViewInLayout(child, index, params, preventRequestLayout);
    }
}
