/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package android.support.design.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

import me.zhanghai.android.douya.R;

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
        mCollapsingTextHelper.setCollapsedTextAppearance(R.style.TextAppearance_AppCompat_Caption);
        mHasTextEditText = new EditText(getContext());
        mHasTextEditText.setText(" ");
    }

    @Override
    void updateLabelState(boolean animate, boolean force) {
        EditText editText = mEditText;
        mEditText = mHasTextEditText;
        super.updateLabelState(animate, force);
        mEditText = editText;
    }
}
