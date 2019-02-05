/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Draws divider before items except the first.
 *
 * @see androidx.recyclerview.widget.DividerItemDecoration
 */
public class DividerItemDecoration extends RecyclerView.ItemDecoration {

    public static final int HORIZONTAL = LinearLayout.HORIZONTAL;
    public static final int VERTICAL = LinearLayout.VERTICAL;

    /**
     * Current orientation. Either {@link #HORIZONTAL} or {@link #VERTICAL}.
     */
    private int mOrientation;

    private Drawable mDivider;

    private final Rect mBounds = new Rect();

    public DividerItemDecoration(int orientation, Drawable divider) {
        setOrientation(orientation);
        setDivider(divider);
    }

    public DividerItemDecoration(int orientation, int dividerRes, Context context) {
        this(orientation, AppCompatResources.getDrawable(context, dividerRes));
    }

    public void setOrientation(int orientation) {
        if (orientation != HORIZONTAL && orientation != VERTICAL) {
            throw new IllegalArgumentException(
                    "Invalid orientation. It should be either HORIZONTAL or VERTICAL");
        }
        mOrientation = orientation;
    }

    public void setDivider(Drawable drawable) {
        mDivider = drawable;
    }

    @Override
    public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        if (parent.getLayoutManager() == null || mDivider == null) {
            return;
        }
        if (mOrientation == VERTICAL) {
            drawVertical(canvas, parent);
        } else {
            drawHorizontal(canvas, parent);
        }
    }

    private void drawVertical(Canvas canvas, RecyclerView parent) {
        canvas.save();
        int left;
        int right;
        if (parent.getClipToPadding()) {
            left = parent.getPaddingLeft();
            right = parent.getWidth() - parent.getPaddingRight();
            canvas.clipRect(left, parent.getPaddingTop(), right,
                    parent.getHeight() - parent.getPaddingBottom());
        } else {
            left = 0;
            right = parent.getWidth();
        }
        for (int i = 0, count = parent.getChildCount(); i < count; ++i) {
            View child = parent.getChildAt(i);
            if (parent.getChildAdapterPosition(child) == 0) {
                continue;
            }
            parent.getDecoratedBoundsWithMargins(child, mBounds);
            int top = mBounds.top + Math.round(child.getTranslationY());
            int bottom = top + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(canvas);
        }
        canvas.restore();
    }

    private void drawHorizontal(Canvas canvas, RecyclerView parent) {
        canvas.save();
        int top;
        int bottom;
        if (parent.getClipToPadding()) {
            top = parent.getPaddingTop();
            bottom = parent.getHeight() - parent.getPaddingBottom();
            canvas.clipRect(parent.getPaddingLeft(), top,
                    parent.getWidth() - parent.getPaddingRight(), bottom);
        } else {
            top = 0;
            bottom = parent.getHeight();
        }
        for (int i = 0, count = parent.getChildCount(); i < count; ++i) {
            View child = parent.getChildAt(i);
            if (parent.getChildAdapterPosition(child) == 0) {
                continue;
            }
            parent.getLayoutManager().getDecoratedBoundsWithMargins(child, mBounds);
            int left = mBounds.left + Math.round(child.getTranslationX());
            int right = left + mDivider.getIntrinsicWidth();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(canvas);
        }
        canvas.restore();
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        if (mDivider == null || parent.getChildAdapterPosition(view) == 0) {
            outRect.set(0, 0, 0, 0);
            return;
        }
        if (mOrientation == VERTICAL) {
            outRect.set(0, mDivider.getIntrinsicHeight(), 0, 0);
        } else {
            outRect.set(mDivider.getIntrinsicWidth(), 0, 0, 0);
        }
    }
}
