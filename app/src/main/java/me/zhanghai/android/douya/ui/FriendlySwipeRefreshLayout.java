/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.TintTypedArray;
import android.util.AttributeSet;

import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.util.ViewUtils;

/**
 * Offers <code>app:progressOffset</code>, <code>app:progressDistanceOffset</code>, and defaults
 * progress color to <code>?colorPrimary</code>.
 */
public class FriendlySwipeRefreshLayout extends SwipeRefreshLayout {

    private static final int CIRCLE_DIAMETER_DP = 40;
    private static final int CIRCLE_DIAMETER_LARGE_DP = 56;
    private static final int CIRCLE_SHADOW_DP = 7;
    private static final int DEFAULT_CIRCLE_DISTANCE_DP = 64;

    private int mSize = DEFAULT;
    private int mCircleDiameter;
    private int mDefaultCircleDistance;

    private CanChildScrollUpCallback mCanChildScrollUpCallback;

    public FriendlySwipeRefreshLayout(Context context) {
        super(context);

        init(null);
    }

    public FriendlySwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(attrs);
    }

    private void init(AttributeSet attrs) {

        updateCircleDiameter();
        Context context = getContext();
        mDefaultCircleDistance = ViewUtils.dpToPxInt(DEFAULT_CIRCLE_DISTANCE_DP, context);

        TintTypedArray a = TintTypedArray.obtainStyledAttributes(getContext(), attrs,
                R.styleable.FriendlySwipeRefreshLayout, 0, 0);
        int progressOffset = a.getDimensionPixelOffset(
                R.styleable.FriendlySwipeRefreshLayout_progressOffset, 0);
        int progressDistanceOffset = a.getDimensionPixelOffset(
                R.styleable.FriendlySwipeRefreshLayout_progressDistanceOffset, 0);
        a.recycle();

        if (progressOffset != 0 || progressDistanceOffset != 0) {
            setProgressViewOffset(progressOffset, progressDistanceOffset);
        }

        setColorSchemeColors(ViewUtils.getColorFromAttrRes(R.attr.colorPrimary, Color.BLACK,
                context));
    }

    @Override
    public void setSize(int size) {
        super.setSize(size);

        if (size == LARGE || size == DEFAULT) {
            mSize = size;
            updateCircleDiameter();
        }
    }

    private void updateCircleDiameter() {
        int circleDiameterDp = mSize == DEFAULT ? CIRCLE_DIAMETER_DP : CIRCLE_DIAMETER_LARGE_DP;
        circleDiameterDp += CIRCLE_SHADOW_DP;
        mCircleDiameter = ViewUtils.dpToPxInt(circleDiameterDp, getContext());
    }

    public void setProgressViewOffset(int offset, int distanceOffset) {
        int progressStart = offset - mCircleDiameter;
        int progressEnd = progressStart + mDefaultCircleDistance + distanceOffset;
        setProgressViewOffset(false, progressStart, progressEnd);
    }

    public void setProgressViewOffset(int offset) {
        setProgressViewOffset(offset, 0);
    }

    public CanChildScrollUpCallback getCanChildScrollUpCallback() {
        return mCanChildScrollUpCallback;
    }

    public void setCanChildScrollUpCallback(CanChildScrollUpCallback canChildScrollUpCallback) {
        mCanChildScrollUpCallback = canChildScrollUpCallback;
    }

    @Override
    public boolean canChildScrollUp() {
        if (mCanChildScrollUpCallback != null) {
            return mCanChildScrollUpCallback.canChildScrollUp();
        }
        return super.canChildScrollUp();
    }

    public interface CanChildScrollUpCallback {
        boolean canChildScrollUp();
    }
}
