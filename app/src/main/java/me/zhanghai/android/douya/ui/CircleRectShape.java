/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.shapes.Shape;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

public class CircleRectShape extends Shape {

    private RectF mTempRectForCanvasAddRoundRect;
    private Path mPath = new Path();

    public CircleRectShape() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            mTempRectForCanvasAddRoundRect = new RectF();
        }
    }

    @Override
    protected void onResize(float width, float height) {
        mPath.rewind();
        float radius = Math.min(width, height) / 2;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mPath.addRoundRect(0, 0, width, height, radius, radius, Path.Direction.CW);
        } else {
            mTempRectForCanvasAddRoundRect.set(0, 0, width, height);
            mPath.addRoundRect(mTempRectForCanvasAddRoundRect, radius, radius, Path.Direction.CW);
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void getOutline(@NonNull Outline outline) {
        outline.setConvexPath(mPath);
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        canvas.drawPath(mPath, paint);
    }

    @Override
    public CircleRectShape clone() throws CloneNotSupportedException {
        CircleRectShape circleRectShape = (CircleRectShape) super.clone();
        circleRectShape.mPath = new Path(mPath);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            circleRectShape.mTempRectForCanvasAddRoundRect = new RectF();
        }
        return circleRectShape;
    }
}
