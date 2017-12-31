/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.style.ReplacementSpan;

public class IconSpan extends ReplacementSpan {

    private Drawable mDrawable;

    public IconSpan(Drawable drawable) {
        mDrawable = drawable;
    }

    @Override
    public int getSize(@NonNull Paint paint, CharSequence text, int start, int end,
                       @Nullable Paint.FontMetricsInt fm) {
        if (fm != null) {
            // Guard against the case when icon is the first character.
            paint.getFontMetricsInt(fm);
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
            // fm.ascent and fm.descent determine top and bottom in draw().
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
        // Center between top and bottom.
        canvas.translate(x, (top + bottom - bounds.top - bounds.bottom) / 2);
        mDrawable.draw(canvas);
        canvas.restore();
    }
}
