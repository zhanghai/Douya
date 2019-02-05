/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.MotionEvent;

import butterknife.BindInt;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.util.ColorUtils;
import me.zhanghai.android.douya.util.ViewUtils;

public class TransparentDoubleClickToolbar extends DoubleClickToolbar {

    @BindInt(android.R.integer.config_shortAnimTime)
    int mAnimationDuration;

    private int mTitleTextColor;

    private boolean mTransparent;
    private int mAlpha = 255;
    private ValueAnimator mAnimator;

    public TransparentDoubleClickToolbar(Context context) {
        super(context);

        init();
    }

    public TransparentDoubleClickToolbar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public TransparentDoubleClickToolbar(Context context, @Nullable AttributeSet attrs,
                                         int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        ButterKnife.bind(this);
        mTitleTextColor = ViewUtils.getColorFromAttrRes(android.R.attr.textColorPrimary, 0,
                getContext());
        ViewCompat.setBackground(this, getBackground().mutate());
    }

    public void setTransparent(boolean transparent) {
        if (mTransparent == transparent) {
            return;
        }
        mTransparent = transparent;
        if (mAnimator != null) {
            mAnimator.cancel();
            mAnimator = null;
        }
        setToolbarAlpha(mTransparent ? 0 : 255);
    }

    public void animateToTransparent(boolean transparent) {
        if (mTransparent == transparent) {
            return;
        }
        mTransparent = transparent;
        if (mAnimator != null) {
            mAnimator.cancel();
            mAnimator = null;
        }
        mAnimator = ValueAnimator.ofInt(mAlpha, mTransparent ? 0 : 255)
                .setDuration(mAnimationDuration);
        mAnimator.setInterpolator(new FastOutSlowInInterpolator());
        mAnimator.addUpdateListener(animation -> {
            int alpha = (int) animation.getAnimatedValue();
            setToolbarAlpha(alpha);
        });
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                mAnimator = null;
            }
            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimator = null;
            }
        });
        mAnimator.start();
    }

    private void setToolbarAlpha(int alpha) {
        if (mAlpha == alpha) {
            return;
        }
        mAlpha = alpha;
        setTitleTextColor(ColorUtils.blendAlphaComponent(mTitleTextColor, mAlpha));
        getBackground().setAlpha(mAlpha);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mAlpha == 0) {
            // We don't want to consume touch event if we are transparent.
            return false;
        }
        return super.onTouchEvent(event);
    }
}
