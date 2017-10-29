/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.item.ui;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.link.UriHandler;
import me.zhanghai.android.douya.network.api.info.frodo.Honor;
import me.zhanghai.android.douya.network.api.info.frodo.Rating;
import me.zhanghai.android.douya.ui.CircleRectShape;
import me.zhanghai.android.douya.ui.PolygonShape;
import me.zhanghai.android.douya.util.ViewUtils;

public class BadgeListLayout extends HorizontalScrollView {

    @BindView(R.id.badge_list)
    ViewGroup mBadgeListLayout;
    @BindView(R.id.top_250_layout)
    ViewGroup mTop250Layout;
    @BindView(R.id.top_250_rank)
    TextView mTop250RankText;
    @BindView(R.id.top_250_badge)
    TextView mTop250Badge;
    @BindView(R.id.rating_layout)
    ViewGroup mRatingLayout;
    @BindView(R.id.rating_badge)
    ViewGroup mRatingBadgeLayout;
    @BindView(R.id.rating_text)
    TextView mRatingText;
    @BindView(R.id.rating_bar)
    RatingBar mRatingBar;
    @BindView(R.id.rating_count)
    TextView mRatingCountText;
    @BindView(R.id.genre_layout)
    ViewGroup mGenreLayout;
    @BindView(R.id.genre_badge)
    ImageView mGenreBadgeImage;
    @BindView(R.id.genre_text)
    TextView mGenreText;
    @BindView(R.id.similar_items_layout)
    ViewGroup mSimilarItemsLayout;

    public BadgeListLayout(Context context) {
        super(context);

        init();
    }

    public BadgeListLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public BadgeListLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public BadgeListLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init();
    }

    private void init() {

        setFillViewport(true);

        ViewUtils.inflateInto(R.layout.item_badge_list_layout, this);
        ButterKnife.bind(this);

        Context context = getContext();
        ViewCompat.setBackground(mTop250Badge, new ShapeDrawable(new CircleRectShape()));
        Drawable ratingBadgeDrawable = new RatingBadgeDrawable(context);
        ratingBadgeDrawable = DrawableCompat.wrap(ratingBadgeDrawable);
        int colorAccent = ViewUtils.getColorFromAttrRes(R.attr.colorAccent, 0, context);
        DrawableCompat.setTint(ratingBadgeDrawable, colorAccent);
        ViewCompat.setBackground(mRatingBadgeLayout, ratingBadgeDrawable);
        mSimilarItemsLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO
            }
        });
    }

    public void setTop250(Honor honor) {
        boolean isTop250 = honor != null;
        ViewUtils.setVisibleOrGone(mTop250Layout, isTop250);
        if (!isTop250) {
            return;
        }
        final Context context = getContext();
        mTop250RankText.setText(context.getString(R.string.item_top_250_rank_format, honor.rank));
        mTop250Layout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //UriHandler.open(honor.uri, context);
                // HACK
                UriHandler.open("https://movie.douban.com/top250", context);
            }
        });
    }

    public void setRating(Rating rating) {
        float ratingOutOfTen = (float) Math.round(rating.value / rating.max * 10 * 10) / 10;
        String ratingString = getContext().getString(R.string.item_rating_format, ratingOutOfTen);
        mRatingText.setText(ratingString);
        float ratingValue = rating.value / rating.max * 5;
        mRatingBar.setRating(ratingValue);
        String ratingCount = getContext().getString(R.string.item_rating_count_format,
                rating.count);
        mRatingCountText.setText(ratingCount);
        mRatingLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO
            }
        });
    }

    public void setGenre(int genreBadgeResId, CharSequence genre) {
        mGenreBadgeImage.setImageResource(genreBadgeResId);
        mGenreText.setText(genre);
        mGenreLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO
            }
        });
    }

    private static class RatingBadgeDrawable extends LayerDrawable {

        private static final float STROKE_WIDTH_DP = 1.5f;
        private static final float STROKE_FILL_GAP_DP = 2;

        public RatingBadgeDrawable(Context context) {
            super(new Drawable[] {
                    new ShapeDrawable(new PolygonShape(8)),
                    new ShapeDrawable(new PolygonShape(8))
            });

            int strokeInset = ViewUtils.dpToPxOffset(STROKE_WIDTH_DP / 2, context);
            setLayerInset(0, strokeInset, strokeInset, strokeInset, strokeInset);
            Paint strokePaint = ((ShapeDrawable) getDrawable(0)).getPaint();
            strokePaint.setStyle(Paint.Style.STROKE);
            int strokeWidth = ViewUtils.dpToPxSize(STROKE_WIDTH_DP, context);
            strokePaint.setStrokeWidth(strokeWidth);

            int fillInset = ViewUtils.dpToPxOffset(STROKE_WIDTH_DP + STROKE_FILL_GAP_DP, context);
            setLayerInset(1, fillInset, fillInset, fillInset, fillInset);
        }
    }
}
