/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.item.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.gallery.ui.GalleryActivity;
import me.zhanghai.android.douya.network.api.info.frodo.CollectableItem;
import me.zhanghai.android.douya.network.api.info.frodo.Honor;
import me.zhanghai.android.douya.network.api.info.frodo.ItemCollectionState;
import me.zhanghai.android.douya.network.api.info.frodo.Movie;
import me.zhanghai.android.douya.ui.RatioImageView;
import me.zhanghai.android.douya.util.ImageUtils;
import me.zhanghai.android.douya.util.StringUtils;
import me.zhanghai.android.douya.util.ViewUtils;

public class MovieAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM_HEADER = 0;
    private static final int ITEM_BADGE_LIST = 1;
    private static final int ITEM_INTRODUCTION = 2;
    private static final int ITEM_RATING = 3;

    private static final int ITEM_COUNT = 4;

    private Data mData;

    public MovieAdapter() {
        setHasStableIds(true);
    }

    public void setData(Data data) {
        mData = data;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mData != null ? ITEM_COUNT : 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_HEADER:
                return new HeaderHolder(ViewUtils.inflate(R.layout.movie_header_item, parent));
            case ITEM_BADGE_LIST:
                return new BadgeListHolder(ViewUtils.inflate(R.layout.item_badge_list_item,
                        parent));
            case ITEM_INTRODUCTION:
                return new IntroductionHolder(ViewUtils.inflate(R.layout.item_introduction_item,
                        parent));
            case ITEM_RATING:
                return new RatingHolder(ViewUtils.inflate(R.layout.item_rating_item, parent));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Context context = holder.itemView.getContext();
        switch (position) {
            case ITEM_HEADER: {
                HeaderHolder headerHolder = (HeaderHolder) holder;
                headerHolder.mPosterImage.setRatio(27, 40);
                // Image from movie.poster is cropped except large.
                ImageUtils.loadImage(headerHolder.mPosterImage, mData.movie.cover);
                headerHolder.mPosterImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent;
                        if (mData.movie.poster != null) {
                            intent = GalleryActivity.makeIntent(mData.movie.poster.image, context);
                        } else {
                            intent = GalleryActivity.makeIntent(mData.movie.cover, context);
                        }
                        context.startActivity(intent);
                    }
                });
                headerHolder.mTitleText.setText(mData.movie.title);
                headerHolder.mOriginalTitleText.setText(mData.movie.originalTitle);
                String detail = StringUtils.joinNonEmpty("  ", mData.movie.getYearMonth(context),
                        mData.movie.getEpisodeCountString(), mData.movie.getDurationString());
                headerHolder.mDetailText.setText(detail);
                headerHolder.mGenresText.setText(CollectableItem.getListAsString(
                        mData.movie.genres));
                headerHolder.mTodoButton.setText(ItemCollectionState.TODO.getString(
                        CollectableItem.Type.MOVIE, context));
                headerHolder.mDoingButton.setText(ItemCollectionState.DOING.getString(
                        CollectableItem.Type.MOVIE, context));
                headerHolder.mDoneButton.setText(ItemCollectionState.DONE.getString(
                        CollectableItem.Type.MOVIE, context));
                break;
            }
            case ITEM_BADGE_LIST: {
                BadgeListHolder badgeListHolder = (BadgeListHolder) holder;
                Honor top250Honor = null;
                for (Honor honor : mData.movie.honors) {
                    if (honor.getType() == Honor.Type.TOP_250) {
                        top250Honor = honor;
                        break;
                    }
                }
                badgeListHolder.mBadgeListLayout.setTop250(top250Honor);
                badgeListHolder.mBadgeListLayout.setRating(mData.movie.rating);
                badgeListHolder.mBadgeListLayout.setGenre(R.drawable.movie_badge_white_40dp,
                        mData.movie.genres.get(0));
                break;
            }
            case ITEM_INTRODUCTION: {
                IntroductionHolder introductionHolder = (IntroductionHolder) holder;
                introductionHolder.mIntroductionText.setText(mData.movie.introduction);
                introductionHolder.mIntroductionLayout.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // TODO
                            }
                        });
                break;
            }
            case ITEM_RATING: {
                RatingHolder ratingHolder = (RatingHolder) holder;
                ratingHolder.mRatingLayout.setRating(mData.movie.rating);
                break;
            }
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        recyclerView.setClipChildren(false);
    }

    public static class Data {

        public Movie movie;

        public Data(Movie movie) {
            this.movie = movie;
        }
    }

    static class HeaderHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.poster)
        RatioImageView mPosterImage;
        @BindView(R.id.title)
        TextView mTitleText;
        @BindView(R.id.original_title)
        TextView mOriginalTitleText;
        @BindView(R.id.detail)
        TextView mDetailText;
        @BindView(R.id.genres)
        TextView mGenresText;
        @BindView(R.id.todo)
        Button mTodoButton;
        @BindView(R.id.doing)
        Button mDoingButton;
        @BindView(R.id.done)
        Button mDoneButton;

        public HeaderHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }

    static class BadgeListHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.badge_list_layout)
        BadgeListLayout mBadgeListLayout;

        public BadgeListHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }

    static class IntroductionHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.introduction_layout)
        ViewGroup mIntroductionLayout;
        @BindView(R.id.introduction)
        TextView mIntroductionText;

        public IntroductionHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }

    static class RatingHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.rating_layout)
        RatingLayout mRatingLayout;

        public RatingHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
