/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.NavigationView;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowInsets;

/**
 * A {@link NavigationView} that draws no scrim and dispatches window insets correctly.
 * <p>
 * {@code android:fitsSystemWindows="true"} will be ignored.
 * </p>
 */
public class FullscreenNavigationView extends NavigationView {

    public FullscreenNavigationView(Context context) {
        super(context);

        init();
    }

    public FullscreenNavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public FullscreenNavigationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {

        // Required to revert the value of fitsSystemWindows set in super constructor.
        setFitsSystemWindows(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            // The ViewCompat version cannot clear the listener.
            // https://code.google.com/p/android/issues/detail?id=196113
            //ViewCompat.setOnApplyWindowInsetsListener(this, null)
            setOnApplyWindowInsetsListener(null);
            View child = getChildAt(0);
            if (!(child instanceof NavigationMenuView) || getChildCount() > 1) {
                throw new IllegalStateException("Design support library has changed its " +
                        "implementation of NavigationView, please re-check the code");
            }
            child.setFitsSystemWindows(true);
        }
    }

    @Override
    @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
    public WindowInsets dispatchApplyWindowInsets(WindowInsets insets) {
        // HeaderView does not need top inset.
        insets = insets.replaceSystemWindowInsets(insets.getSystemWindowInsetLeft(), 0,
                insets.getSystemWindowInsetRight(), insets.getSystemWindowInsetBottom());
        return super.dispatchApplyWindowInsets(insets);
    }
}
