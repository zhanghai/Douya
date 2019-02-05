/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.google.android.material.textfield;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

import me.zhanghai.android.douya.R;

@SuppressLint("RestrictedApi")
public class ExpandedHintTextInputLayout extends TextInputLayout {

    private EditText mHasTextEditText;

    public ExpandedHintTextInputLayout(Context context) {
        super(context);

        init();
    }

    public ExpandedHintTextInputLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public ExpandedHintTextInputLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        collapsingTextHelper.setCollapsedTextAppearance(R.style.TextAppearance_AppCompat_Caption);
        mHasTextEditText = new EditText(getContext());
        mHasTextEditText.setText(" ");
    }

    @Override
    void updateLabelState(boolean animate) {
        EditText realEditText = editText;
        editText = mHasTextEditText;
        super.updateLabelState(animate);
        editText = realEditText;
    }
}
