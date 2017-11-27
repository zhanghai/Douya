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
import me.zhanghai.android.douya.link.UriHandler;
import me.zhanghai.android.douya.network.api.info.frodo.CollectableItem;
import me.zhanghai.android.douya.network.api.info.frodo.Honor;
import me.zhanghai.android.douya.network.api.info.frodo.ItemAwardItem;
import me.zhanghai.android.douya.network.api.info.frodo.ItemCollection;
import me.zhanghai.android.douya.network.api.info.frodo.ItemCollectionState;
import me.zhanghai.android.douya.network.api.info.frodo.Movie;
import me.zhanghai.android.douya.network.api.info.frodo.Photo;
import me.zhanghai.android.douya.network.api.info.frodo.Rating;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleCelebrity;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleReview;
import me.zhanghai.android.douya.ui.AdapterLinearLayout;
import me.zhanghai.android.douya.ui.DividerItemDecoration;
import me.zhanghai.android.douya.ui.HorizontalImageAdapter;
import me.zhanghai.android.douya.ui.RatioImageView;
import me.zhanghai.android.douya.util.CollectionUtils;
import me.zhanghai.android.douya.util.ImageUtils;
import me.zhanghai.android.douya.util.StringUtils;
import me.zhanghai.android.douya.util.ViewUtils;

