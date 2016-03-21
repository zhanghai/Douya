/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import me.zhanghai.android.douya.R;

public class RatioHeightRecyclerView extends RecyclerView {

    private float mRatio;

    public RatioHeightRecyclerView(Context context) {
        super(context);

        init(getContext(), null, 0);
    }

    public RatioHeightRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(getContext(), attrs, 0);
    }

    public RatioHeightRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init(getContext(), attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RatioHeightRecyclerView,
                defStyle, 0);
        String ratio = a.getString(R.styleable.RatioHeightRecyclerView_ratio);
        if (ratio != null) {
            int colonIndex = ratio.indexOf(':');
            if (colonIndex < 0) {
                throw new IllegalArgumentException(
                        "ratio should be a string in the form \"width:height\": " + ratio);
            }
            int width = Integer.parseInt(ratio.substring(0, colonIndex));
            int height = Integer.parseInt(ratio.substring(colonIndex + 1));
            mRatio = (float) width / height;
        }
        a.recycle();
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
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = Math.round(width / mRatio);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                height = Math.max(height, getMinimumHeight());
            }
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
