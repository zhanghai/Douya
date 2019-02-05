/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.TintTypedArray;
import android.util.AttributeSet;

import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.util.ViewUtils;

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

    @SuppressLint("RestrictedApi")
    private void init(AttributeSet attrs, int defStyleAttr) {

        TintTypedArray a = TintTypedArray.obtainStyledAttributes(getContext(), attrs,
                R.styleable.CardView, defStyleAttr, R.style.CardView);
        setMaxCardElevation(a.getDimension(R.styleable.CardView_cardMaxElevation,
                getCardElevation()));
        a.recycle();

        setUseCompatPadding(true);
        setPreventCornerOverlap(false);

        // User should never click through a card.
        setClickable(true);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        // Fix off-by-one-pixel negative margin.
        Resources resources = getResources();
        int cardShadowHorizontalMargin = resources.getDimensionPixelOffset(
                R.dimen.card_shadow_horizontal_margin);
        if (ViewUtils.getMarginLeft(this) == cardShadowHorizontalMargin) {
            ViewUtils.setMarginLeft(this, getContentPaddingLeft() - getPaddingLeft());
        }
        if (ViewUtils.getMarginRight(this) == cardShadowHorizontalMargin) {
            ViewUtils.setMarginRight(this, getContentPaddingRight() - getPaddingRight());
        }
        int cardShadowVerticalMargin = resources.getDimensionPixelOffset(
                R.dimen.card_shadow_vertical_margin);
        if (ViewUtils.getMarginTop(this) == cardShadowVerticalMargin) {
            ViewUtils.setMarginTop(this, getContentPaddingTop() - getPaddingTop());
        }
        if (ViewUtils.getMarginBottom(this) == cardShadowVerticalMargin) {
            ViewUtils.setMarginBottom(this, getContentPaddingBottom() - getPaddingBottom());
        }
    }
}
