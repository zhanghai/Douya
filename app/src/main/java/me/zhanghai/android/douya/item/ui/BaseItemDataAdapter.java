/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.item.ui;

import android.content.Context;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.Space;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.gallery.ui.GalleryActivity;
import me.zhanghai.android.douya.link.UriHandler;
import me.zhanghai.android.douya.network.api.info.frodo.CollectableItem;
import me.zhanghai.android.douya.network.api.info.frodo.Doulist;
import me.zhanghai.android.douya.network.api.info.frodo.ItemAwardItem;
import me.zhanghai.android.douya.network.api.info.frodo.ItemCollectionState;
import me.zhanghai.android.douya.network.api.info.frodo.Photo;
import me.zhanghai.android.douya.network.api.info.frodo.Rating;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleCelebrity;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleItemCollection;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleItemForumTopic;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleReview;
import me.zhanghai.android.douya.ui.AdapterLinearLayout;
import me.zhanghai.android.douya.ui.BarrierDataAdapter;
import me.zhanghai.android.douya.ui.DividerItemDecoration;
import me.zhanghai.android.douya.ui.HorizontalImageAdapter;
import me.zhanghai.android.douya.util.CollectionUtils;
import me.zhanghai.android.douya.util.RecyclerViewUtils;
import me.zhanghai.android.douya.util.TimeUtils;
import me.zhanghai.android.douya.util.ViewUtils;

