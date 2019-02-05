/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.AttributeSet;

import me.zhanghai.android.douya.util.ViewUtils;

/**
 * TextView that automatically sets its visibility to View.GONE when empty.
 */
public class AutoGoneTextView extends AppCompatTextView {

    public AutoGoneTextView(@NonNull Context context) {
        super(context);
    }

    public AutoGoneTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoGoneTextView(@NonNull Context context, @Nullable AttributeSet attrs,
                            int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setText(@Nullable CharSequence text, @NonNull BufferType type) {
        super.setText(text, type);

        ViewUtils.setVisibleOrGone(this, !TextUtils.isEmpty(text));
    }
}
