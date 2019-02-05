/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import android.util.AttributeSet;

public class GetOnLongClickListenerImageButton extends AppCompatImageButton {

    private OnLongClickListener mOnLongClickListener;

    public GetOnLongClickListenerImageButton(Context context) {
        super(context);
    }

    public GetOnLongClickListenerImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GetOnLongClickListenerImageButton(Context context, AttributeSet attrs,
                                             int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public OnLongClickListener getOnLongClickListener() {
        return mOnLongClickListener;
    }

    @Override
    public void setOnLongClickListener(@Nullable OnLongClickListener listener) {
        super.setOnLongClickListener(listener);

        mOnLongClickListener = listener;
    }
}
