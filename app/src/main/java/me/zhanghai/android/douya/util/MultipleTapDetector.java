/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util;

import android.view.MotionEvent;
import android.view.ViewConfiguration;

/**
 * In fact a {@code MultipleClickDetector}.
 */
public class MultipleTapDetector {

    private int mTapCount;
    private Listener mListener;

    private long mLastDownMillis;
    private long mLastTapUpMillis;
    private int mCurrentTapCount;

    public MultipleTapDetector(int tapCount, Listener listener) {
        mTapCount = tapCount;
        mListener = listener;
    }

    public void onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mLastDownMillis = event.getEventTime();
                break;
            case MotionEvent.ACTION_UP: {
                long eventTime = event.getEventTime();
                if (eventTime - mLastDownMillis < ViewConfiguration.getTapTimeout()) {
                    // This is a tap.
                    if (mCurrentTapCount == 0 || (eventTime - mLastTapUpMillis
                            < ViewConfiguration.getDoubleTapTimeout())) {
                        // This is a multiple tap.
                        mLastTapUpMillis = eventTime;
                        ++mCurrentTapCount;
                        if (mCurrentTapCount == mTapCount) {
                            mListener.onMultipleTapDetected();
                        } else {
                            // Wait for more multiple taps.
                            return;
                        }
                    }
                }
                mLastTapUpMillis = 0;
                mCurrentTapCount = 0;
                break;
            }
        }
    }

    public interface Listener {
        void onMultipleTapDetected();
    }
}
