/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.view.ViewCompat;
import androidx.appcompat.widget.TintTypedArray;
import android.util.AttributeSet;
import android.view.View;

public class PercentageWidthView extends View {

    private static final int[] STYLEABLE = { android.R.attr.maxWidth };
    private static final int STYLEABLE_ANDROID_MAX_WIDTH = 0;

    private boolean mMaxWidthEnabled = true;
    private int mMaxWidth;
    private float mWidthPercentage;

    public PercentageWidthView(Context context) {
        super(context);
    }

    public PercentageWidthView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(attrs, 0, 0);
    }

    public PercentageWidthView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(attrs, defStyleAttr, 0);
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public PercentageWidthView(Context context, @Nullable AttributeSet attrs, int defStyleAttr,
                               int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init(attrs, defStyleAttr, defStyleRes);
    }

    @SuppressLint("RestrictedApi")
    private void init(@Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TintTypedArray a = TintTypedArray.obtainStyledAttributes(getContext(), attrs, STYLEABLE,
                defStyleAttr, defStyleRes);
        mMaxWidth = a.getDimensionPixelSize(STYLEABLE_ANDROID_MAX_WIDTH, mMaxWidth);
        a.recycle();
    }

    public void setMaxWidthEnabled(boolean maxWidthEnabled) {
        if (mMaxWidthEnabled == maxWidthEnabled) {
            return;
        }
        mMaxWidthEnabled = maxWidthEnabled;
        requestLayout();
    }

    public void setMaxWidth(int maxWidth) {
        if (mMaxWidth == maxWidth) {
            return;
        }
        mMaxWidth = maxWidth;
        if (mMaxWidthEnabled) {
            requestLayout();
        }
    }

    public void setWidthPercentage(float widthPercentage) {
        if (mWidthPercentage == widthPercentage) {
            return;
        }
        mWidthPercentage = widthPercentage;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = mMaxWidth;
        if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.AT_MOST) {
            mMaxWidth = Math.min(mMaxWidth, MeasureSpec.getSize(widthMeasureSpec));
        }
        width = Math.round(mWidthPercentage * width);
        width = Math.max(width, ViewCompat.getMinimumWidth(this));
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
