/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class ProfileScrollLayout extends FlexibleSpaceScrollLayout {

    private boolean mFinishing;

    public ProfileScrollLayout(Context context) {
        super(context);
    }

    public ProfileScrollLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ProfileScrollLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ProfileScrollLayout(Context context, AttributeSet attrs, int defStyleAttr,
                               int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        if (getChildCount() != 1) {
            throw new IllegalStateException("Must have one and only one child");
        }
    }

    public int getOffset() {
        return getChildAt(0).getTop();
    }

    public void offsetBy(int delta) {
        offsetTo(getOffset() + delta);
    }

    public void offsetTo(int offset) {
        int oldOffset = getOffset();
        if (oldOffset == offset || offset < 0) {
            return;
        }
        ViewCompat.offsetTopAndBottom(getChildAt(0), offset - oldOffset);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (mFinishing) {
            return false;
        } else {
            return super.onInterceptTouchEvent(event);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mFinishing) {
            return false;
        } else {
            return super.onTouchEvent(event);
        }
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        if (mFinishing) {
            return false;
        } else {
            return super.onGenericMotionEvent(event);
        }
    }

    @Override
    protected void onDrag(MotionEvent event, float delta) {
        if (delta > 0) {
            int oldScroll = getScroll();
            scrollBy((int) -delta);
            delta += getScroll() - oldScroll;
            offsetBy((int) delta);
        } else {
            int oldOffset = getOffset();
            offsetBy((int) delta);
            delta -= getOffset() - oldOffset;
            int oldScroll = getScroll();
            scrollBy((int) -delta);
            delta += getScroll() - oldScroll;
            if (delta < 0) {
                pullEdgeEffectBottom(event, delta);
            }
        }
    }

    @Override
    protected void onDragEnd(boolean cancelled) {
        if (getOffset() > 0) {
            finish();
        } else {
            super.onDragEnd(cancelled);
        }
    }

    private void finish() {

        mFinishing = true;
        recycleVelocityTrackerIfHas();

        ((Activity) getContext()).finish();
    }
}
