/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.item.ui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import butterknife.BindDimen;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.util.ViewUtils;

public class ItemContentStateViewsLayout extends FrameLayout {

    private float mBackdropRatio;

    public ItemContentStateViewsLayout(Context context) {
        super(context);
    }

    public ItemContentStateViewsLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ItemContentStateViewsLayout(Context context, @Nullable AttributeSet attrs,
                                       int defStyle) {
        super(context, attrs, defStyle);
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

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mBackdropRatio > 0) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int backdropHeight = Math.round(width / mBackdropRatio);
            if (ViewUtils.isInPortait(getContext())) {
                setPadding(getPaddingLeft(), backdropHeight, getPaddingRight(), getPaddingBottom());
            } else {
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(backdropHeight,
                        MeasureSpec.getMode(heightMeasureSpec));
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
