/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.core.view.GestureDetectorCompat;
import androidx.appcompat.widget.Toolbar;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class DoubleClickToolbar extends Toolbar {

    private GestureDetectorCompat mGestureDetectorCompat;
    private OnDoubleClickListener mOnDoubleClickListener;

    public DoubleClickToolbar(Context context) {
        super(context);

        init();
    }

    public DoubleClickToolbar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public DoubleClickToolbar(Context context, @Nullable AttributeSet attrs,
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
                            return mOnDoubleClickListener.onDoubleClick(DoubleClickToolbar.this);
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
