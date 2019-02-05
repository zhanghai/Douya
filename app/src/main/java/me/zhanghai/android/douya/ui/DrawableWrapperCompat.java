/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Outline;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.graphics.drawable.DrawableCompat;

/**
 * Drawable container with only one child element.
 */
public abstract class DrawableWrapperCompat extends Drawable implements Drawable.Callback {

    private DrawableWrapperState mState;
    private Drawable mDrawable;
    private boolean mMutated;

    protected DrawableWrapperCompat(DrawableWrapperState state, Resources res) {
        mState = state;
        updateLocalState(res);
    }

    /**
     * Creates a new wrapper around the specified drawable.
     *
     * @param dr the drawable to wrap
     */
    public DrawableWrapperCompat(@Nullable Drawable dr) {
        mState = mutateConstantState();
        setDrawable(dr);
    }

    /**
     * Initializes local dynamic properties from state. This should be called
     * after significant state changes, e.g. from the One True Constructor and
     * after inflating or applying a theme.
     */
    private void updateLocalState(Resources res) {
        if (mState != null && mState.mDrawableState != null) {
            final Drawable dr = mState.mDrawableState.newDrawable(res);
            setDrawable(dr);
        }
    }

    /**
     * Sets the wrapped drawable.
     *
     * @param dr the wrapped drawable
     */
    public void setDrawable(@Nullable Drawable dr) {

        if (mDrawable != null) {
            mDrawable.setCallback(null);
        }

        mDrawable = dr;

        if (dr != null) {
            dr.setCallback(this);

            // Only call setters for data that's stored in the base Drawable.
            dr.setVisible(isVisible(), true);
            dr.setState(getState());
            dr.setLevel(getLevel());
            dr.setBounds(getBounds());
            DrawableCompat.setLayoutDirection(dr, DrawableCompat.getLayoutDirection(this));

            if (mState != null) {
                mState.mDrawableState = dr.getConstantState();
            }
        }

        invalidateSelf();
    }

    /**
     * @return the wrapped drawable
     */
    @Nullable
    public Drawable getDrawable() {
        return mDrawable;
    }

    @Override
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public void applyTheme(@NonNull Resources.Theme t) {
        super.applyTheme(t);

        if (mDrawable != null && mDrawable.canApplyTheme()) {
            mDrawable.applyTheme(t);
        }
    }

    @Override
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public boolean canApplyTheme() {
        return (mState != null && mState.canApplyTheme()) || super.canApplyTheme();
    }

    @Override
    public void invalidateDrawable(@NonNull Drawable who) {
        final Callback callback = getCallback();
        if (callback != null) {
            callback.invalidateDrawable(this);
        }
    }

    @Override
    public void scheduleDrawable(@NonNull Drawable who, @NonNull Runnable what, long when) {
        final Callback callback = getCallback();
        if (callback != null) {
            callback.scheduleDrawable(this, what, when);
        }
    }

    @Override
    public void unscheduleDrawable(@NonNull Drawable who, @NonNull Runnable what) {
        final Callback callback = getCallback();
        if (callback != null) {
            callback.unscheduleDrawable(this, what);
        }
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        if (mDrawable != null) {
            mDrawable.draw(canvas);
        }
    }

    @Override
    public int getChangingConfigurations() {
        return super.getChangingConfigurations()
                | (mState != null ? mState.getChangingConfigurations() : 0)
                | mDrawable.getChangingConfigurations();
    }

    @Override
    public boolean getPadding(@NonNull Rect padding) {
        return mDrawable != null && mDrawable.getPadding(padding);
    }

    @Override
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public void setHotspot(float x, float y) {
        if (mDrawable != null) {
            mDrawable.setHotspot(x, y);
        }
    }

    @Override
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public void setHotspotBounds(int left, int top, int right, int bottom) {
        if (mDrawable != null) {
            mDrawable.setHotspotBounds(left, top, right, bottom);
        }
    }

    @Override
    @RequiresApi(Build.VERSION_CODES.M)
    public void getHotspotBounds(@NonNull Rect outRect) {
        if (mDrawable != null) {
            mDrawable.getHotspotBounds(outRect);
        } else {
            outRect.set(getBounds());
        }
    }

    @Override
    public boolean setVisible(boolean visible, boolean restart) {
        final boolean superChanged = super.setVisible(visible, restart);
        final boolean changed = mDrawable != null && mDrawable.setVisible(visible, restart);
        return superChanged || changed;
    }