public abstract class BaseItemDataAdapter<T extends CollectableItem>
        extends BarrierDataAdapter<RecyclerView.ViewHolder> {

    private static final int ITEM_COLLECTION_LIST_MAX_SIZE = 5;
    private static final int ITEM_REVIEW_LIST_MAX_SIZE = 5;
    private static final int ITEM_FORUM_TOPIC_LIST_MAX_SIZE = 5;
    private static final int ITEM_RELATED_DOULIST_LIST_MAX_SIZE = 5;

    private Listener<T> mListener;

    public BaseItemDataAdapter(Listener<T> listener) {
        mListener = listener;
    }

    protected Listener<T> getListener() {
        return mListener;
    }

    protected ItemCollectionHolder createItemCollectionHolder(ViewGroup parent) {
        ItemCollectionHolder holder = new ItemCollectionHolder(ViewUtils.inflate(
                R.layout.item_fragment_collection, parent));
        holder.menu = new PopupMenu(RecyclerViewUtils.getContext(holder), holder.menuButton);
        holder.menu.inflate(R.menu.item_collection_actions);
        holder.menuButton.setOnClickListener(view -> holder.menu.show());
        holder.menuButton.setOnTouchListener(holder.menu.getDragToOpenListener());
        return holder;
    }

    protected BadgeListHolder createBadgeListHolder(ViewGroup parent) {
        return new BadgeListHolder(ViewUtils.inflate(R.layout.item_fragment_badge_list, parent));
    }

    protected IntroductionHolder createIntroductionHolder(ViewGroup parent) {
        return new IntroductionHolder(ViewUtils.inflate(R.layout.item_fragment_introduction,
                parent));
    }

    protected PhotoListHolder createPhotoListHolder(ViewGroup parent) {
        PhotoListHolder holder = new PhotoListHolder(ViewUtils.inflate(
                R.layout.item_fragment_photo_list, parent));
        holder.photoList.setHasFixedSize(true);
        holder.photoList.setLayoutManager(new LinearLayoutManager(parent.getContext(),
                LinearLayoutManager.HORIZONTAL, false));
        holder.photoList.addItemDecoration(new DividerItemDecoration(
                DividerItemDecoration.HORIZONTAL, R.drawable.transparent_divider_vertical_4dp,
                holder.photoList.getContext()));
        holder.photoList.setAdapter(new HorizontalImageAdapter());
        return holder;
    }

    protected CelebrityListHolder createCelebrityListHolder(ViewGroup parent) {
        CelebrityListHolder holder = new CelebrityListHolder(ViewUtils.inflate(
                R.layout.item_fragment_celebrity_list, parent));
        holder.celebrityList.setHasFixedSize(true);
        holder.celebrityList.setLayoutManager(new LinearLayoutManager(parent.getContext(),
                LinearLayoutManager.HORIZONTAL, false));
        holder.celebrityList.addItemDecoration(new DividerItemDecoration(
                DividerItemDecoration.HORIZONTAL, R.drawable.transparent_divider_vertical_16dp,
                holder.celebrityList.getContext()));
        holder.celebrityList.setAdapter(new CelebrityListAdapter());
        return holder;
    }

    protected AwardListHolder createAwardListHolder(ViewGroup parent) {
        AwardListHolder holder = new AwardListHolder(ViewUtils.inflate(
                R.layout.item_fragment_award_list, parent));
        holder.awardList.setHasFixedSize(true);
        holder.awardList.setLayoutManager(new LinearLayoutManager(parent.getContext(),
                LinearLayoutManager.HORIZONTAL, false));
        holder.awardList.addItemDecoration(new DividerItemDecoration(
                DividerItemDecoration.HORIZONTAL, R.drawable.transparent_divider_vertical_16dp,
                holder.awardList.getContext()));
        holder.awardList.setAdapter(new ItemAwardListAdapter());
        return holder;
    }

    protected RatingHolder createRatingHolder(ViewGroup parent) {
        RatingHolder holder = new RatingHolder(ViewUtils.inflate(R.layout.item_fragment_rating,
                parent));
        holder.ratingDistributionLayout.setCompact(true);
        return holder;
    }

    protected ItemCollectionListHolder createItemCollectionListHolder(ViewGroup parent) {
        ItemCollectionListHolder holder = new ItemCollectionListHolder(ViewUtils.inflate(
                R.layout.item_fragment_collection_list, parent));
        holder.itemCollectionList.setAdapter(new ItemCollectionListAdapter(mListener));
        return holder;
    }

    protected ReviewListHolder createReviewListHolder(ViewGroup parent) {
        ReviewListHolder holder = new ReviewListHolder(ViewUtils.inflate(
                R.layout.item_fragment_review_list, parent));
        holder.reviewList.setAdapter(new ReviewListAdapter());
        return holder;
    }

    protected ForumTopicListHolder createForumTopicListHolder(ViewGroup parent) {
        ForumTopicListHolder holder = new ForumTopicListHolder(ViewUtils.inflate(
                R.layout.item_fragment_forum_topic_list, parent));
        holder.forumTopicList.setAdapter(new ItemForumTopicListAdapter());
        return holder;
    }

    protected RecommendationListHolder createRecommendationListHolder(ViewGroup parent) {
        RecommendationListHolder holder = new RecommendationListHolder(ViewUtils.inflate(
                R.layout.item_fragment_recommendation_list, parent));
        holder.recommendationList.setHasFixedSize(true);
        holder.recommendationList.setLayoutManager(new LinearLayoutManager(
                parent.getContext(), LinearLayoutManager.HORIZONTAL, false));
        holder.recommendationList.addItemDecoration(new DividerItemDecoration(
                DividerItemDecoration.HORIZONTAL, R.drawable.transparent_divider_vertical_16dp,
                holder.recommendationList.getContext()));
        holder.recommendationList.setAdapter(new RecommendationListAdapter());
        return holder;
    }

    protected RelatedDoulistListHolder createRelatedDoulistListHolder(ViewGroup parent) {
        RelatedDoulistListHolder holder = new RelatedDoulistListHolder(ViewUtils.inflate(
                R.layout.item_fragment_related_doulist_list, parent));
        holder.relatedDoulistList.setAdapter(new ItemRelatedDoulistListAdapter());
        return holder;
    }

    /**
     * @deprecated Use {@link #onBindViewHolder(RecyclerView.ViewHolder, int, List)} instead.
     */
    @CallSuper
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        throw new UnsupportedOperationException();
    }

    @CallSuper
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position,
                                 @NonNull List<Object> payloads) {
        // HACK: Make sure we don't click through any view to our backdrop.
        holder.itemView.setClickable(true);
    }

    protected void bindItemCollectionHolder(RecyclerView.ViewHolder holder, T item) {
        ItemCollectionHolder itemCollectionHolder = (ItemCollectionHolder) holder;
        CollectableItem.Type type = item.getType();
        itemCollectionHolder.todoButton.setText(ItemCollectionState.TODO.getString(type,
                itemCollectionHolder.todoButton.getContext()));
        itemCollectionHolder.doingButton.setText(ItemCollectionState.DOING.getString(type,
                itemCollectionHolder.doingButton.getContext()));
        itemCollectionHolder.doneButton.setText(ItemCollectionState.DONE.getString(type,
                itemCollectionHolder.doneButton.getContext()));
        SimpleItemCollection itemCollection = item.collection;
        ItemCollectionState state = itemCollection != null ? itemCollection.getState() : null;
        boolean todoVisible = itemCollection == null;
        ViewUtils.setVisibleOrGone(itemCollectionHolder.todoButton, todoVisible);
        itemCollectionHolder.todoButton.setOnClickListener(view -> {
            Context context = view.getContext();
            context.startActivity(ItemCollectionActivity.makeIntent(item, ItemCollectionState.TODO,
                    context));
        });
        boolean doingVisible = item.getType().hasDoingState() && (itemCollection == null
                || state == ItemCollectionState.TODO);
        ViewUtils.setVisibleOrGone(itemCollectionHolder.doingButton, doingVisible);
        itemCollectionHolder.doingButton.setOnClickListener(view -> {
            Context context = view.getContext();
            context.startActivity(ItemCollectionActivity.makeIntent(item, ItemCollectionState.DOING,
                    context));
        });
        boolean doneVisible = itemCollection == null || state == ItemCollectionState.TODO
                || state == ItemCollectionState.DOING;
        ViewUtils.setVisibleOrGone(itemCollectionHolder.doneButton, doneVisible);
        itemCollectionHolder.doneButton.setOnClickListener(view -> {
            Context context = view.getContext();
            context.startActivity(ItemCollectionActivity.makeIntent(item, ItemCollectionState.DONE,
                    context));
        });
        boolean buttonBarVisible = todoVisible || doingVisible || doneVisible;
        ViewUtils.setVisibleOrGone(itemCollectionHolder.buttonBar, buttonBarVisible);
        ViewUtils.setVisibleOrGone(itemCollectionHolder.buttonBarSpace, !buttonBarVisible);
        boolean hasItemCollection = itemCollection != null;
        ViewUtils.setVisibleOrGone(itemCollectionHolder.itemCollectionLayout, hasItemCollection);
        if (hasItemCollection) {
            itemCollectionHolder.stateText.setText(state.getString(item.getType(),
                    itemCollectionHolder.stateText.getContext()));
            boolean hasRating = itemCollection.rating != null;
            ViewUtils.setVisibleOrGone(itemCollectionHolder.ratingBar, hasRating);
            if (hasRating) {
                itemCollectionHolder.ratingBar.setRating(
                        itemCollection.rating.getRatingBarRating());
            }
            itemCollectionHolder.dateText.setText(TimeUtils.formatDate(
                    TimeUtils.parseDoubanDateTime(itemCollection.createTime),
                    itemCollectionHolder.dateText.getContext()));
            itemCollectionHolder.commentText.setText(itemCollection.comment);
            itemCollectionHolder.itemCollectionLayout.setOnClickListener(view -> {
                Context context = view.getContext();
                context.startActivity(ItemCollectionActivity.makeIntent(item, context));
            });
            itemCollectionHolder.menu.setOnMenuItemClickListener(menuItem -> {
                switch (menuItem.getItemId()) {
                    case R.id.action_edit: {
                        Context context = RecyclerViewUtils.getContext(holder);
                        context.startActivity(ItemCollectionActivity.makeIntent(item, context));
                        return true;
                    }
                    case R.id.action_uncollect:
                        mListener.onUncollectItem(item);
                        return true;
                    default:
                        return false;
                }
            });
        }
        ViewUtils.setVisibleOrGone(itemCollectionHolder.dividerView, !hasItemCollection);
    }

    protected void bindIntroductionHolder(RecyclerView.ViewHolder holder, T item) {
        IntroductionHolder introductionHolder = (IntroductionHolder) holder;
        introductionHolder.introductionText.setText(!TextUtils.isEmpty(item.introduction) ?
                item.introduction : introductionHolder.introductionText.getContext().getString(
                R.string.item_introduction_empty));
    }

    protected void bindPhotoListHolder(RecyclerView.ViewHolder holder, T item,
                                       List<Photo> photoList, boolean excludeFirstPhoto) {
        PhotoListHolder photoListHolder = (PhotoListHolder) holder;
        List<Photo> originalPhotoList = photoList;
        if (excludeFirstPhoto) {
            photoList = photoList.subList(1, photoList.size());
        }
        ViewUtils.setVisibleOrGone(photoListHolder.photoList, !photoList.isEmpty());
        HorizontalImageAdapter adapter = (HorizontalImageAdapter)
                photoListHolder.photoList.getAdapter();
        adapter.replace(photoList);
        Context context = RecyclerViewUtils.getContext(holder);
        adapter.setOnItemClickListener((parent, itemView, item_, photoPosition) -> {
            if (excludeFirstPhoto) {
                ++photoPosition;
            }
            // TODO: Use PhotoAlbumGalleryActivity instead.
            context.startActivity(GalleryActivity.makeImageListIntent(originalPhotoList,
                    photoPosition, context));
        });
        photoListHolder.viewMoreButton.setOnClickListener(view -> {
            // TODO
            UriHandler.open(item.url + "photos", context);
        });
    }

    protected void bindCelebrityListHolder(RecyclerView.ViewHolder holder, T item,
                                           List<SimpleCelebrity> celebrityList) {
        CelebrityListHolder celebrityListHolder = (CelebrityListHolder) holder;
        CelebrityListAdapter adapter = (CelebrityListAdapter)
                celebrityListHolder.celebrityList.getAdapter();
        adapter.replace(celebrityList);
    }

    protected void bindAwardListHolder(RecyclerView.ViewHolder holder, T item,
                                       List<ItemAwardItem> awardList) {
        AwardListHolder awardListHolder = (AwardListHolder) holder;
        ViewUtils.setVisibleOrGone(awardListHolder.awardList, !awardList.isEmpty());
        ItemAwardListAdapter adapter = (ItemAwardListAdapter)
                awardListHolder.awardList.getAdapter();
        adapter.replace(awardList);
        awardListHolder.itemView.setOnClickListener(view -> {
            // TODO
            UriHandler.open(item.url + "awards/", view.getContext());
        });
    }

    protected void bindRatingHolder(RecyclerView.ViewHolder holder, T item, Rating rating) {
        RatingHolder ratingHolder = (RatingHolder) holder;
        boolean hasRating = rating.hasRating();
        ViewUtils.setVisibleOrGone(ratingHolder.ratingWrapperLayout, hasRating);
        if (hasRating) {
            ratingHolder.ratingLayout.setRating(rating.rating);
            ratingHolder.ratingDistributionLayout.setRating(rating);
            ratingHolder.itemView.setOnClickListener(view -> {
                UriHandler.open(item.url + "collections", view.getContext());
            });
        }
    }

    protected void bindItemCollectionListHolder(RecyclerView.ViewHolder holder, T item,
                                                List<SimpleItemCollection> itemCollectionList,
                                                @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            bindItemCollectionListHolder(holder, item, itemCollectionList);
        } else {
            //noinspection unchecked
            for (List<Object> payload : (List<List<Object>>) (Object) payloads) {
                int position = (int) payload.get(0);
                SimpleItemCollection newItemCollection = (SimpleItemCollection)
                        CollectionUtils.getOrNull(payload, 1);
                bindItemCollectionListHolder(holder, position, newItemCollection);
            }
        }
    }

    private void bindItemCollectionListHolder(RecyclerView.ViewHolder holder, T item,
                                              List<SimpleItemCollection> itemCollectionList) {
        ItemCollectionListHolder itemCollectionListHolder = (ItemCollectionListHolder) holder;
        itemCollectionListHolder.createButton.setOnClickListener(view -> {
            Context context = view.getContext();
            context.startActivity(ItemCollectionActivity.makeIntent(item, context));
        });
        itemCollectionList = itemCollectionList.subList(0, Math.min(ITEM_COLLECTION_LIST_MAX_SIZE,
                itemCollectionList.size()));
        ViewUtils.setVisibleOrGone(itemCollectionListHolder.itemCollectionList,
                !itemCollectionList.isEmpty());
        ItemCollectionListAdapter adapter = (ItemCollectionListAdapter)
                itemCollectionListHolder.itemCollectionList.getAdapter();
        adapter.setItem(item);
        adapter.replace(itemCollectionList);
        itemCollectionListHolder.viewMoreButton.setOnClickListener(view -> {
            // TODO
            UriHandler.open(item.url + "collections", view.getContext());
        });
    }

    private void bindItemCollectionListHolder(RecyclerView.ViewHolder holder, int position,
                                              SimpleItemCollection newItemCollection) {
        ItemCollectionListHolder itemCollectionListHolder = (ItemCollectionListHolder) holder;
        ItemCollectionListAdapter adapter = (ItemCollectionListAdapter)
                itemCollectionListHolder.itemCollectionList.getAdapter();
        if (newItemCollection != null) {
            adapter.set(position, newItemCollection);
        } else {
            adapter.notifyItemChanged(position);
        }
    }

    protected void notifyItemCollectionListItemChanged(int position, int itemCollectionPosition,
                                                       SimpleItemCollection newItemCollection) {
        if (position < getItemCount()) {
            notifyItemChanged(position, Arrays.asList(itemCollectionPosition, newItemCollection));
        }
    }

    private void bindReviewListHolder(RecyclerView.ViewHolder holder, T item,
                                      List<SimpleReview> reviewList,
                                      View.OnClickListener onCreateListener,
                                      View.OnClickListener onViewMoreListener) {
        ReviewListHolder reviewListHolder = (ReviewListHolder) holder;
        reviewListHolder.createButton.setOnClickListener(onCreateListener);
        reviewList = reviewList.subList(0, Math.min(ITEM_REVIEW_LIST_MAX_SIZE, reviewList.size()));
        ViewUtils.setVisibleOrGone(reviewListHolder.reviewList, !reviewList.isEmpty());
        ReviewListAdapter adapter = (ReviewListAdapter)
                reviewListHolder.reviewList.getAdapter();
        adapter.replace(reviewList);
        reviewListHolder.viewMoreButton.setOnClickListener(onViewMoreListener);
    }

    protected void bindReviewListHolder(RecyclerView.ViewHolder holder, T item,
                                        List<SimpleReview> reviewList) {
        bindReviewListHolder(holder, item, reviewList, view -> {
            // TODO
            UriHandler.open(item.url + "new_review", view.getContext());
        }, view -> {
            // TODO
            UriHandler.open(item.url + "reviews", view.getContext());
        });
    }

    protected void bindReviewListHolder(RecyclerView.ViewHolder holder, T item,
                                        List<SimpleReview> reviewList, int titleRes, int createRes,
                                        int viewMoreRes, View.OnClickListener onCreateListener,
                                        View.OnClickListener onViewMoreListener) {
        ReviewListHolder reviewListHolder = (ReviewListHolder) holder;
        reviewListHolder.titleText.setText(titleRes);
        reviewListHolder.createButton.setText(createRes);
        reviewListHolder.viewMoreButton.setText(viewMoreRes);
        bindReviewListHolder(holder, item, reviewList, onCreateListener, onViewMoreListener);
    }

    protected void bindForumTopicListHolder(RecyclerView.ViewHolder holder, T item,
                                            List<SimpleItemForumTopic> forumTopicList) {
        ForumTopicListHolder forumTopicListHolder = (ForumTopicListHolder) holder;
        forumTopicListHolder.createButton.setOnClickListener(view -> {
            // TODO
            UriHandler.open(item.url + "discussion/create", view.getContext());
        });
        forumTopicList = forumTopicList.subList(0, Math.min(ITEM_FORUM_TOPIC_LIST_MAX_SIZE,
                forumTopicList.size()));
        ViewUtils.setVisibleOrGone(forumTopicListHolder.forumTopicList,
                !forumTopicList.isEmpty());
        ItemForumTopicListAdapter adapter = (ItemForumTopicListAdapter)
                forumTopicListHolder.forumTopicList.getAdapter();
        adapter.replace(forumTopicList);
        forumTopicListHolder.viewMoreButton.setOnClickListener(view -> {
            // TODO
            UriHandler.open(item.url + "discussion", view.getContext());
        });
    }

    protected void bindRecommendationListHolder(RecyclerView.ViewHolder holder, T item,
                                                List<CollectableItem> recommendationList) {
        RecommendationListHolder recommendationListHolder = (RecommendationListHolder) holder;
        recommendationListHolder.titleLayout.setOnClickListener(view -> {
            // TODO
        });
        RecommendationListAdapter adapter = (RecommendationListAdapter)
                recommendationListHolder.recommendationList.getAdapter();
        adapter.replace(recommendationList);
    }

    protected void bindRelatedDoulistListHolder(RecyclerView.ViewHolder holder, T item,
                                                List<Doulist> relatedDoulistList) {
        RelatedDoulistListHolder relatedDoulistListHolder =
                (RelatedDoulistListHolder) holder;
        relatedDoulistListHolder.titleLayout.setOnClickListener(view -> {
            // TODO
            UriHandler.open(item.url + "doulists", view.getContext());
        });
        ViewUtils.setVisibleOrGone(relatedDoulistListHolder.relatedDoulistList,
                !relatedDoulistList.isEmpty());
        ItemRelatedDoulistListAdapter adapter = (ItemRelatedDoulistListAdapter)
                relatedDoulistListHolder.relatedDoulistList.getAdapter();
        relatedDoulistList = relatedDoulistList.subList(0, Math.min(
                ITEM_RELATED_DOULIST_LIST_MAX_SIZE, relatedDoulistList.size()));
        adapter.replace(relatedDoulistList);
    }

    public interface Listener<T> extends ItemCollectionListAdapter.Listener {
        void onUncollectItem(T item);
        void copyText(String text);
    }

    protected static class ItemCollectionHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.button_bar)
        public ViewGroup buttonBar;
        @BindView(R.id.todo)
        public Button todoButton;
        @BindView(R.id.doing)
        public Button doingButton;
        @BindView(R.id.done)
        public Button doneButton;
        @BindView(R.id.button_bar_space)
        public Space buttonBarSpace;
        @BindView(R.id.item_collection_layout)
        public ViewGroup itemCollectionLayout;
        @BindView(R.id.state)
        public TextView stateText;
        @BindView(R.id.date)
        public TextView dateText;
        @BindView(R.id.rating)
        public RatingBar ratingBar;
        @BindView(R.id.comment)
        public TextView commentText;
        @BindView(R.id.menu)
        public ImageButton menuButton;
        @BindView(R.id.divider)
        public View dividerView;

        public PopupMenu menu;

        public ItemCollectionHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }

    protected static class BadgeListHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.badge_list_layout)
        public BadgeListLayout badgeListLayout;

        public BadgeListHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }

    protected static class IntroductionHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.introduction_layout)
        public ViewGroup introductionLayout;
        @BindView(R.id.introduction)
        public TextView introductionText;

        public IntroductionHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }

    protected static class CelebrityListHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.celebrity_list)
        public RecyclerView celebrityList;

        public CelebrityListHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }

    protected static class PhotoListHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.photo_list)
        public RecyclerView photoList;
        @BindView(R.id.view_more)
        public Button viewMoreButton;

        public PhotoListHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }

    protected static class AwardListHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.award_list)
        public RecyclerView awardList;

        public AwardListHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }

    protected static class RatingHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.rating_wrapper)
        public ViewGroup ratingWrapperLayout;
        @BindView(R.id.rating_layout)
        public RatingLayout ratingLayout;
        @BindView(R.id.rating_distribution_layout)
        public RatingDistributionLayout ratingDistributionLayout;

        public RatingHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }

    protected static class ItemCollectionListHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.create)
        public Button createButton;
        @BindView(R.id.item_collection_list)
        public AdapterLinearLayout itemCollectionList;
        @BindView(R.id.view_more)
        public Button viewMoreButton;

        public ItemCollectionListHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }

    protected static class ReviewListHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.title)
        public TextView titleText;
        @BindView(R.id.create)
        public Button createButton;
        @BindView(R.id.review_list)
        public AdapterLinearLayout reviewList;
        @BindView(R.id.view_more)
        public Button viewMoreButton;

        public ReviewListHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }

    protected static class ForumTopicListHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.create)
        public Button createButton;
        @BindView(R.id.forum_topic_list)
        public AdapterLinearLayout forumTopicList;
        @BindView(R.id.view_more)
        public Button viewMoreButton;

        public ForumTopicListHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }

    protected static class RecommendationListHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.title_layout)
        public ViewGroup titleLayout;
        @BindView(R.id.recommendation_list)
        public RecyclerView recommendationList;

        public RecommendationListHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }

    protected static class RelatedDoulistListHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.title_layout)
        public ViewGroup titleLayout;
        @BindView(R.id.related_doulist_list)
        public AdapterLinearLayout relatedDoulistList;

        public RelatedDoulistListHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
