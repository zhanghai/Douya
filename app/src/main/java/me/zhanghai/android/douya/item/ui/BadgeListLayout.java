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
import android.graphics.drawable.shapes.Shape;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.ViewCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.link.UriHandler;
import me.zhanghai.android.douya.network.api.info.frodo.CollectableItem;
import me.zhanghai.android.douya.network.api.info.frodo.Honor;
import me.zhanghai.android.douya.network.api.info.frodo.Rating;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleRating;
import me.zhanghai.android.douya.ui.CircleRectShape;
import me.zhanghai.android.douya.ui.PolygonShape;
import me.zhanghai.android.douya.ui.StarShape;
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
    @BindView(R.id.rating_count_icon)
    ImageView mRatingCountIconImage;
    @BindView(R.id.followings_rating_layout)
    ViewGroup mFollowingsRatingLayout;
    @BindView(R.id.followings_rating_badge)
    ViewGroup mFollowingsRatingBadgeLayout;
    @BindView(R.id.followings_rating_text)
    TextView mFollowingsRatingText;
    @BindView(R.id.followings_rating_count_icon)
    ImageView mFollowingsRatingCountIconImage;
    @BindView(R.id.followings_rating_bar)
    RatingBar mFollowingsRatingBar;
    @BindView(R.id.followings_rating_count)
    TextView mFollowingsRatingCountText;
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
        // Scroll bar looks ugly here, and Google Play also hid it.
        setHorizontalScrollBarEnabled(false);

        ViewUtils.inflateInto(R.layout.item_badge_list_layout, this);
        ButterKnife.bind(this);

        ViewCompat.setBackground(mTop250Badge, new ShapeDrawable(new CircleRectShape()));
        ViewCompat.setBackground(mRatingBadgeLayout, new RatingBadgeDrawable(
                mRatingBadgeLayout.getContext()));
        ViewCompat.setBackground(mFollowingsRatingBadgeLayout, new FollowingsRatingBadgeDrawable(
                mFollowingsRatingBadgeLayout.getContext()));
        mSimilarItemsLayout.setOnClickListener(view -> {
            // TODO
        });
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        // HorizontalScrollView's edge effect and scroll bar has an incorrect offset if padding is
        // set.
        getChildAt(0).setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(),
                getPaddingBottom());
        setPadding(0, 0, 0, 0);
    }

    public void setTop250(Honor honor) {
        boolean isTop250 = honor != null;
        ViewUtils.setVisibleOrGone(mTop250Layout, isTop250);
        if (!isTop250) {
            return;
        }
        final Context context = getContext();
        mTop250RankText.setText(context.getString(R.string.item_top_250_rank_format, honor.rank));
        mTop250Layout.setOnClickListener(view -> {
            //UriHandler.open(honor.uri, context);
            // HACK
            // TODO
            UriHandler.open("https://movie.douban.com/top250", context);
        });
    }

    public void setRating(Rating rating, CollectableItem item) {
        if (rating.hasRating()) {
            setRating(rating.rating, mRatingText, mRatingBar, mRatingCountText,
                    mRatingCountIconImage);
        } else {
            setRatingUnavailable(rating.getRatingUnavailableReason(getContext()));
        }
        mRatingLayout.setOnClickListener(view -> {
            // TODO
            UriHandler.open(item.url + "collections", view.getContext());
        });
        boolean hasFollowingsRating = rating.followingsRating != null;
        ViewUtils.setVisibleOrGone(mFollowingsRatingLayout, hasFollowingsRating);
        if (hasFollowingsRating) {
            setRating(rating.followingsRating, mFollowingsRatingText, mFollowingsRatingBar,
                    mFollowingsRatingCountText, mFollowingsRatingCountIconImage);
            mFollowingsRatingLayout.setOnClickListener(view -> {
                // TODO
                UriHandler.open(item.url + "collections?show_followings=on", view.getContext());
            });
        }
    }

    private void setRating(SimpleRating rating, TextView ratingText, RatingBar ratingBar,
                           TextView ratingCountText, ImageView ratingCountIconImage) {
        Context context = getContext();
        ratingText.setText(rating.getRatingString(context));
        float ratingBarRating = rating.getRatingBarRating();
        ratingBar.setNumStars(Math.round(ratingBarRating));
        ratingBar.setRating(ratingBarRating);
        ratingCountText.setText(rating.getRatingCountString(context));
        ViewUtils.setVisibleOrGone(ratingCountIconImage, true);
    }

    private void setRatingUnavailable(String ratingUnavailableReason) {
        mRatingText.setText(R.string.item_rating_unavailable);
        mRatingBar.setNumStars(5);
        mRatingBar.setRating(mRatingBar.getNumStars());
        mRatingCountText.setText(ratingUnavailableReason);
        ViewUtils.setVisibleOrGone(mRatingCountIconImage, false);
    }

    public void setGenre(int genreBadgeResId, String genre, CollectableItem.Type itemType) {
        boolean hasGenre = !TextUtils.isEmpty(genre);
        ViewUtils.setVisibleOrGone(mGenreLayout, hasGenre);
        if (hasGenre) {
            mGenreBadgeImage.setImageResource(genreBadgeResId);
            mGenreText.setText(genre);
            mGenreLayout.setOnClickListener(view -> {
                // TODO
                String url = null;
                switch (itemType) {
                    case MOVIE:
                    case TV:
                        url = "https://movie.douban.com/tag/#/?tags=" + Uri.encode(genre);
                        break;
                    case MUSIC:
                        url = "https://music.douban.com/tag/" + Uri.encode(genre);
                        break;
                }
                if (!TextUtils.isEmpty(url)) {
                    UriHandler.open(url, view.getContext());
                }
            });
        }
    }

    private static abstract class BaseRatingBadgeDrawable extends LayerDrawable {

        private static final float STROKE_WIDTH_DP = 1.5f;
        private static final float FILL_INSET_DP = 3.5f;

        public BaseRatingBadgeDrawable(Shape shape, Context context) {
            super(new Drawable[] {
                    DrawableCompat.wrap(new ShapeDrawable(shape)),
                    DrawableCompat.wrap(new ShapeDrawable(cloneShape(shape)))
            });

            // If the outer accent color stroke is not drawn by a stroke paint, it becomes too
            // narrow.
            Drawable strokeDrawable = getDrawable(0);
            int colorAccent = ViewUtils.getColorFromAttrRes(R.attr.colorAccent, 0, context);
            DrawableCompat.setTint(strokeDrawable, colorAccent);
            // Not using ViewUtils.dpToPxOffset() because it causes truncation.
            int strokeInset = ViewUtils.dpToPxSize(STROKE_WIDTH_DP / 2, context);
            setLayerInset(0, strokeInset, strokeInset, strokeInset, strokeInset);
            Paint strokePaint = ((ShapeDrawable) DrawableCompat.unwrap(strokeDrawable)).getPaint();
            strokePaint.setStyle(Paint.Style.STROKE);
            int strokeWidth = ViewUtils.dpToPxSize(STROKE_WIDTH_DP, context);
            strokePaint.setStrokeWidth(strokeWidth);

            Drawable fillDrawable = getDrawable(1);
            DrawableCompat.setTint(fillDrawable, colorAccent);
            // Not using ViewUtils.dpToPxOffset() for better visual effect.
            int fillInset = ViewUtils.dpToPxSize(FILL_INSET_DP, context);
            setLayerInset(1, fillInset, fillInset, fillInset, fillInset);
        }

        private static Shape cloneShape(Shape shape) {
            try {
                return shape.clone();
            } catch (CloneNotSupportedException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

    private static class RatingBadgeDrawable extends BaseRatingBadgeDrawable {

        public RatingBadgeDrawable(Context context) {
            super(new PolygonShape(8), context);
        }
    }

    private static class FollowingsRatingBadgeDrawable extends BaseRatingBadgeDrawable {

        public FollowingsRatingBadgeDrawable(Context context) {
            super(new StarShape(10, 0.85f), context);
        }
    }
}
