/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.item.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleRating;
import me.zhanghai.android.douya.util.ViewUtils;

public class RatingLayout extends LinearLayout {

    @BindView(R.id.rating_text)
    TextView mRatingText;
    @BindView(R.id.rating_bar)
    RatingBar mRatingBar;
    @BindView(R.id.rating_count)
    TextView mRatingCountText;

    public RatingLayout(Context context) {
        super(context);

        init();
    }

    public RatingLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public RatingLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RatingLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init();
    }

    private void init() {
        ViewUtils.inflateInto(R.layout.item_rating_layout, this);
        ButterKnife.bind(this);
    }

    public void setRating(SimpleRating rating) {
        Context context = getContext();
        mRatingText.setText(rating.getRatingString(context));
        mRatingBar.setRating(rating.getRatingBarRating());
        mRatingCountText.setText(rating.getRatingCountString(context));
    }
}
