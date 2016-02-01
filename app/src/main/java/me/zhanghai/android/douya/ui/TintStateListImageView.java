/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

import me.zhanghai.android.douya.R;

/**
 * Use app:tint instead of android:tint for a ColorStateList.
 */
public class TintStateListImageView extends ImageView {

    private ColorStateList mTintList;

    public TintStateListImageView(Context context) {
        super(context);

        init(context, null, 0, 0);
    }

    public TintStateListImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs, 0, 0);
    }

    public TintStateListImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TintStateListImageView(Context context, AttributeSet attrs, int defStyleAttr,
                                  int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TintStateListImageView,
                defStyleAttr, defStyleRes);
        mTintList = a.getColorStateList(R.styleable.TintStateListImageView_tint);
        updateTintColorFilter();
        a.recycle();
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();

        updateTintColorFilter();
    }

    public void setColorFilter(ColorStateList tint) {
        mTintList = tint;
        updateTintColorFilter();
    }

    private void updateTintColorFilter() {
        if (mTintList != null) {
            int color = mTintList.getColorForState(getDrawableState(), mTintList.getDefaultColor());
            setColorFilter(color);
        }
    }
}
