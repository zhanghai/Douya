/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewConfiguration;

public abstract class OnVerticalScrollWithPagingSlopListener extends OnVerticalScrollListener {

    private final int mPagingTouchSlop;

    // Distance in y since last idle or direction change.
    private int mDy;

    public OnVerticalScrollWithPagingSlopListener(Context context) {
        mPagingTouchSlop = ViewConfiguration.get(context).getScaledPagingTouchSlop();
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            mDy = 0;
        }
    }

    @Override
    public void onScrolledUp(int dy) {
        mDy = mDy < 0 ? mDy + dy : dy;
        if (mDy < -mPagingTouchSlop) {
            onScrolledUp();
        }
    }

    @Override
    public void onScrolledDown(int dy) {
        mDy = mDy > 0 ? mDy + dy : dy;
        if (mDy > mPagingTouchSlop) {
            onScrolledDown();
        }
    }
}
