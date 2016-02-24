/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewParent;
import android.widget.RelativeLayout;

import me.zhanghai.android.douya.util.MathUtils;
import me.zhanghai.android.douya.util.ViewUtils;

/**
 * Set the initial layout_height to match_parent or wrap_content instead a specific value so that
 * the view measures itself correctly for the first time.
 */
public class ProfileHeaderView extends RelativeLayout implements FlexibleSpaceView {

    private int mScroll;

    public ProfileHeaderView(Context context) {
        super(context);
    }

    public ProfileHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ProfileHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ProfileHeaderView(Context context, AttributeSet attrs, int defStyleAttr,
                             int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (MeasureSpec.getMode(heightMeasureSpec) != MeasureSpec.EXACTLY) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(getMaxHeight(), MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public int getScroll() {
        return mScroll;
    }

    @Override
    public void scrollBy(int delta) {
        int maxHeight = getMaxHeight();
        int newScroll = MathUtils.clamp(mScroll + delta, 0, maxHeight - getMinHeight());
        if (mScroll == newScroll) {
            return;
        }
        ViewUtils.setHeight(this, maxHeight - newScroll);
        mScroll = newScroll;
    }

    private int getMinHeight() {
        // FIXME
        return 56 * 3;
    }

    private int getMaxHeight() {
        ViewParent viewParent = getParent();
        if (viewParent instanceof View) {
            return ((View) viewParent).getHeight() * 2 / 3;
        }
        return 0;
    }
}
