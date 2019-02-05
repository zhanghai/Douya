/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.ViewCompat;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatButton;
import android.util.AttributeSet;

import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.util.ViewUtils;

public class ColoredBorderButton extends AppCompatButton {

    public ColoredBorderButton(Context context) {
        super(context);

        init();
    }

    public ColoredBorderButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public ColoredBorderButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        Context context = getContext();
        Drawable background = AppCompatResources.getDrawable(context,
                R.drawable.colored_border_button_background);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            background = DrawableCompat.wrap(background);
            DrawableCompat.setTint(background, ViewUtils.getColorFromAttrRes(R.attr.colorAccent, 0,
                    context));
        }
        ViewCompat.setBackground(this, background);
    }
}