    @Override
    public void setAlpha(int alpha) {
        if (mDrawable != null) {
            mDrawable.setAlpha(alpha);
        }
    }

    @Override
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    public int getAlpha() {
        return mDrawable != null ? mDrawable.getAlpha() : 255;
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        if (mDrawable != null) {
            mDrawable.setColorFilter(colorFilter);
        }
    }

    @Override
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public void setTintList(@Nullable ColorStateList tint) {
        if (mDrawable != null) {
            mDrawable.setTintList(tint);
        }
    }

    @Override
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public void setTintMode(@Nullable PorterDuff.Mode tintMode) {
        if (mDrawable != null) {
            mDrawable.setTintMode(tintMode);
        }
    }

    @Override
    @RequiresApi(Build.VERSION_CODES.M)
    public boolean onLayoutDirectionChanged(int layoutDirection) {
        return mDrawable != null && mDrawable.setLayoutDirection(layoutDirection);
    }

    @Override
    public int getOpacity() {
        return mDrawable != null ? mDrawable.getOpacity() : PixelFormat.TRANSPARENT;
    }

    @Override
    public boolean isStateful() {
        return mDrawable != null && mDrawable.isStateful();
    }

    @Override
    protected boolean onStateChange(int[] state) {
        if (mDrawable != null && mDrawable.isStateful()) {
            final boolean changed = mDrawable.setState(state);
            if (changed) {
                onBoundsChange(getBounds());
            }
            return changed;
        }
        return false;
    }

    @Override
    protected boolean onLevelChange(int level) {
        return mDrawable != null && mDrawable.setLevel(level);
    }

    @Override
    protected void onBoundsChange(@NonNull Rect bounds) {
        if (mDrawable != null) {
            mDrawable.setBounds(bounds);
        }
    }

    @Override
    public int getIntrinsicWidth() {
        return mDrawable != null ? mDrawable.getIntrinsicWidth() : -1;
    }

    @Override
    public int getIntrinsicHeight() {
        return mDrawable != null ? mDrawable.getIntrinsicHeight() : -1;
    }

    @Override
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public void getOutline(@NonNull Outline outline) {
        if (mDrawable != null) {
            mDrawable.getOutline(outline);
        } else {
            super.getOutline(outline);
        }
    }

    @Override
    @Nullable
    public ConstantState getConstantState() {
        if (mState != null && mState.canConstantState()) {
            mState.mChangingConfigurations = getChangingConfigurations();
            return mState;
        }
        return null;
    }

    @Override
    @NonNull
    public Drawable mutate() {
        if (!mMutated && super.mutate() == this) {
            mState = mutateConstantState();
            if (mDrawable != null) {
                mDrawable.mutate();
            }
            if (mState != null) {
                mState.mDrawableState = mDrawable != null ? mDrawable.getConstantState() : null;
            }
            mMutated = true;
        }
        return this;
    }

    /**
     * Mutates the constant state and returns the new state. Responsible for
     * updating any local copy.
     * <p>
     * This method should never call the super implementation; it should always
     * mutate and return its own constant state.
     *
     * @return the new state
     */
    protected DrawableWrapperState mutateConstantState() {
        return mState;
    }

    protected abstract static class DrawableWrapperState extends ConstantState {

        private int[] mThemeAttrs;

        int mChangingConfigurations;

        ConstantState mDrawableState;

        DrawableWrapperState(@Nullable DrawableWrapperState orig, @Nullable Resources res) {
            if (orig != null) {
                mThemeAttrs = orig.mThemeAttrs;
                mChangingConfigurations = orig.mChangingConfigurations;
                mDrawableState = orig.mDrawableState;
            }
        }

        @Override
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        public boolean canApplyTheme() {
            return mThemeAttrs != null
                    || (mDrawableState != null && mDrawableState.canApplyTheme())
                    || super.canApplyTheme();
        }

        @NonNull
        @Override
        public Drawable newDrawable() {
            return newDrawable(null);
        }

        @NonNull
        @Override
        public abstract Drawable newDrawable(@Nullable Resources res);

        @Override
        public int getChangingConfigurations() {
            return mChangingConfigurations
                    | (mDrawableState != null ? mDrawableState.getChangingConfigurations() : 0);
        }

        public boolean canConstantState() {
            return mDrawableState != null;
        }
    }
}
