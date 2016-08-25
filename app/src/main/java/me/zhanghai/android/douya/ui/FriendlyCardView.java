/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.TintTypedArray;
import android.util.AttributeSet;

import me.zhanghai.android.douya.R;

/**
 * A friendly card view that has consistent padding across API levels.
 */
public class FriendlyCardView extends CardView {

    public FriendlyCardView(Context context) {
        super(context);

        init(null, 0);
    }

    public FriendlyCardView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(attrs, 0);
    }

    public FriendlyCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyleAttr) {

        TintTypedArray a = TintTypedArray.obtainStyledAttributes(getContext(), attrs,
                R.styleable.CardView, defStyleAttr, R.style.CardView_Light);
        setMaxCardElevation(a.getDimension(R.styleable.CardView_cardMaxElevation,
                getCardElevation()));
        a.recycle();

        setUseCompatPadding(true);
        setPreventCornerOverlap(false);

        // User should never click through a card.
        setClickable(true);
    }
}
