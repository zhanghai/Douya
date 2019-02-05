/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewConfiguration;

public abstract class OnVerticalScrollWithPagingTouchSlopListener extends OnVerticalScrollListener {

    private final int mPagingTouchSlop;

    // Distance in y since last idle or direction change.
    private int mDy;

    public OnVerticalScrollWithPagingTouchSlopListener(Context context) {
        mPagingTouchSlop = ViewConfiguration.get(context).getScaledPagingTouchSlop();
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            mDy = 0;
        }
    }

    @Override
    public final void onScrolledUp(int dy) {
        mDy = mDy < 0 ? mDy + dy : dy;
        if (mDy < -mPagingTouchSlop) {
            onScrolledUp();
        }
    }

    @Override
    public final void onScrolledDown(int dy) {
        mDy = mDy > 0 ? mDy + dy : dy;
        if (mDy > mPagingTouchSlop) {
            onScrolledDown();
        }
    }

    public void onScrolledUp() {}

    public void onScrolledDown() {}
}
