/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.item.ui;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.gallery.ui.GalleryActivity;
import me.zhanghai.android.douya.media.PlayMusicService;
import me.zhanghai.android.douya.network.api.info.frodo.CollectableItem;
import me.zhanghai.android.douya.network.api.info.frodo.Doulist;
import me.zhanghai.android.douya.network.api.info.frodo.Music;
import me.zhanghai.android.douya.network.api.info.frodo.Rating;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleItemCollection;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleItemForumTopic;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleReview;
import me.zhanghai.android.douya.ui.AdapterLinearLayout;
import me.zhanghai.android.douya.ui.RatioImageView;
import me.zhanghai.android.douya.util.CollectionUtils;
import me.zhanghai.android.douya.util.ImageUtils;
import me.zhanghai.android.douya.util.ObjectUtils;
import me.zhanghai.android.douya.util.RecyclerViewUtils;
import me.zhanghai.android.douya.util.StringCompat;
import me.zhanghai.android.douya.util.StringUtils;
import me.zhanghai.android.douya.util.ViewUtils;

public class MusicDataAdapter extends BaseItemDataAdapter<Music> {

    private enum Items {
        HEADER,
        ITEM_COLLECTION,
        BADGE_LIST,
        INTRODUCTION,
        TRACK_LIST,
        RATING,
        ITEM_COLLECTION_LIST,
        REVIEW_LIST,
        FORUM_TOPIC_LIST,
        RECOMMENDATION_LIST,
        RELATED_DOULIST_LIST
    }

    private Data mData;

