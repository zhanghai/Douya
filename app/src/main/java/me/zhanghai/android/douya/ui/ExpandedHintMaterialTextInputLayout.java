/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.textfield.ExpandedHintTextInputLayout;

import me.zhanghai.android.materialedittext.MaterialEditText;
import me.zhanghai.android.materialedittext.MaterialEditTextBackgroundDrawable;

/**
 * @see me.zhanghai.android.materialedittext.MaterialTextInputLayout
 */
public class ExpandedHintMaterialTextInputLayout extends ExpandedHintTextInputLayout {

    private MaterialEditTextBackgroundDrawable mEditTextBackground;

    public ExpandedHintMaterialTextInputLayout(Context context) {
        super(context);
    }

    public ExpandedHintMaterialTextInputLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExpandedHintMaterialTextInputLayout(Context context, AttributeSet attrs,
                                               int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);

        if (child instanceof MaterialEditText) {
            // Just throw a ClassCastException if the background of MaterialEditText is not the one
            // automatically set.
            mEditTextBackground = (MaterialEditTextBackgroundDrawable) child.getBackground();
        }
    }

    @Override
    public void setError(CharSequence error) {
        super.setError(error);

        if (mEditTextBackground != null) {
            mEditTextBackground.setError(!TextUtils.isEmpty(error));
        }
    }
}
