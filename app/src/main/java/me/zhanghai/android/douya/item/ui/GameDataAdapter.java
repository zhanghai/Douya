/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.item.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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
import me.zhanghai.android.douya.network.api.info.frodo.Game;
import me.zhanghai.android.douya.network.api.info.frodo.Photo;
import me.zhanghai.android.douya.network.api.info.frodo.Rating;
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

public class GameDataAdapter extends BaseItemDataAdapter<Game> {

    private enum Items {
        HEADER,
        ITEM_COLLECTION,
        BADGE_LIST,
        INTRODUCTION,
        RATING,
        ITEM_COLLECTION_LIST,
        REVIEW_LIST,
        FORUM_TOPIC_LIST,
        RECOMMENDATION_LIST,
        RELATED_DOULIST_LIST
    }

    private Data mData;

    public GameDataAdapter(Listener listener) {
        super(listener);
    }

    @Override
    protected Listener getListener() {
        return (Listener) super.getListener();
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
        if (mData.game == null) {
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
        return new HeaderHolder(ViewUtils.inflate(R.layout.item_fragment_game_header, parent));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position,
                                 @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);

        switch (Items.values()[position]) {
            case HEADER:
                bindHeaderHolder(holder, mData.game);
                break;
            case ITEM_COLLECTION:
                bindItemCollectionHolder(holder, mData.game);
                break;
            case BADGE_LIST:
                bindBadgeListHolder(holder, mData.game, mData.rating);
                break;
            case INTRODUCTION:
                bindIntroductionHolder(holder, mData.game);
                break;
            case RATING:
                bindRatingHolder(holder, mData.game, mData.rating);
                break;
            case ITEM_COLLECTION_LIST:
                bindItemCollectionListHolder(holder, mData.game, mData.itemCollectionList,
                        payloads);
                break;
            case REVIEW_LIST:
                bindReviewListHolder(holder, mData.game, mData.reviewList);
                break;
            case FORUM_TOPIC_LIST:
                bindForumTopicListHolder(holder, mData.game, mData.forumTopicList);
                break;
            case RECOMMENDATION_LIST:
                bindRecommendationListHolder(holder, mData.game, mData.recommendationList);
                break;
            case RELATED_DOULIST_LIST:
                bindRelatedDoulistListHolder(holder, mData.game, mData.relatedDoulistList);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    private void bindHeaderHolder(RecyclerView.ViewHolder holder, Game game) {
        HeaderHolder headerHolder = (HeaderHolder) holder;
        boolean coverVisible = ViewUtils.isInLandscape(headerHolder.coverImage.getContext());
        ViewUtils.setVisibleOrGone(headerHolder.coverImage, coverVisible);
        if (coverVisible) {
            headerHolder.coverImage.setRatio(1, 1);
            ImageUtils.loadImage(headerHolder.coverImage, game.cover);
            headerHolder.coverImage.setOnClickListener(view -> {
                Context context = view.getContext();
                context.startActivity(GalleryActivity.makeIntent(game.cover, context));
            });
        }
        headerHolder.titleText.setText(game.title);
        Context context = RecyclerViewUtils.getContext(holder);
        String slashDelimiter = context.getString(R.string.item_information_delimiter_slash);
        headerHolder.platformsText.setText(StringCompat.join(slashDelimiter,
                game.getPlatformNames()));
        String spaceDelimiter = context.getString(R.string.item_information_delimiter_space);
        String detail = StringUtils.joinNonEmpty(spaceDelimiter, game.getYearMonth(context),
                StringCompat.join(slashDelimiter, game.developers));
        headerHolder.detailText.setText(detail);
        headerHolder.genresText.setText(StringCompat.join(slashDelimiter, game.genres));
    }

    private void bindBadgeListHolder(RecyclerView.ViewHolder holder, Game game, Rating rating) {
        BadgeListHolder badgeListHolder = (BadgeListHolder) holder;
        badgeListHolder.badgeListLayout.setTop250(null);
        badgeListHolder.badgeListLayout.setRating(rating, game);
        badgeListHolder.badgeListLayout.setGenre(R.drawable.game_badge_white_40dp,
                CollectionUtils.firstOrNull(game.genres));
    }

    @Override
    protected void bindIntroductionHolder(RecyclerView.ViewHolder holder, Game game) {
        super.bindIntroductionHolder(holder, game);

        IntroductionHolder introductionHolder = (IntroductionHolder) holder;
        introductionHolder.introductionLayout.setOnClickListener(view -> {
            Context context = view.getContext();
            context.startActivity(ItemIntroductionActivity.makeIntent(game, context));
        });
    }

    public interface Listener extends BaseItemDataAdapter.Listener<Game> {}

    public static class Data {

        public Game game;
        public Rating rating;
        public List<Photo> photoList;
        public boolean excludeFirstPhoto;
        public List<SimpleItemCollection> itemCollectionList;
        public List<SimpleReview> gameGuideList;
        public List<SimpleReview> reviewList;
        public List<SimpleItemForumTopic> forumTopicList;
        public List<CollectableItem> recommendationList;
        public List<Doulist> relatedDoulistList;

        public Data(Game game, Rating rating, List<Photo> photoList, boolean excludeFirstPhoto,
                    List<SimpleItemCollection> itemCollectionList, List<SimpleReview> gameGuideList,
                    List<SimpleReview> reviewList, List<SimpleItemForumTopic> forumTopicList,
                    List<CollectableItem> recommendationList, List<Doulist> relatedDoulistList) {
            this.game = game;
            this.rating = rating;
            this.photoList = photoList;
            this.excludeFirstPhoto = excludeFirstPhoto;
            this.itemCollectionList = itemCollectionList;
            this.gameGuideList = gameGuideList;
            this.reviewList = reviewList;
            this.forumTopicList = forumTopicList;
            this.recommendationList = recommendationList;
            this.relatedDoulistList = relatedDoulistList;
        }
    }

    static class HeaderHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.cover)
        public RatioImageView coverImage;
        @BindView(R.id.title)
        public TextView titleText;
        @BindView(R.id.platforms)
        public TextView platformsText;
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
