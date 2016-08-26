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
import android.widget.ScrollView;

public class FlexibleSpaceContentScrollView extends ScrollView implements FlexibleSpaceContentView {

    public FlexibleSpaceContentScrollView(Context context) {
        super(context);
    }

    public FlexibleSpaceContentScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FlexibleSpaceContentScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FlexibleSpaceContentScrollView(Context context, AttributeSet attrs, int defStyleAttr,
                                          int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public int getScroll() {
        return getScrollY();
    }

    @Override
    public void scrollTo(int scroll) {
        scrollTo(0, scroll);
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
        // Do not save the current scroll position. Always store scrollY as 0 and delegate
        // responsibility of saving state to FlexibleSpaceLayout.
        int scrollY = getScrollY();
        setScrollY(0);
        final Parcelable savedState = super.onSaveInstanceState();
        setScrollY(scrollY);
        return savedState;
    }
}
