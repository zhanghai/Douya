/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class DoubleClickToolBar extends Toolbar {

    private GestureDetectorCompat mGestureDetectorCompat;
    private OnDoubleClickListener mOnDoubleClickListener;

    public DoubleClickToolBar(Context context) {
        super(context);

        init();
    }

    public DoubleClickToolBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public DoubleClickToolBar(Context context, @Nullable AttributeSet attrs,
                              int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        mGestureDetectorCompat = new GestureDetectorCompat(getContext(),
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDoubleTap(MotionEvent event) {
                        if (mOnDoubleClickListener != null) {
                            return mOnDoubleClickListener.onDoubleClick(DoubleClickToolBar.this);
                        }
                        return false;
                    }
                });
        mGestureDetectorCompat.setIsLongpressEnabled(false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean handled = super.onTouchEvent(event);
        mGestureDetectorCompat.onTouchEvent(event);
        return handled;
    }

    public void setOnDoubleClickListener(OnDoubleClickListener listener) {
        mOnDoubleClickListener = listener;
    }

    public interface OnDoubleClickListener {
        boolean onDoubleClick(View view);
    }
}
