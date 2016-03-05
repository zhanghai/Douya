/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

@SuppressLint("MissingSuperCall")
@TargetApi(Build.VERSION_CODES.M)
public class ForegroundRelativeLayout extends RelativeLayout {

    private ForegroundHelper mForegroundHelper = new ForegroundHelper(
            new ForegroundHelper.Delegate() {

                @Override
                public View getOwner() {
                    return ForegroundRelativeLayout.this;
                }

                @Override
                public int superGetForegroundGravity() {
                    return ForegroundRelativeLayout.super.getForegroundGravity();
                }

                @Override
                public void superSetForegroundGravity(int foregroundGravity) {
                    ForegroundRelativeLayout.super.setForegroundGravity(foregroundGravity);
                }

                @Override
                public void superSetVisibility(int visibility) {
                    ForegroundRelativeLayout.super.setVisibility(visibility);
                }

                @Override
                public boolean superVerifyDrawable(Drawable who) {
                    return ForegroundRelativeLayout.super.verifyDrawable(who);
                }

                @Override
                public void superJumpDrawablesToCurrentState() {
                    ForegroundRelativeLayout.super.jumpDrawablesToCurrentState();
                }

                @Override
                public void superDrawableStateChanged() {
                    ForegroundRelativeLayout.super.drawableStateChanged();
                }

                @Override
                public void superDrawableHotspotChanged(float x, float y) {
                    ForegroundRelativeLayout.super.drawableHotspotChanged(x, y);
                }

                @Override
                public void superSetForeground(Drawable foreground) {
                    ForegroundRelativeLayout.super.setForeground(foreground);
                }

                @Override
                public Drawable superGetForeground() {
                    return ForegroundRelativeLayout.super.getForeground();
                }

                @Override
                @SuppressLint("WrongCall")
                public void superOnLayout(boolean changed, int left, int top, int right,
                                          int bottom) {
                    ForegroundRelativeLayout.super.onLayout(changed, left, top, right, bottom);
                }

                @Override
                public void superOnSizeChanged(int w, int h, int oldw, int oldh) {
                    ForegroundRelativeLayout.super.onSizeChanged(w, h, oldw, oldh);
                }

                @Override
                public void superDraw(@NonNull Canvas canvas) {
                    ForegroundRelativeLayout.super.draw(canvas);
                }
            });

    public ForegroundRelativeLayout(Context context) {
        super(context);

        init(getContext(), null, 0, 0);
    }

    public ForegroundRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(getContext(), attrs, 0, 0);
    }

    public ForegroundRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(getContext(), attrs, defStyleAttr, 0);
    }

    public ForegroundRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr,
                                    int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init(getContext(), attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        mForegroundHelper.init(getContext(), attrs, defStyleAttr, defStyleRes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getForegroundGravity() {
        return mForegroundHelper.getForegroundGravity();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setForegroundGravity(int foregroundGravity) {
        mForegroundHelper.setForegroundGravity(foregroundGravity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setVisibility(int visibility) {
        mForegroundHelper.setVisibility(visibility);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean verifyDrawable(Drawable who) {
        return mForegroundHelper.verifyDrawable(who);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void jumpDrawablesToCurrentState() {
        mForegroundHelper.jumpDrawablesToCurrentState();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void drawableStateChanged() {
        mForegroundHelper.drawableStateChanged();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void drawableHotspotChanged(float x, float y) {
        mForegroundHelper.drawableHotspotChanged(x, y);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setForeground(Drawable foreground) {
        mForegroundHelper.setForeground(foreground);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Drawable getForeground() {
        return mForegroundHelper.getForeground();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        mForegroundHelper.onLayout(changed, left, top, right, bottom);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mForegroundHelper.onSizeChanged(w, h, oldw, oldh);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void draw(@NonNull Canvas canvas) {
        mForegroundHelper.draw(canvas);
    }
}
