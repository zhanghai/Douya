/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.item.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.gallery.ui.GalleryActivity;
import me.zhanghai.android.douya.network.api.info.frodo.CollectableItem;
import me.zhanghai.android.douya.network.api.info.frodo.Honor;
import me.zhanghai.android.douya.network.api.info.frodo.ItemCollectionState;
import me.zhanghai.android.douya.network.api.info.frodo.Movie;
import me.zhanghai.android.douya.network.api.info.frodo.Photo;
import me.zhanghai.android.douya.network.api.info.frodo.Rating;
import me.zhanghai.android.douya.network.api.info.frodo.Review;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleCelebrity;
import me.zhanghai.android.douya.ui.DividerItemDecoration;
import me.zhanghai.android.douya.ui.HorizontalImageAdapter;
import me.zhanghai.android.douya.ui.RatioImageView;
import me.zhanghai.android.douya.util.ImageUtils;
import me.zhanghai.android.douya.util.StringUtils;
import me.zhanghai.android.douya.util.ViewUtils;

public class MovieAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM_HEADER = 0;
    private static final int ITEM_BADGE_LIST = 1;
    private static final int ITEM_INTRODUCTION = 2;
    private static final int ITEM_GALLERY = 3;
    private static final int ITEM_CELEBRITY_LIST = 4;
    private static final int ITEM_RATING = 5;

    private static final int ITEM_COUNT = 6;

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
            case ITEM_GALLERY: {
                GalleryHolder holder = new GalleryHolder(ViewUtils.inflate(
                        R.layout.item_gallery_item, parent));
                holder.photoList.setHasFixedSize(true);
                holder.photoList.setLayoutManager(new LinearLayoutManager(parent.getContext(),
                        LinearLayoutManager.HORIZONTAL, false));
                holder.photoList.addItemDecoration(new DividerItemDecoration(
                        DividerItemDecoration.HORIZONTAL,
                        R.drawable.transparent_divider_vertical_4dp,
                        holder.photoList.getContext()));
                holder.photoList.setAdapter(new HorizontalImageAdapter());
                return holder;
            }
            case ITEM_CELEBRITY_LIST: {
                CelebrityListHolder holder = new CelebrityListHolder(ViewUtils.inflate(
                        R.layout.item_celebrity_list_item, parent));
                holder.celebrityList.setHasFixedSize(true);
                holder.celebrityList.setLayoutManager(new LinearLayoutManager(parent.getContext(),
                        LinearLayoutManager.HORIZONTAL, false));
                holder.celebrityList.addItemDecoration(new DividerItemDecoration(
                        DividerItemDecoration.HORIZONTAL,
                        R.drawable.transparent_divider_vertical_16dp,
                        holder.celebrityList.getContext()));
                holder.celebrityList.setAdapter(new CelebrityListAdapter());
                return holder;
            }
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
                headerHolder.posterImage.setRatio(27, 40);
                // Image from movie.poster is cropped except large.
                ImageUtils.loadImage(headerHolder.posterImage, mData.movie.cover);
                headerHolder.posterImage.setOnClickListener(view -> {
                    Intent intent;
                    if (mData.movie.poster != null) {
                        intent = GalleryActivity.makeIntent(mData.movie.poster.image, context);
                    } else {
                        intent = GalleryActivity.makeIntent(mData.movie.cover, context);
                    }
                    context.startActivity(intent);
                });
                headerHolder.titleText.setText(mData.movie.title);
                headerHolder.originalTitleText.setText(mData.movie.originalTitle);
                String detail = StringUtils.joinNonEmpty("  ", mData.movie.getYearMonth(context),
                        mData.movie.getEpisodeCountString(), mData.movie.getDurationString());
                headerHolder.detailText.setText(detail);
                headerHolder.genresText.setText(CollectableItem.getListAsString(
                        mData.movie.genres));
                headerHolder.todoButton.setText(ItemCollectionState.TODO.getString(
                        CollectableItem.Type.MOVIE, context));
                headerHolder.doingButton.setText(ItemCollectionState.DOING.getString(
                        CollectableItem.Type.MOVIE, context));
                headerHolder.doneButton.setText(ItemCollectionState.DONE.getString(
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
                badgeListHolder.badgeListLayout.setTop250(top250Honor);
                badgeListHolder.badgeListLayout.setRating(mData.rating);
                badgeListHolder.badgeListLayout.setGenre(R.drawable.movie_badge_white_40dp,
                        mData.movie.genres.get(0));
                break;
            }
            case ITEM_INTRODUCTION: {
                IntroductionHolder introductionHolder = (IntroductionHolder) holder;
                introductionHolder.introductionText.setText(mData.movie.introduction);
                introductionHolder.introductionLayout.setOnClickListener(view -> {
                    // TODO
                });
                break;
            }
            case ITEM_GALLERY: {
                GalleryHolder galleryHolder = (GalleryHolder) holder;
                HorizontalImageAdapter adapter = (HorizontalImageAdapter)
                        galleryHolder.photoList.getAdapter();
                adapter.replace(mData.photoList);
                break;
            }
            case ITEM_CELEBRITY_LIST: {
                CelebrityListHolder celebrityListHolder = (CelebrityListHolder) holder;
                CelebrityListAdapter adapter = (CelebrityListAdapter)
                        celebrityListHolder.celebrityList.getAdapter();
                adapter.replace(mData.celebrityList);
                break;
            }
            case ITEM_RATING: {
                RatingHolder ratingHolder = (RatingHolder) holder;
                ratingHolder.ratingLayout.setRating(mData.movie.rating);
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
        public Rating rating;
        public List<SimpleCelebrity> celebrityList;
        public List<Photo> photoList;
        public List<Review> reviewList;

        public Data(Movie movie, Rating rating, List<SimpleCelebrity> celebrityList,
                    List<Photo> photoList, List<Review> reviewList) {
            this.movie = movie;
            this.rating = rating;
            this.celebrityList = celebrityList;
            this.photoList = photoList;
            this.reviewList = reviewList;
        }
    }

    static class HeaderHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.poster)
        public RatioImageView posterImage;
        @BindView(R.id.title)
        public TextView titleText;
        @BindView(R.id.original_title)
        public TextView originalTitleText;
        @BindView(R.id.detail)
        public TextView detailText;
        @BindView(R.id.genres)
        public TextView genresText;
        @BindView(R.id.todo)
        public Button todoButton;
        @BindView(R.id.doing)
        public Button doingButton;
        @BindView(R.id.done)
        public Button doneButton;

        public HeaderHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }

    static class BadgeListHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.badge_list_layout)
        public BadgeListLayout badgeListLayout;

        public BadgeListHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }

    static class IntroductionHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.introduction_layout)
        public ViewGroup introductionLayout;
        @BindView(R.id.introduction)
        public TextView introductionText;

        public IntroductionHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }

    static class CelebrityListHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.celebrity_list)
        public RecyclerView celebrityList;

        public CelebrityListHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }

    static class GalleryHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.photo_list)
        public RecyclerView photoList;
        @BindView(R.id.view_all)
        public Button viewAllButton;

        public GalleryHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }

    static class RatingHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.rating_layout)
        public RatingLayout ratingLayout;

        public RatingHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
