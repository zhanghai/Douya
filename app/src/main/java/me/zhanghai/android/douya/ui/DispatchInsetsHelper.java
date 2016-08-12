/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.FrameLayout;

/**
 * Helper for dispatching transformed insets to children. Also dispatch insets to newly added child.
 */
// Notice that WindowInsets is immutable.
public class DispatchInsetsHelper {

    private Delegate mDelegate;

    // TODO: Use Object for compatibility?
    private WindowInsets mInsets;

    public DispatchInsetsHelper(Delegate delegate) {
        mDelegate = delegate;
    }

    // Not calling super deliberately.
    @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
    public WindowInsets dispatchApplyWindowInsets(WindowInsets insets) {

        mInsets = insets;

        ViewGroup viewGroup = mDelegate.getOwner();
        int layoutDirection = ViewCompat.getLayoutDirection(viewGroup);
        for (int i = 0, count = viewGroup.getChildCount(); i < count; ++i) {
            View child = viewGroup.getChildAt(i);
            dispatchInsetsToChild(layoutDirection, child, child.getLayoutParams());
        }

        return insets.consumeSystemWindowInsets();
    }

    public void addView(View child, int index, ViewGroup.LayoutParams params) {

        if (mInsets != null) {
            dispatchInsetsToChild(child, params);
        }

        mDelegate.superAddView(child, index, params);
    }

    public boolean addViewInLayout(View child, int index, ViewGroup.LayoutParams params,
                                   boolean preventRequestLayout) {

        if (mInsets != null) {
            dispatchInsetsToChild(child, params);
        }

        return mDelegate.superAddViewInLayout(child, index, params, preventRequestLayout);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
    private void dispatchInsetsToChild(int layoutDirection, View child,
                                       ViewGroup.LayoutParams childLayoutParams) {

        int childGravity = GravityCompat.getAbsoluteGravity(
                mDelegate.getGravityFromLayoutParams(childLayoutParams), layoutDirection);

        // In fact equivalent to the algorithm in Gravity.apply().

        int childInsetLeft = mInsets.getSystemWindowInsetLeft();
        int childInsetRight = mInsets.getSystemWindowInsetRight();
        if (childLayoutParams.width != FrameLayout.LayoutParams.MATCH_PARENT) {
            if ((childGravity & (Gravity.AXIS_PULL_BEFORE << Gravity.AXIS_X_SHIFT)) == 0) {
                childInsetLeft = 0;
            }
            if ((childGravity & (Gravity.AXIS_PULL_AFTER << Gravity.AXIS_X_SHIFT)) == 0) {
                childInsetRight = 0;
            }
        }

        int childInsetTop = mInsets.getSystemWindowInsetTop();
        int childInsetBottom = mInsets.getSystemWindowInsetBottom();
        if (childLayoutParams.height != FrameLayout.LayoutParams.MATCH_PARENT) {
            if ((childGravity & (Gravity.AXIS_PULL_BEFORE << Gravity.AXIS_Y_SHIFT)) == 0) {
                childInsetTop = 0;
            }
            if ((childGravity & (Gravity.AXIS_PULL_AFTER << Gravity.AXIS_Y_SHIFT)) == 0) {
                childInsetBottom = 0;
            }
        }

        WindowInsets childInsets = mInsets.replaceSystemWindowInsets(childInsetLeft,
                childInsetTop, childInsetRight, childInsetBottom);
        child.dispatchApplyWindowInsets(childInsets);
    }

    private void dispatchInsetsToChild(View child, ViewGroup.LayoutParams childLayoutParams) {
        dispatchInsetsToChild(ViewCompat.getLayoutDirection(mDelegate.getOwner()), child,
                childLayoutParams);
    }

    public interface Delegate {
        int getGravityFromLayoutParams(ViewGroup.LayoutParams layoutParams);
        ViewGroup getOwner();
        void superAddView(View child, int index, ViewGroup.LayoutParams params);
        boolean superAddViewInLayout(View child, int index, ViewGroup.LayoutParams params,
                                boolean preventRequestLayout);
    }
}
