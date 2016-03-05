/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.util.MathUtils;
import me.zhanghai.android.douya.util.ViewUtils;

/**
 * Set the initial layout_height to match_parent or wrap_content instead a specific value so that
 * the view measures itself correctly for the first time.
 */
public class ProfileHeaderLayout extends RelativeLayout implements FlexibleSpaceView {

    @Bind(R.id.appBar)
    LinearLayout mAppBarLayout;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    private int mScroll;

    public ProfileHeaderLayout(Context context) {
        super(context);
    }

    public ProfileHeaderLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ProfileHeaderLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ProfileHeaderLayout(Context context, AttributeSet attrs, int defStyleAttr,
                               int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        ButterKnife.bind(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (MeasureSpec.getMode(heightMeasureSpec) != MeasureSpec.EXACTLY) {
            int height = getMaxHeight();
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
            mAppBarLayout.getLayoutParams().height = height / 2;
        } else {
            int height = getLayoutParams().height;
            int maxHeight = getMaxHeight();
            int minHeight = getMinHeight();
            float fraction = MathUtils.unlerp(minHeight, maxHeight, height);
            mAppBarLayout.getLayoutParams().height = MathUtils.lerp(minHeight, maxHeight / 2,
                    fraction);
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
        return mToolbar.getHeight();
    }

    private int getMaxHeight() {
        ViewParent viewParent = getParent();
        if (viewParent instanceof View) {
            return ((View) viewParent).getHeight() * 2 / 3;
        }
        return 0;
    }
}