    public MusicDataAdapter(Listener listener) {
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
        if (mData.music == null) {
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
            case TRACK_LIST:
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
            case TRACK_LIST:
                return createTrackListHolder(parent);
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
        return new HeaderHolder(ViewUtils.inflate(R.layout.item_fragment_music_header, parent));
    }

    private TrackListHolder createTrackListHolder(ViewGroup parent) {
        TrackListHolder holder = new TrackListHolder(ViewUtils.inflate(
                R.layout.item_fragment_track_list, parent));
        holder.trackList.setAdapter(new TrackListAdapter(getListener()));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position,
                                 @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);

        switch (Items.values()[position]) {
            case HEADER:
                bindHeaderHolder(holder, mData.music);
                break;
            case ITEM_COLLECTION:
                bindItemCollectionHolder(holder, mData.music);
                break;
            case BADGE_LIST:
                bindBadgeListHolder(holder, mData.music, mData.rating);
                break;
            case INTRODUCTION:
                bindIntroductionHolder(holder, mData.music);
                break;
            case TRACK_LIST:
                bindTrackListHolder(holder, mData.music, payloads);
                break;
            case RATING:
                bindRatingHolder(holder, mData.music, mData.rating);
                break;
            case ITEM_COLLECTION_LIST:
                bindItemCollectionListHolder(holder, mData.music, mData.itemCollectionList,
                        payloads);
                break;
            case REVIEW_LIST:
                bindReviewListHolder(holder, mData.music, mData.reviewList);
                break;
            case FORUM_TOPIC_LIST:
                bindForumTopicListHolder(holder, mData.music, mData.forumTopicList);
                break;
            case RECOMMENDATION_LIST:
                bindRecommendationListHolder(holder, mData.music, mData.recommendationList);
                break;
            case RELATED_DOULIST_LIST:
                bindRelatedDoulistListHolder(holder, mData.music, mData.relatedDoulistList);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    private void bindHeaderHolder(RecyclerView.ViewHolder holder, Music music) {
        HeaderHolder headerHolder = (HeaderHolder) holder;
        boolean coverVisible = ViewUtils.isInLandscape(headerHolder.coverImage.getContext());
        ViewUtils.setVisibleOrGone(headerHolder.coverImage, coverVisible);
        if (coverVisible) {
            headerHolder.coverImage.setRatio(1, 1);
            ImageUtils.loadImage(headerHolder.coverImage, music.cover);
            headerHolder.coverImage.setOnClickListener(view -> {
                Context context = view.getContext();
                context.startActivity(GalleryActivity.makeIntent(music.cover, context));
            });
        }
        headerHolder.titleText.setText(music.title);
        Context context = RecyclerViewUtils.getContext(holder);
        String slashDelimiter = context.getString(R.string.item_information_delimiter_slash);
        headerHolder.artistsText.setText(StringCompat.join(slashDelimiter, music.getArtistNames()));
        String spaceDelimiter = context.getString(R.string.item_information_delimiter_space);
        String detail = StringUtils.joinNonEmpty(spaceDelimiter, music.getYearMonth(context),
                StringCompat.join(slashDelimiter, music.publishers));
        headerHolder.detailText.setText(detail);
        headerHolder.genresText.setText(StringCompat.join(slashDelimiter, music.genres));
    }

    private void bindBadgeListHolder(RecyclerView.ViewHolder holder, Music music, Rating rating) {
        BadgeListHolder badgeListHolder = (BadgeListHolder) holder;
        badgeListHolder.badgeListLayout.setTop250(null);
        badgeListHolder.badgeListLayout.setRating(rating, music);
        badgeListHolder.badgeListLayout.setGenre(R.drawable.music_badge_white_40dp,
                CollectionUtils.firstOrNull(music.genres), CollectableItem.Type.MUSIC);
    }

    @Override
    protected void bindIntroductionHolder(RecyclerView.ViewHolder holder, Music music) {
        super.bindIntroductionHolder(holder, music);

        IntroductionHolder introductionHolder = (IntroductionHolder) holder;
        introductionHolder.introductionLayout.setOnClickListener(view -> {
            Context context = view.getContext();
            context.startActivity(ItemIntroductionActivity.makeIntent(music, context));
        });
    }

    private void bindTrackListHolder(RecyclerView.ViewHolder holder, Music music,
                                     @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            //noinspection deprecation
            bindTrackListHolder(holder, music);
        } else {
            //noinspection deprecation
            bindTrackListHolder(holder);
        }
    }

    /**
     * @deprecated Use {@link #onBindViewHolder(RecyclerView.ViewHolder, int, List)} instead.
     */
    private void bindTrackListHolder(RecyclerView.ViewHolder holder, Music music) {
        TrackListHolder trackListHolder = (TrackListHolder) holder;
        boolean playable = music.vendorCount > 0;
        ViewUtils.setVisibleOrGone(trackListHolder.playAllButton, playable);
        if (playable) {
            trackListHolder.playAllButton.setOnClickListener(view -> PlayMusicService.start(
                    music, view.getContext()));
        }
        ViewUtils.setVisibleOrGone(trackListHolder.trackList, !music.tracks.isEmpty());
        TrackListAdapter adapter = (TrackListAdapter) trackListHolder.trackList.getAdapter();
        adapter.setMusic(music);
    }

    /**
     * @deprecated Use {@link #onBindViewHolder(RecyclerView.ViewHolder, int, List)} instead.
     */
    private void bindTrackListHolder(RecyclerView.ViewHolder holder) {
        TrackListHolder trackListHolder = (TrackListHolder) holder;
        TrackListAdapter adapter = (TrackListAdapter) trackListHolder.trackList.getAdapter();
        adapter.notifyItemRangeChanged(0, adapter.getItemCount());
    }

    public void notifyTrackListChanged() {
        int position = Items.TRACK_LIST.ordinal();
        if (position < getItemCount()) {
            notifyItemChanged(position, ObjectUtils.EMPTY_OBJECT);
        }
    }

    public interface Listener extends BaseItemDataAdapter.Listener<Music>,
            TrackListAdapter.Listener {}

    public static class Data {

        public Music music;
        public Rating rating;
        public List<SimpleItemCollection> itemCollectionList;
        public List<SimpleReview> reviewList;
        public List<SimpleItemForumTopic> forumTopicList;
        public List<CollectableItem> recommendationList;
        public List<Doulist> relatedDoulistList;

        public Data(Music music, Rating rating, List<SimpleItemCollection> itemCollectionList,
                    List<SimpleReview> reviewList, List<SimpleItemForumTopic> forumTopicList,
                    List<CollectableItem> recommendationList, List<Doulist> relatedDoulistList) {
            this.music = music;
            this.rating = rating;
            this.itemCollectionList = itemCollectionList;
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
        @BindView(R.id.artists)
        public TextView artistsText;
        @BindView(R.id.detail)
        public TextView detailText;
        @BindView(R.id.genres)
        public TextView genresText;

        public HeaderHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }

    static class TrackListHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.play_all)
        public Button playAllButton;
        @BindView(R.id.track_list)
        public AdapterLinearLayout trackList;

        public TrackListHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
