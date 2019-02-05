/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.shapes.Shape;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

public class PolygonShape extends Shape {

    private int mNumSides;
    private Path mPolygon = new Path();

    public PolygonShape(int numSides) {
        mNumSides = numSides;
    }

    @Override
    protected void onResize(float width, float height) {
        mPolygon.rewind();
        double radianPerSide = 2 * Math.PI / mNumSides;
        float halfWidth = width / 2;
        float halfHeight = height / 2;
        mPolygon.moveTo(halfWidth, 0);
        for (int i = 1; i < mNumSides; ++i) {
            mPolygon.lineTo(halfWidth + (float) Math.sin(i * radianPerSide) * halfWidth,
                    halfHeight - (float) Math.cos(i * radianPerSide) * halfHeight);
        }
        mPolygon.close();
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void getOutline(@NonNull Outline outline) {
        outline.setConvexPath(mPolygon);
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        canvas.drawPath(mPolygon, paint);
    }

    @Override
    public PolygonShape clone() throws CloneNotSupportedException {
        PolygonShape polygonShape = (PolygonShape) super.clone();
        polygonShape.mPolygon = new Path(mPolygon);
        return polygonShape;
    }
}
