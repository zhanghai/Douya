/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.style.ReplacementSpan;

public class IconSpan extends ReplacementSpan {

    private Drawable mDrawable;

    private int topFix;
    private int bottomFix;

    public IconSpan(Drawable drawable) {
        mDrawable = drawable;
    }

    @Override
    public int getSize(@NonNull Paint paint, CharSequence text, int start, int end,
                       @Nullable Paint.FontMetricsInt fm) {
        if (fm != null) {
            // Important in the case when icon is the first character.
            int oldTop = fm.top;
            int oldBottom = fm.bottom;
            paint.getFontMetricsInt(fm);
            topFix = fm.top - oldTop;
            bottomFix = fm.bottom - oldBottom;
            int height = fm.descent - fm.ascent;
            int width;
            if (mDrawable.getIntrinsicHeight() > 0) {
                width = Math.round((float) height / mDrawable.getIntrinsicHeight()
                        * mDrawable.getIntrinsicWidth());
            } else {
                //noinspection SuspiciousNameCombination
                width = height;
            }
            mDrawable.setBounds(0, 0, width, height);
            // fm.ascent and fm.descent can affect top and bottom in draw().
            fm.ascent = fm.top;
            fm.descent = fm.bottom;
        }
        return mDrawable.getBounds().right;
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x,
                     int top, int y, int bottom, @NonNull Paint paint) {
        canvas.save();
        Rect bounds = mDrawable.getBounds();
        top += topFix;
        bottom += bottomFix;
        // Center between top and bottom.
        canvas.translate(x, (top + bottom - bounds.top - bounds.bottom) / 2);
        mDrawable.draw(canvas);
        canvas.restore();
    }
}
