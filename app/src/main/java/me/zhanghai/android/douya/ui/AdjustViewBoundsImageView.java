/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import android.util.AttributeSet;

/**
 * Fixes setAdjustViewBounds() and onMeasure() not respecting minimum sizes when adjusting view
 * bounds.
 */
public class AdjustViewBoundsImageView extends AppCompatImageView {

    public AdjustViewBoundsImageView(Context context) {
        super(context);
    }

    public AdjustViewBoundsImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AdjustViewBoundsImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setAdjustViewBounds(boolean adjustViewBounds) {
        ScaleType scaleType = getScaleType();
        super.setAdjustViewBounds(adjustViewBounds);
        setScaleType(scaleType);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int measuredWidth = getMeasuredWidth();
        int width = Math.max(measuredWidth, getSuggestedMinimumWidth());
        int measuredHeight = getMeasuredHeight();
        int height = Math.max(measuredHeight, getSuggestedMinimumHeight());
        if (measuredWidth != width || measuredHeight != height) {
            setMeasuredDimension(width, height);
        }
    }
}
