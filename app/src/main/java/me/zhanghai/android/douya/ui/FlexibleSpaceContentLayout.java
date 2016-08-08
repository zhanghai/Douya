/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class FlexibleSpaceContentLayout extends ContentStateLayout
        implements FlexibleSpaceContentView {

    public FlexibleSpaceContentLayout(Context context) {
        super(context);
    }

    public FlexibleSpaceContentLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FlexibleSpaceContentLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public FlexibleSpaceContentLayout(Context context, AttributeSet attrs, int defStyleAttr,
                                      int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public int getScroll() {
        View contentView = getContentView();
        if (contentView instanceof FlexibleSpaceContentView) {
            return ((FlexibleSpaceContentView) contentView).getScroll();
        } else {
            return contentView.getScrollY();
        }
    }

    @Override
    public void scrollTo(int scroll) {
        View contentView = getContentView();
        if (contentView instanceof FlexibleSpaceContentView) {
            ((FlexibleSpaceContentView) contentView).scrollTo(scroll);
        } else {
            contentView.scrollTo(0, scroll);
        }
    }
}
