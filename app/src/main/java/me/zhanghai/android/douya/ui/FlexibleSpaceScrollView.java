/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

public class FlexibleSpaceScrollView extends ScrollView {

    public FlexibleSpaceScrollView(Context context) {
        super(context);
    }

    public FlexibleSpaceScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FlexibleSpaceScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FlexibleSpaceScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        // Do not save the current scroll position. Always store scrollY=0 and delegate
        // responsibility of saving state to the MultiShrinkScroller.
        int scrollY = getScrollY();
        setScrollY(0);
        final Parcelable savedState = super.onSaveInstanceState();
        setScrollY(scrollY);
        return savedState;
    }
}
