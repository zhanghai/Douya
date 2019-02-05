/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.TintTypedArray;
import android.util.AttributeSet;

import me.zhanghai.android.douya.R;

public class TintDrawablesTextView extends AppCompatTextView {

    private boolean mIsDrawablesRelative;
    private TintInfo mDrawablesTintInfo;

    public TintDrawablesTextView(Context context) {
        super(context);

        init(null, 0, 0);
    }

    public TintDrawablesTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(attrs, 0, 0);
    }

    public TintDrawablesTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(attrs, defStyleAttr, 0);
    }

    @SuppressLint("RestrictedApi")
    private void init(AttributeSet attrs, int defStyleAttr, int defStyleRes) {

        Context context = getContext();
        TintTypedArray a = TintTypedArray.obtainStyledAttributes(context, attrs,
                R.styleable.TintDrawablesTextView, defStyleAttr, defStyleRes);
        ColorStateList drawableTint = a.getColorStateList(
                R.styleable.TintDrawablesTextView_drawableTint);
        PorterDuff.Mode drawableTintMode =
                me.zhanghai.android.materialprogressbar.internal.DrawableCompat.parseTintMode(
                        a.getInt(R.styleable.TintDrawablesTextView_drawableTintMode, -1), null);
        a.recycle();

        if (hasFrameworkDrawableTint()) {
            if (drawableTint != null) {
                setCompoundDrawableTintList(drawableTint);
            }
            if (drawableTintMode != null) {
                setCompoundDrawableTintMode(drawableTintMode);
            }
            return;
        }

        mDrawablesTintInfo = new TintInfo();
        if (drawableTint != null) {
            mDrawablesTintInfo.mTintList = drawableTint;
            mDrawablesTintInfo.mHasTint = true;
        }
        if (drawableTintMode != null) {
            mDrawablesTintInfo.mTintMode = drawableTintMode;
            mDrawablesTintInfo.mHasTintMode = true;
        }

        // Always try to apply because we have done the quick return in it and
        // TextView.setRelativeDrawablesIfNeeded() is private.
        applyCompoundDrawableTint();
    }

    @Override
    public void setCompoundDrawables(@Nullable Drawable left, @Nullable Drawable top,
                                     @Nullable Drawable right, @Nullable Drawable bottom) {

        if (hasFrameworkDrawableTint()) {
            super.setCompoundDrawables(left, top, right, bottom);
            return;
        }

        mIsDrawablesRelative = false;

        if (mDrawablesTintInfo == null) {
            // Super class initialization, skipping tint.
            super.setCompoundDrawables(left, top, right, bottom);
            return;
        }

        super.setCompoundDrawables(applyTintForDrawable(left), applyTintForDrawable(top),
                applyTintForDrawable(right), applyTintForDrawable(bottom));
    }

    @Override
    public void setCompoundDrawablesRelative(@Nullable Drawable start, @Nullable Drawable top,
                                             @Nullable Drawable end, @Nullable Drawable bottom) {

        if (hasFrameworkDrawableTint()) {
            super.setCompoundDrawablesRelative(start, top, end, bottom);
            return;
        }

        mIsDrawablesRelative = true;
        super.setCompoundDrawablesRelative(applyTintForDrawable(start), applyTintForDrawable(top),
                applyTintForDrawable(end), applyTintForDrawable(bottom));
    }

    @Override
    public void setCompoundDrawableTintList(@Nullable ColorStateList tint) {

        if (hasFrameworkDrawableTint()) {
            super.setCompoundDrawableTintList(tint);
            return;
        }

        mDrawablesTintInfo.mTintList = tint;
        mDrawablesTintInfo.mHasTint = true;

        applyCompoundDrawableTint();
    }

    @Override
    public void setCompoundDrawableTintMode(@Nullable PorterDuff.Mode tintMode) {

        if (hasFrameworkDrawableTint()) {
            super.setCompoundDrawableTintMode(tintMode);
            return;
        }

        mDrawablesTintInfo.mTintMode = tintMode;
        mDrawablesTintInfo.mHasTintMode = true;

        applyCompoundDrawableTint();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void applyCompoundDrawableTint() {

        if (!(mDrawablesTintInfo.mHasTint || mDrawablesTintInfo.mHasTintMode)) {
            return;
        }

        if (mIsDrawablesRelative) {
            Drawable[] drawables = getCompoundDrawablesRelative();
            setCompoundDrawablesRelative(drawables[0], drawables[1], drawables[2], drawables[3]);
        } else {
            Drawable[] drawables = getCompoundDrawables();
            setCompoundDrawables(drawables[0], drawables[1], drawables[2], drawables[3]);
        }
    }

    private Drawable applyTintForDrawable(Drawable drawable) {

        if (drawable == null) {
            return null;
        }

        if (mDrawablesTintInfo.mHasTint || mDrawablesTintInfo.mHasTintMode) {

            drawable = DrawableCompat.wrap(drawable);
            drawable.mutate();
            if (mDrawablesTintInfo.mHasTint) {
                DrawableCompat.setTintList(drawable, mDrawablesTintInfo.mTintList);
            }
            if (mDrawablesTintInfo.mHasTintMode) {
                DrawableCompat.setTintMode(drawable, mDrawablesTintInfo.mTintMode);
            }

            // The drawable (or one of its children) may not have been
            // stateful before applying the tint, so let's try again.
            if (drawable.isStateful()) {
                drawable.setState(getDrawableState());
            }
        }

        return drawable;
    }

    // Cannot make this a field because it may be needed during super class initialization.
    private boolean hasFrameworkDrawableTint() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    private static class TintInfo {
        public ColorStateList mTintList;
        public PorterDuff.Mode mTintMode;
        public boolean mHasTint;
        public boolean mHasTintMode;
    }
}
