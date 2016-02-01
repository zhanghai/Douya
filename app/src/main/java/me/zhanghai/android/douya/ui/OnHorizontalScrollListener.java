/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.support.v7.widget.RecyclerView;

public abstract class OnHorizontalScrollListener extends RecyclerView.OnScrollListener {

    @Override
    public final void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        if (!recyclerView.canScrollHorizontally(-1)) {
            onScrolledToLeft();
        } else if (!recyclerView.canScrollHorizontally(1)) {
            onScrolledToRight();
        } else if (dx < 0) {
            onScrolledLeft();
        } else if (dx > 0) {
            onScrolledRight();
        }
    }

    public void onScrolledLeft() {}

    public void onScrolledRight() {}

    public void onScrolledToLeft() {}

    public void onScrolledToRight() {}
}
