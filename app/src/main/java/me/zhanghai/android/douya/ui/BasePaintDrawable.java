/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public abstract class BasePaintDrawable extends BaseDrawable {

    private Paint mPaint;

    @Override
    protected final void onDraw(Canvas canvas, int width, int height) {

        if (mPaint == null) {
            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setColor(Color.BLACK);
            onPreparePaint(mPaint);
        }
        mPaint.setAlpha(mAlpha);
        mPaint.setColorFilter(getColorFilterForDrawing());

        onDraw(canvas, width, height, mPaint);
    }

    protected abstract void onPreparePaint(Paint paint);

    protected abstract void onDraw(Canvas canvas, int width, int height, Paint paint);
}
