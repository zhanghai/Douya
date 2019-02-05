/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.shapes.Shape;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

public class StarShape extends Shape {

    private int mNumVertices;
    private float mInnerRadiusRatio;
    private Path mStar = new Path();

    public StarShape(int numVertices, float innerRadiusRatio) {
        mNumVertices = numVertices;
        mInnerRadiusRatio = innerRadiusRatio;
    }

    @Override
    protected void onResize(float width, float height) {
        mStar.rewind();
        double radianPerPoint = Math.PI / mNumVertices;
        float halfWidth = width / 2;
        float halfHeight = height / 2;
        float halfInnerWidth = mInnerRadiusRatio * halfWidth;
        float halfInnerHeight = mInnerRadiusRatio * halfHeight;
        for (int i = 0; i < mNumVertices; ++i) {
            if (i == 0) {
                mStar.moveTo(halfWidth, 0);
            } else {
                mStar.lineTo(halfWidth + (float) Math.sin(2 * i * radianPerPoint) * halfWidth,
                        halfHeight - (float) Math.cos(2 * i * radianPerPoint) * halfHeight);
            }
            mStar.lineTo(
                    halfWidth + (float) Math.sin((2 * i + 1) * radianPerPoint) * halfInnerWidth,
                    halfHeight - (float) Math.cos((2 * i + 1) * radianPerPoint) * halfInnerHeight);
        }
        mStar.close();
    }

    // Star is not a convex shape.
    //@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    //@Override
    //public void getOutline(@NonNull Outline outline) {
    //    outline.setConvexPath(mStar);
    //}

    @Override
    public void draw(Canvas canvas, Paint paint) {
        canvas.drawPath(mStar, paint);
    }

    @Override
    public StarShape clone() throws CloneNotSupportedException {
        StarShape polygonShape = (StarShape) super.clone();
        polygonShape.mStar = new Path(mStar);
        return polygonShape;
    }
}
