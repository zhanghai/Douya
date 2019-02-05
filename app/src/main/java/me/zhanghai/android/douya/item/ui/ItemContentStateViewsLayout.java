/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.item.ui;

import android.content.Context;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import android.util.AttributeSet;
import android.widget.FrameLayout;

import butterknife.BindDimen;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;

public class ItemContentStateViewsLayout extends FrameLayout {

    @BindDimen(R.dimen.item_content_padding_top_max)
    int mPaddingTopMax;

    private float mBackdropRatio;
    private int mPaddingTopPaddingExtra;

    public ItemContentStateViewsLayout(@NonNull Context context) {
        super(context);

        init();
    }

    public ItemContentStateViewsLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public ItemContentStateViewsLayout(@NonNull Context context, @Nullable AttributeSet attrs,
                                       int defStyle) {
        super(context, attrs, defStyle);

        init();
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public ItemContentStateViewsLayout(@NonNull Context context, @Nullable AttributeSet attrs,
                                       int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init();
    }

    private void init() {
        ButterKnife.bind(this);
    }

    public float getBackdropRatio() {
        return mBackdropRatio;
    }

    public void setBackdropRatio(float ratio) {
        if (mBackdropRatio != ratio) {
            mBackdropRatio = ratio;
            requestLayout();
            invalidate();
        }
    }

    public void setBackdropRatio(float width, float height) {
        setBackdropRatio(width / height);
    }

    public int getPaddingTopPaddingExtra() {
        return mPaddingTopPaddingExtra;
    }

    public void setPaddingTopPaddingExtra(int paddingTopPaddingExtra) {
        if (mPaddingTopPaddingExtra != paddingTopPaddingExtra) {
            mPaddingTopPaddingExtra = paddingTopPaddingExtra;
            requestLayout();
            invalidate();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int paddingTop = 0;
        if (mBackdropRatio > 0) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            // Should fix off-by-one-pixel visual glitch.
            paddingTop += (int) (width / mBackdropRatio);
        }
        paddingTop += mPaddingTopPaddingExtra;
        if (mPaddingTopMax > 0) {
            paddingTop = Math.min(paddingTop, mPaddingTopMax);
        }
        setPadding(getPaddingLeft(), paddingTop, getPaddingRight(), getPaddingBottom());
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