public class MovieAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM_HEADER = 0;
    private static final int ITEM_COLLECTION = 1;
    private static final int ITEM_BADGE_LIST = 2;
    private static final int ITEM_INTRODUCTION = 3;
    private static final int ITEM_PHOTO_LIST = 4;
    private static final int ITEM_CELEBRITY_LIST = 5;
    private static final int ITEM_AWARD_LIST = 6;
    private static final int ITEM_RATING = 7;
    private static final int ITEM_COLLECTION_LIST = 8;
    private static final int ITEM_REVIEW_LIST = 9;

    private static final int ITEM_COUNT = 10;

    private static final int ITEM_COLLECTION_LIST_MAX_SIZE = 5;
    private static final int ITEM_REVIEW_LIST_MAX_SIZE = 5;

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
                return new HeaderHolder(ViewUtils.inflate(R.layout.item_movie_fragment_header,
                        parent));
            case ITEM_COLLECTION:
                return new ItemCollectionHolder(ViewUtils.inflate(R.layout.item_fragment_collection,
                        parent));
            case ITEM_BADGE_LIST:
                return new BadgeListHolder(ViewUtils.inflate(R.layout.item_fragment_badge_list,
                        parent));
            case ITEM_INTRODUCTION:
                return new IntroductionHolder(ViewUtils.inflate(R.layout.item_fragment_introduction,
                        parent));
            case ITEM_PHOTO_LIST: {
                PhotoListHolder holder = new PhotoListHolder(ViewUtils.inflate(
                        R.layout.item_fragment_photo_list, parent));
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
                        R.layout.item_fragment_celebrity_list, parent));
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
            case ITEM_AWARD_LIST: {
                AwardListHolder holder = new AwardListHolder(ViewUtils.inflate(
                        R.layout.item_fragment_award_list, parent));
                ViewUtils.setVisibleOrGone(holder.awardList, !mData.awardList.isEmpty());
                holder.awardList.setHasFixedSize(true);
                holder.awardList.setLayoutManager(new LinearLayoutManager(parent.getContext(),
                        LinearLayoutManager.HORIZONTAL, false));
                holder.awardList.addItemDecoration(new DividerItemDecoration(
                        DividerItemDecoration.HORIZONTAL,
                        R.drawable.transparent_divider_vertical_16dp,
                        holder.awardList.getContext()));
                holder.awardList.setAdapter(new ItemAwardListAdapter());
                return holder;
            }
            case ITEM_RATING: {
                RatingHolder holder = new RatingHolder(ViewUtils.inflate(
                        R.layout.item_fragment_rating, parent));
                holder.ratingDistributionLayout.setCompact(true);
                return holder;
            }
            case ITEM_COLLECTION_LIST: {
                ItemCollectionListHolder holder = new ItemCollectionListHolder(ViewUtils.inflate(
                        R.layout.item_fragment_collection_list, parent));
                ViewUtils.setVisibleOrGone(holder.itemCollectionList,
                        !mData.itemCollectionList.isEmpty());
                holder.itemCollectionList.setAdapter(new ItemCollectionListAdapter());
                return holder;
            }
            case ITEM_REVIEW_LIST: {
                ReviewListHolder holder = new ReviewListHolder(ViewUtils.inflate(
                        R.layout.item_fragment_review_list, parent));
                ViewUtils.setVisibleOrGone(holder.reviewList, !mData.reviewList.isEmpty());
                holder.reviewList.setAdapter(new ReviewListAdapter());
                return holder;
            }
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // HACK: Make sure we don't click through any view to our backdrop.
        holder.itemView.setClickable(true);
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
                break;
            }
            case ITEM_COLLECTION: {
                ItemCollectionHolder itemCollectionHolder = (ItemCollectionHolder) holder;
                itemCollectionHolder.todoButton.setText(ItemCollectionState.TODO.getString(
                        CollectableItem.Type.MOVIE, context));
                itemCollectionHolder.doingButton.setText(ItemCollectionState.DOING.getString(
                        CollectableItem.Type.MOVIE, context));
                itemCollectionHolder.doneButton.setText(ItemCollectionState.DONE.getString(
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
                badgeListHolder.badgeListLayout.setRating(mData.rating, mData.movie);
                badgeListHolder.badgeListLayout.setGenre(R.drawable.movie_badge_white_40dp,
                        CollectionUtils.firstOrNull(mData.movie.genres));
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
            case ITEM_PHOTO_LIST: {
                PhotoListHolder photoListHolder = (PhotoListHolder) holder;
                HorizontalImageAdapter adapter = (HorizontalImageAdapter)
                        photoListHolder.photoList.getAdapter();
                List<Photo> photoList = mData.photoList;
                if (mData.excludeFirstPhoto) {
                    photoList = photoList.subList(1, photoList.size());
                }
                adapter.replace(photoList);
                adapter.setOnImageClickListener(photoPosition -> {
                    // TODO: Use PhotoAlbumGalleryActivity instead.
                    if (mData.excludeFirstPhoto) {
                        ++photoPosition;
                    }
                    context.startActivity(GalleryActivity.makeIntent(mData.photoList, photoPosition,
                            photoListHolder.photoList.getContext()));
                });
                photoListHolder.viewMoreButton.setOnClickListener(view -> {
                    // TODO
                    UriHandler.open(mData.movie.url + "/photos", context);
                });
                break;
            }
            case ITEM_CELEBRITY_LIST: {
                CelebrityListHolder celebrityListHolder = (CelebrityListHolder) holder;
                CelebrityListAdapter adapter = (CelebrityListAdapter)
                        celebrityListHolder.celebrityList.getAdapter();
                adapter.replace(mData.celebrityList);
                break;
            }
            case ITEM_AWARD_LIST: {
                AwardListHolder awardListHolder = (AwardListHolder) holder;
                ItemAwardListAdapter adapter = (ItemAwardListAdapter)
                        awardListHolder.awardList.getAdapter();
                adapter.replace(mData.awardList);
                awardListHolder.itemView.setOnClickListener(view -> {
                    // TODO
                    UriHandler.open(mData.movie.url + "/awards/", view.getContext());
                });
                break;
            }
            case ITEM_RATING: {
                RatingHolder ratingHolder = (RatingHolder) holder;
                ratingHolder.ratingLayout.setRating(mData.rating.rating);
                ratingHolder.ratingDistributionLayout.setRating(mData.rating);
                ratingHolder.itemView.setOnClickListener(view -> {
                    UriHandler.open(mData.movie.url + "/collections", view.getContext());
                });
                break;
            }
            case ITEM_COLLECTION_LIST: {
                ItemCollectionListHolder itemCollectionListHolder =
                        (ItemCollectionListHolder) holder;
                ItemCollectionListAdapter adapter = (ItemCollectionListAdapter)
                        itemCollectionListHolder.itemCollectionList.getAdapter();
                List<ItemCollection> itemCollectionList = mData.itemCollectionList.subList(0,
                        Math.min(ITEM_COLLECTION_LIST_MAX_SIZE, mData.itemCollectionList.size()));
                adapter.replace(itemCollectionList);
                itemCollectionListHolder.viewMoreButton.setOnClickListener(view -> {
                    // TODO
                    UriHandler.open(mData.movie.url + "/collections", view.getContext());
                });
                break;
            }
            case ITEM_REVIEW_LIST: {
                ReviewListHolder reviewListHolder = (ReviewListHolder) holder;
                ReviewListAdapter adapter = (ReviewListAdapter)
                        reviewListHolder.reviewList.getAdapter();
                List<SimpleReview> reviewList = mData.reviewList.subList(0, Math.min(
                        ITEM_REVIEW_LIST_MAX_SIZE, mData.reviewList.size()));
                adapter.replace(reviewList);
                reviewListHolder.viewMoreButton.setOnClickListener(view -> {
                    // TODO
                    UriHandler.open(mData.movie.url + "/reviews", view.getContext());
                });
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
        public List<Photo> photoList;
        public boolean excludeFirstPhoto;
        public List<SimpleCelebrity> celebrityList;
        public List<ItemAwardItem> awardList;
        public List<ItemCollection> itemCollectionList;
        public List<SimpleReview> reviewList;

        public Data(Movie movie, Rating rating, List<Photo> photoList, boolean excludeFirstPhoto,
                    List<SimpleCelebrity> celebrityList, List<ItemAwardItem> awardList,
                    List<ItemCollection> itemCollectionList, List<SimpleReview> reviewList) {
            this.movie = movie;
            this.rating = rating;
            this.photoList = photoList;
            this.excludeFirstPhoto = excludeFirstPhoto;
            this.celebrityList = celebrityList;
            this.awardList = awardList;
            this.itemCollectionList = itemCollectionList;
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

        public HeaderHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }

    static class ItemCollectionHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.todo)
        public Button todoButton;
        @BindView(R.id.doing)
        public Button doingButton;
        @BindView(R.id.done)
        public Button doneButton;

        public ItemCollectionHolder(View itemView) {
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

    static class PhotoListHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.photo_list)
        public RecyclerView photoList;
        @BindView(R.id.view_more)
        public Button viewMoreButton;

        public PhotoListHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }

    static class AwardListHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.award_list)
        public RecyclerView awardList;

        public AwardListHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }

    static class RatingHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.rating_layout)
        public RatingLayout ratingLayout;
        @BindView(R.id.rating_distribution_layout)
        public RatingDistributionLayout ratingDistributionLayout;

        public RatingHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }

    static class ItemCollectionListHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_collection_list)
        public AdapterLinearLayout itemCollectionList;
        @BindView(R.id.view_more)
        public Button viewMoreButton;

        public ItemCollectionListHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }

    static class ReviewListHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_review_list)
        public AdapterLinearLayout reviewList;
        @BindView(R.id.view_more)
        public Button viewMoreButton;

        public ReviewListHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
