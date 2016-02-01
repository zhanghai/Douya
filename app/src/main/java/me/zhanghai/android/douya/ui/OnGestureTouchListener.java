/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public abstract class OnGestureTouchListener implements View.OnTouchListener {

    private final GestureDetectorCompat mGestureDetector;

    public OnGestureTouchListener(Context context, boolean isLongPressEnabled) {
        mGestureDetector = new GestureDetectorCompat(context, new OnGestureListener());
        mGestureDetector.setIsLongpressEnabled(isLongPressEnabled);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    // SimpleOnGestureListener conditionally implements OnContextClickListener.
    private final class OnGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent event) {
            // Return true because we want system to report subsequent events to us.
            return true;
        }

        @Override
        public void onShowPress(MotionEvent event) {
            OnGestureTouchListener.this.onShowPress(event);
        }

        @Override
        public boolean onScroll(MotionEvent event1, MotionEvent event2, float distanceX,
                                float distanceY) {
            return OnGestureTouchListener.this.onScroll(event1, event2, distanceX, distanceY);
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX,
                               float velocityY) {
            return OnGestureTouchListener.this.onFling(event1, event2, velocityX, velocityY);
        }

        @Override
        public void onLongPress(MotionEvent event) {
            OnGestureTouchListener.this.onLongPress(event);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            return OnGestureTouchListener.this.onSingleTapUp(event);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent event) {
            return OnGestureTouchListener.this.onSingleTapConfirmed(event);
        }

        @Override
        public boolean onDoubleTap(MotionEvent event) {
            return OnGestureTouchListener.this.onDoubleTap(event);
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent event) {
            return OnGestureTouchListener.this.onDoubleTapEvent(event);
        }

        @Override
        public boolean onContextClick(MotionEvent event) {
            return OnGestureTouchListener.this.onContextClick(event);
        }
    }

    public void onShowPress(MotionEvent event) {}

    public boolean onScroll(MotionEvent event1, MotionEvent event2, float distanceX,
                            float distanceY) {
        return false;
    }

    public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX,
                           float velocityY) {
        return false;
    }

    public void onLongPress(MotionEvent event) {}

    public boolean onSingleTapUp(MotionEvent event) {
        return false;
    }

    public boolean onSingleTapConfirmed(MotionEvent event) {
        return false;
    }

    public boolean onDoubleTap(MotionEvent event) {
        return false;
    }

    public boolean onDoubleTapEvent(MotionEvent event) {
        return false;
    }

    // Will only be called on API level >= 23.
    public boolean onContextClick(MotionEvent event) {
        return false;
    }
}
