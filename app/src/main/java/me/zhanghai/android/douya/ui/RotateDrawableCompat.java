/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;

import me.zhanghai.android.douya.util.MathUtils;

public class RotateDrawableCompat extends DrawableWrapperCompat {

    private static final int MAX_LEVEL = 10000;

    private RotateState mState;

    public RotateDrawableCompat(Drawable drawable) {
        super(drawable);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {

        Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }

        Rect bounds = drawable.getBounds();
        int width = bounds.right - bounds.left;
        int height = bounds.bottom - bounds.top;
        float pivotX = mState.mIsPivotXRelative ? (width * mState.mPivotX) : mState.mPivotX;
        float pivotY = mState.mIsPivotYRelative ? (height * mState.mPivotY) : mState.mPivotY;

        int saveCount = canvas.save();
        canvas.rotate(mState.mCurrentDegrees, bounds.left + pivotX, bounds.top + pivotY);
        drawable.draw(canvas);
        canvas.restoreToCount(saveCount);
    }

    /**
     * Sets the start angle for rotation.
     *
     * @param fromDegrees starting angle in degrees
     * @see #getFromDegrees()
     */
    public void setFromDegrees(float fromDegrees) {
        if (mState.mFromDegrees == fromDegrees) {
            return;
        }
        mState.mFromDegrees = fromDegrees;
        invalidateSelf();
    }

    /**
     * @return starting angle for rotation in degrees
     * @see #setFromDegrees(float)
     */
    public float getFromDegrees() {
        return mState.mFromDegrees;
    }

    /**
     * Sets the end angle for rotation.
     *
     * @param toDegrees ending angle in degrees
     * @see #getToDegrees()
     */
    public void setToDegrees(float toDegrees) {
        if (mState.mToDegrees == toDegrees) {
            return;
        }
        mState.mToDegrees = toDegrees;
        invalidateSelf();
    }

    /**
     * @return ending angle for rotation in degrees
     * @see #setToDegrees(float)
     */
    public float getToDegrees() {
        return mState.mToDegrees;
    }

    public void setDegrees(float degrees) {
        mState.mFromDegrees = mState.mToDegrees = mState.mCurrentDegrees = degrees;
    }

    /**
     * Sets the X position around which the drawable is rotated.
     * <p>
     * If the X pivot is relative (as specified by
     * {@link #setPivotXRelative(boolean)}), then the position represents a
     * fraction of the drawable width. Otherwise, the position represents an
     * absolute value in pixels.
     *
     * @param pivotX X position around which to rotate
     * @see #setPivotXRelative(boolean)
     */
    public void setPivotX(float pivotX) {
        if (mState.mPivotX == pivotX) {
            return;
        }
        mState.mPivotX = pivotX;
        invalidateSelf();
    }

    /**
     * @return X position around which to rotate
     * @see #setPivotX(float)
     */
    public float getPivotX() {
        return mState.mPivotX;
    }

    /**
     * Sets whether the X pivot value represents a fraction of the drawable
     * width or an absolute value in pixels.
     *
     * @param relative true if the X pivot represents a fraction of the drawable
     *            width, or false if it represents an absolute value in pixels
     * @see #isPivotXRelative()
     */
    public void setPivotXRelative(boolean relative) {
        if (mState.mIsPivotXRelative == relative) {
            return;
        }
        mState.mIsPivotXRelative = relative;
        invalidateSelf();
    }

    /**
     * @return true if the X pivot represents a fraction of the drawable width,
     *         or false if it represents an absolute value in pixels
     * @see #setPivotXRelative(boolean)
     */
    public boolean isPivotXRelative() {
        return mState.mIsPivotXRelative;
    }

    /**
     * Sets the Y position around which the drawable is rotated.
     * <p>
     * If the Y pivot is relative (as specified by
     * {@link #setPivotYRelative(boolean)}), then the position represents a
     * fraction of the drawable height. Otherwise, the position represents an
     * absolute value in pixels.
     *
     * @param pivotY Y position around which to rotate
     * @see #getPivotY()
     */
    public void setPivotY(float pivotY) {
        if (mState.mPivotY == pivotY) {
            return;
        }
        mState.mPivotY = pivotY;
        invalidateSelf();
    }

    /**
     * @return Y position around which to rotate
     * @see #setPivotY(float)
     */
    public float getPivotY() {
        return mState.mPivotY;
    }

    /**
     * Sets whether the Y pivot value represents a fraction of the drawable
     * height or an absolute value in pixels.
     *
     * @param relative True if the Y pivot represents a fraction of the drawable
     *            height, or false if it represents an absolute value in pixels
     * @see #isPivotYRelative()
     */
    public void setPivotYRelative(boolean relative) {
        if (mState.mIsPivotYRelative == relative) {
            return;
        }
        mState.mIsPivotYRelative = relative;
        invalidateSelf();
    }

    /**
     * @return true if the Y pivot represents a fraction of the drawable height,
     *         or false if it represents an absolute value in pixels
     * @see #setPivotYRelative(boolean)
     */
    public boolean isPivotYRelative() {
        return mState.mIsPivotYRelative;
    }

    @Override
    protected boolean onLevelChange(int level) {
        super.onLevelChange(level);

        float value = level / (float) MAX_LEVEL;
        mState.mCurrentDegrees = MathUtils.lerp(mState.mFromDegrees, mState.mToDegrees, value);
        invalidateSelf();
        return true;
    }

    @Override
    protected RotateState mutateConstantState() {
        mState = new RotateState(mState, null);
        return mState;
    }

    private static final class RotateState extends DrawableWrapperState {

        boolean mIsPivotXRelative = true;
        float mPivotX = 0.5f;
        boolean mIsPivotYRelative = true;
        float mPivotY = 0.5f;
        float mFromDegrees = 0.0f;
        float mToDegrees = 360.0f;
        float mCurrentDegrees = 0.0f;

        RotateState(RotateState orig, Resources res) {
            super(orig, res);

            if (orig != null) {
                mIsPivotXRelative = orig.mIsPivotXRelative;
                mPivotX = orig.mPivotX;
                mIsPivotYRelative = orig.mIsPivotYRelative;
                mPivotY = orig.mPivotY;
                mFromDegrees = orig.mFromDegrees;
                mToDegrees = orig.mToDegrees;
                mCurrentDegrees = orig.mCurrentDegrees;
            }
        }

        @NonNull
        @Override
        public Drawable newDrawable(Resources res) {
            return new RotateDrawableCompat(this, res);
        }
    }

    private RotateDrawableCompat(RotateState state, Resources res) {
        super(state, res);

        mState = state;
    }
}
