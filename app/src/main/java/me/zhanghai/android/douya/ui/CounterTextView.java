/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.content.Context;
import androidx.appcompat.widget.AppCompatTextView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.util.ViewUtils;

public class CounterTextView extends AppCompatTextView {

    private EditText mEditText;
    private int mMaxLength;

    public CounterTextView(Context context) {
        super(context);
    }

    public CounterTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CounterTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setEditText(EditText editText, int maxLength) {
        mEditText = editText;
        mMaxLength = maxLength;
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                updateText();
            }
        });
        updateText();
    }

    private void updateText() {
        int length = mEditText.length();
        boolean visible = length > mMaxLength / 2;
        ViewUtils.fadeToVisibility(this, visible, false);
        if (visible) {
            setText(getContext().getString(R.string.counter_format, length, mMaxLength));
            int textColorAttrRes = length <= mMaxLength ? android.R.attr.textColorSecondary
                    : R.attr.colorError;
            setTextColor(ViewUtils.getColorStateListFromAttrRes(textColorAttrRes, getContext()));
        }
    }
}
