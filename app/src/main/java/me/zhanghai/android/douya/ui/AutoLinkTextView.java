/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import me.zhanghai.android.douya.util.SpanUtils;
import me.zhanghai.android.douya.util.ViewUtils;

public class AutoLinkTextView extends AppCompatTextView {

    public AutoLinkTextView(Context context) {
        super(context);

        init();
    }

    public AutoLinkTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public AutoLinkTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {

        if (getAutoLinkMask() != 0) {
            throw new IllegalStateException("Don't set android:autoLink");
        }

        ViewUtils.setTextViewLinkClickable(this);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(SpanUtils.addLinks(text), BufferType.SPANNABLE);
    }
}
