/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
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
    private CanChildScrollUpCallback mCanChildScrollUpCallback;

    public FriendlySwipeRefreshLayout(Context context) {
        super(context);

        init(getContext(), null);
    }

    public FriendlySwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(getContext(), attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.FriendlySwipeRefreshLayout, 0, 0);
        int progressOffset = a.getDimensionPixelOffset(
                R.styleable.FriendlySwipeRefreshLayout_progressOffset, 0);
        int progressDistanceOffset = a.getDimensionPixelOffset(
                R.styleable.FriendlySwipeRefreshLayout_progressDistanceOffset, 0);
        a.recycle();

        if (progressOffset != 0 || progressDistanceOffset != 0) {
            // TODO: Intercept calls to setSize().
            int circleDiameterDp = mSize == DEFAULT ? CIRCLE_DIAMETER_DP : CIRCLE_DIAMETER_LARGE_DP;
            circleDiameterDp += CIRCLE_SHADOW_DP;
            int progressStart = progressOffset - ViewUtils.dpToPxInt(circleDiameterDp, context);
            int progressEnd = progressStart + ViewUtils.dpToPxInt(DEFAULT_CIRCLE_DISTANCE_DP
                    , context) + progressDistanceOffset;
            setProgressViewOffset(false, progressStart, progressEnd);
        }

        setColorSchemeColors(ViewUtils.getColorFromAttrRes(R.attr.colorPrimary, Color.BLACK,
                context));
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
