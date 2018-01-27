/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;

/**
 * An ImageView that measures with a ratio. Also sets scaleType to centerCrop.
 */
public class RatioImageView extends AdjustViewBoundsImageView {

    private float mRatio;

    public RatioImageView(Context context) {
        super(context);

        init();
    }

    public RatioImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public RatioImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        setScaleType(ScaleType.CENTER_CROP);
    }

    public float getRatio() {
        return mRatio;
    }

    public void setRatio(float ratio) {
        if (mRatio != ratio) {
            mRatio = ratio;
            requestLayout();
            invalidate();
        }
    }

    public void setRatio(float width, float height) {
        setRatio(width / height);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        if (mRatio > 0) {
            if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) {
                int height = MeasureSpec.getSize(heightMeasureSpec);
                int width = Math.round(mRatio * height);
                width = Math.max(width, getSuggestedMinimumWidth());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    width = Math.min(width, getMaxWidth());
                }
                widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
            } else {
                int width = MeasureSpec.getSize(widthMeasureSpec);
                int height = Math.round(width / mRatio);
                height = Math.max(height, getSuggestedMinimumHeight());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    height = Math.min(height, getMaxHeight());
                }
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
            }
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
