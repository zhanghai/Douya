/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.item.ui;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.gallery.ui.GalleryActivity;
import me.zhanghai.android.douya.network.api.info.frodo.CollectableItem;
import me.zhanghai.android.douya.network.api.info.frodo.Doulist;
import me.zhanghai.android.douya.network.api.info.frodo.Honor;
import me.zhanghai.android.douya.network.api.info.frodo.ItemAwardItem;
import me.zhanghai.android.douya.network.api.info.frodo.Movie;
import me.zhanghai.android.douya.network.api.info.frodo.Photo;
import me.zhanghai.android.douya.network.api.info.frodo.Rating;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleCelebrity;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleItemCollection;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleItemForumTopic;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleReview;
import me.zhanghai.android.douya.ui.RatioImageView;
import me.zhanghai.android.douya.util.CollectionUtils;
import me.zhanghai.android.douya.util.ImageUtils;
import me.zhanghai.android.douya.util.RecyclerViewUtils;
import me.zhanghai.android.douya.util.StringCompat;
import me.zhanghai.android.douya.util.StringUtils;
import me.zhanghai.android.douya.util.ViewUtils;

public class MovieDataAdapter extends BaseItemDataAdapter<Movie> {

    private enum Items {
        HEADER,
        ITEM_COLLECTION,
        BADGE_LIST,
        INTRODUCTION,
        PHOTO_LIST,
        CELEBRITY_LIST,
        AWARD_LIST,
        RATING,
        ITEM_COLLECTION_LIST,
        REVIEW_LIST,
        FORUM_TOPIC_LIST,
        RECOMMENDATION_LIST,
        RELATED_DOULIST_LIST
    }

    private Data mData;

    public MovieDataAdapter(Listener listener) {
        super(listener);
    }

    public void setData(Data data) {
        mData = data;
        notifyDataChanged();
    }

    public void notifyItemCollectionChanged() {
        int position = Items.ITEM_COLLECTION.ordinal();
        if (position < getItemCount()) {
            notifyItemChanged(position);
        }
    }

    public void notifyItemCollectionListItemChanged(int position,
                                                    SimpleItemCollection newItemCollection) {
        notifyItemCollectionListItemChanged(Items.ITEM_COLLECTION_LIST.ordinal(), position,
                newItemCollection);
    }

    @Override
    public int getTotalItemCount() {
        return Items.values().length;
    }

