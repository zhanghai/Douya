/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * RecyclerView always returns true for its {@link RecyclerView#onTouchEvent(MotionEvent)}. This
 * subclass passes the same touch event up to it's parent view and cancels if user starts dragging.
 */
public class DispatchTouchEventToParentNestedRecyclerView extends NestedRecyclerView {

    public DispatchTouchEventToParentNestedRecyclerView(Context context) {
        super(context);
    }

    public DispatchTouchEventToParentNestedRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DispatchTouchEventToParentNestedRecyclerView(Context context, AttributeSet attrs,
                                                        int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        if (getScrollState() != SCROLL_STATE_DRAGGING) {
            ((View) getParent()).onTouchEvent(event);
        } else {
            int oldAction = event.getAction();
            event.setAction(MotionEvent.ACTION_CANCEL | (oldAction & ~MotionEvent.ACTION_MASK));
            ((View) getParent()).onTouchEvent(event);
            event.setAction(oldAction);
        }
        return true;
    }
}
