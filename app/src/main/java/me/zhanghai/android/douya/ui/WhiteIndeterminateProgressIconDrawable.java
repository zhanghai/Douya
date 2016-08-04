/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import me.zhanghai.android.materialprogressbar.IndeterminateProgressDrawable;

public class WhiteIndeterminateProgressIconDrawable extends Drawable implements Animatable {

    private static final int ICON_SIZE_DP = 24;
    private static final int ICON_PADDING_DP = 2;

    private int mIntrinsicSize;
    private int mIntrinsicPadding;

    private ProgressDrawable mProgressDrawable;

    public WhiteIndeterminateProgressIconDrawable(Context context) {

        mProgressDrawable = new ProgressDrawable(context);

        float density = context.getResources().getDisplayMetrics().density;
        mIntrinsicSize = Math.round(ICON_SIZE_DP * density);
        mIntrinsicPadding = Math.round(ICON_PADDING_DP * density);

        setTint(Color.WHITE);
    }

    @Override
    @SuppressLint("NewApi")
    public int getAlpha() {
        return mProgressDrawable.getAlpha();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAlpha(int alpha) {
        mProgressDrawable.setAlpha(alpha);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressLint("NewApi")
    public ColorFilter getColorFilter() {
        return mProgressDrawable.getColorFilter();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mProgressDrawable.setColorFilter(colorFilter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressLint("NewApi")
    public void setTint(@ColorInt int tintColor) {
        mProgressDrawable.setTint(tintColor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressLint("NewApi")
    public void setTintList(@Nullable ColorStateList tint) {
        mProgressDrawable.setTintList(tint);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressLint("NewApi")
    public void setTintMode(@NonNull PorterDuff.Mode tintMode) {
        mProgressDrawable.setTintMode(tintMode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void start() {
        mProgressDrawable.start();
    }

    @Override
    public void stop() {
        mProgressDrawable.stop();
    }

    @Override
    public boolean isRunning() {
        return mProgressDrawable.isRunning();
    }

    @Override
    public int getIntrinsicWidth() {
        return mIntrinsicSize;
    }

    @Override
    public int getIntrinsicHeight() {
        return mIntrinsicSize;
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);

        int paddingHorizontal = (right - left) * mIntrinsicPadding / mIntrinsicSize;
        int paddingVertical = (bottom - top) * mIntrinsicPadding / mIntrinsicSize;
        mProgressDrawable.setBounds(left + paddingHorizontal, top + paddingVertical,
                right - paddingHorizontal, bottom - paddingVertical);
    }

    @Override
    public void draw(Canvas canvas) {
        mProgressDrawable.draw(canvas);
    }

    public class ProgressDrawable extends IndeterminateProgressDrawable {

        public ProgressDrawable(Context context) {
            super(context);
        }

        @Override
        public void invalidateSelf() {
            super.invalidateSelf();

            WhiteIndeterminateProgressIconDrawable.this.invalidateSelf();
        }
    }
}