    @Override
    protected boolean isItemLoaded(int position) {
        if (mData == null) {
            return false;
        }
        if (mData.movie == null) {
            return false;
        }
        switch (Items.values()[position]) {
            case HEADER:
                return true;
            case ITEM_COLLECTION:
                return true;
            case BADGE_LIST:
                return mData.rating != null;
            case INTRODUCTION:
                return true;
            case PHOTO_LIST:
                return mData.photoList != null;
            case CELEBRITY_LIST:
                return mData.celebrityList != null;
            case AWARD_LIST:
                return mData.awardList != null;
            case RATING:
                return mData.rating != null;
            case ITEM_COLLECTION_LIST:
                return mData.itemCollectionList != null;
            case REVIEW_LIST:
                return mData.reviewList != null;
            case FORUM_TOPIC_LIST:
                return mData.forumTopicList != null;
            case RECOMMENDATION_LIST:
                return mData.recommendationList != null;
            case RELATED_DOULIST_LIST:
                return mData.relatedDoulistList != null;
            default:
                throw new IllegalArgumentException();
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (Items.values()[viewType]) {
            case HEADER:
                return createHeaderHolder(parent);
            case ITEM_COLLECTION:
                return createItemCollectionHolder(parent);
            case BADGE_LIST:
                return createBadgeListHolder(parent);
            case INTRODUCTION:
                return createIntroductionHolder(parent);
            case PHOTO_LIST:
                return createPhotoListHolder(parent);
            case CELEBRITY_LIST:
                return createCelebrityListHolder(parent);
            case AWARD_LIST:
                return createAwardListHolder(parent);
            case RATING:
                return createRatingHolder(parent);
            case ITEM_COLLECTION_LIST:
                return createItemCollectionListHolder(parent);
            case REVIEW_LIST:
                return createReviewListHolder(parent);
            case FORUM_TOPIC_LIST:
                return createForumTopicListHolder(parent);
            case RECOMMENDATION_LIST:
                return createRecommendationListHolder(parent);
            case RELATED_DOULIST_LIST:
                return createRelatedDoulistListHolder(parent);
            default:
                throw new IllegalArgumentException();
        }
    }

    private HeaderHolder createHeaderHolder(ViewGroup parent) {
        return new HeaderHolder(ViewUtils.inflate(R.layout.item_fragment_movie_header, parent));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position,
                                 @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);

        switch (Items.values()[position]) {
            case HEADER:
                bindHeaderHolder(holder, mData.movie);
                break;
            case ITEM_COLLECTION:
                bindItemCollectionHolder(holder, mData.movie);
                break;
            case BADGE_LIST:
                bindBadgeListHolder(holder, mData.movie, mData.rating);
                break;
            case INTRODUCTION:
                bindIntroductionHolder(holder, mData.movie);
                break;
            case PHOTO_LIST:
                bindPhotoListHolder(holder, mData.movie, mData.photoList, mData.excludeFirstPhoto);
                break;
            case CELEBRITY_LIST:
                bindCelebrityListHolder(holder, mData.movie, mData.celebrityList);
                break;
            case AWARD_LIST:
                bindAwardListHolder(holder, mData.movie, mData.awardList);
                break;
            case RATING:
                bindRatingHolder(holder, mData.movie, mData.rating);
                break;
            case ITEM_COLLECTION_LIST:
                bindItemCollectionListHolder(holder, mData.movie, mData.itemCollectionList,
                        payloads);
                break;
            case REVIEW_LIST:
                bindReviewListHolder(holder, mData.movie, mData.reviewList);
                break;
            case FORUM_TOPIC_LIST:
                bindForumTopicListHolder(holder, mData.movie, mData.forumTopicList);
                break;
            case RECOMMENDATION_LIST:
                bindRecommendationListHolder(holder, mData.movie, mData.recommendationList);
                break;
            case RELATED_DOULIST_LIST:
                bindRelatedDoulistListHolder(holder, mData.movie, mData.relatedDoulistList);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    private void bindHeaderHolder(RecyclerView.ViewHolder holder, Movie movie) {
        HeaderHolder headerHolder = (HeaderHolder) holder;
        headerHolder.posterImage.setRatio(27, 40);
        // Image from movie.poster is cropped except large.
        ImageUtils.loadImage(headerHolder.posterImage, movie.cover);
        headerHolder.posterImage.setOnClickListener(view -> {
            Context context = view.getContext();
            Intent intent;
            if (movie.poster != null) {
                intent = GalleryActivity.makeIntent(movie.poster.image, context);
            } else {
                intent = GalleryActivity.makeIntent(movie.cover, context);
            }
            context.startActivity(intent);
        });
        headerHolder.titleText.setText(movie.title);
        headerHolder.originalTitleText.setText(movie.originalTitle);
        Context context = RecyclerViewUtils.getContext(holder);
        String spaceDelimiter = context.getString(R.string.item_information_delimiter_space);
        String detail = StringUtils.joinNonEmpty(spaceDelimiter, movie.getYearMonth(context),
                movie.getEpisodeCountString(), movie.getDurationString());
        headerHolder.detailText.setText(detail);
        String slashDelimiter = context.getString(R.string.item_information_delimiter_slash);
        headerHolder.genresText.setText(StringCompat.join(slashDelimiter, movie.genres));
    }

    private void bindBadgeListHolder(RecyclerView.ViewHolder holder, Movie movie, Rating rating) {
        BadgeListHolder badgeListHolder = (BadgeListHolder) holder;
        Honor top250Honor = null;
        for (Honor honor : movie.honors) {
            if (honor.getType() == Honor.Type.TOP_250) {
                top250Honor = honor;
                break;
            }
        }
        badgeListHolder.badgeListLayout.setTop250(top250Honor);
        badgeListHolder.badgeListLayout.setRating(rating, movie);
        badgeListHolder.badgeListLayout.setGenre(R.drawable.movie_badge_white_40dp,
                CollectionUtils.firstOrNull(movie.genres), CollectableItem.Type.MOVIE);
    }

    @Override
    protected void bindIntroductionHolder(RecyclerView.ViewHolder holder, Movie movie) {
        super.bindIntroductionHolder(holder, movie);

        IntroductionHolder introductionHolder = (IntroductionHolder) holder;
        introductionHolder.introductionLayout.setOnClickListener(view -> {
            Context context = view.getContext();
            context.startActivity(ItemIntroductionActivity.makeIntent(movie, context));
        });
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setClipChildren(false);
    }

    public interface Listener extends BaseItemDataAdapter.Listener<Movie> {}

    public static class Data {

        public Movie movie;
        public Rating rating;
        public List<Photo> photoList;
        public boolean excludeFirstPhoto;
        public List<SimpleCelebrity> celebrityList;
        public List<ItemAwardItem> awardList;
        public List<SimpleItemCollection> itemCollectionList;
        public List<SimpleReview> reviewList;
        public List<SimpleItemForumTopic> forumTopicList;
        public List<CollectableItem> recommendationList;
        public List<Doulist> relatedDoulistList;

        public Data(Movie movie, Rating rating, List<Photo> photoList, boolean excludeFirstPhoto,
                    List<SimpleCelebrity> celebrityList, List<ItemAwardItem> awardList,
                    List<SimpleItemCollection> itemCollectionList, List<SimpleReview> reviewList,
                    List<SimpleItemForumTopic> forumTopicList,
                    List<CollectableItem> recommendationList, List<Doulist> relatedDoulistList) {
            this.movie = movie;
            this.rating = rating;
            this.photoList = photoList;
            this.excludeFirstPhoto = excludeFirstPhoto;
            this.celebrityList = celebrityList;
            this.awardList = awardList;
            this.itemCollectionList = itemCollectionList;
            this.reviewList = reviewList;
            this.forumTopicList = forumTopicList;
            this.recommendationList = recommendationList;
            this.relatedDoulistList = relatedDoulistList;
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
}
