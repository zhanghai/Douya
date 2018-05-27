/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.item.content;

import android.content.Context;
import android.os.Bundle;

import java.util.List;

import me.zhanghai.android.douya.app.TargetedRetainedFragment;
import me.zhanghai.android.douya.doulist.content.ItemRelatedDoulistListResource;
import me.zhanghai.android.douya.network.api.ApiError;
import me.zhanghai.android.douya.network.api.info.frodo.CollectableItem;
import me.zhanghai.android.douya.network.api.info.frodo.Doulist;
import me.zhanghai.android.douya.network.api.info.frodo.ItemAwardItem;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleItemCollection;
import me.zhanghai.android.douya.network.api.info.frodo.Photo;
import me.zhanghai.android.douya.network.api.info.frodo.Rating;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleCelebrity;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleItemForumTopic;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleReview;
import me.zhanghai.android.douya.photo.content.ItemPhotoListResource;
import me.zhanghai.android.douya.review.content.ItemReviewListResource;
import me.zhanghai.android.douya.util.FragmentUtils;

public abstract class BaseItemFragmentResource<SimpleItemType extends CollectableItem,
        ItemType extends SimpleItemType> extends TargetedRetainedFragment
        implements BaseItemResource.Listener<ItemType>, RatingResource.Listener,
        ItemPhotoListResource.Listener, CelebrityListResource.Listener,
        ItemAwardListResource.Listener, ItemCollectionListResource.Listener,
        GameGuideListResource.Listener, ItemReviewListResource.Listener,
        ItemForumTopicListResource.Listener, ItemRecommendationListResource.Listener,
        ItemRelatedDoulistListResource.Listener {

    private final String KEY_PREFIX = getClass().getName() + '.';

    private final String EXTRA_ITEM_ID = KEY_PREFIX + "item_id";
    private final String EXTRA_SIMPLE_ITEM = KEY_PREFIX + "simple_item";
    private final String EXTRA_ITEM = KEY_PREFIX + "item";

    private static final int ITEM_ID_INVALID = -1;

    private long mItemId = ITEM_ID_INVALID;
    private SimpleItemType mSimpleItem;

    private ItemType mItem;

    private BaseItemResource<SimpleItemType, ItemType> mItemResource;
    private RatingResource mRatingResource;
    private ItemPhotoListResource mPhotoListResource;
    private CelebrityListResource mCelebrityListResource;
    private ItemAwardListResource mAwardListResource;
    private ItemCollectionListResource mItemCollectionListResource;
    private GameGuideListResource mGameGuideListResource;
    private ItemReviewListResource mReviewListResource;
    private ItemForumTopicListResource mForumTopicListResource;
    private ItemRecommendationListResource mRecommendationListResource;
    private ItemRelatedDoulistListResource mRelatedDoulistListResource;

    private boolean mHasError;

    protected BaseItemFragmentResource setArguments(long itemId, SimpleItemType simpleItem,
                                                    ItemType item) {
        FragmentUtils.getArgumentsBuilder(this)
                .putLong(EXTRA_ITEM_ID, itemId)
                .putParcelable(EXTRA_SIMPLE_ITEM, simpleItem)
                .putParcelable(EXTRA_ITEM, item);
        return this;
    }

    public long getItemId() {
        // Can be called before onCreate() is called.
        ensureArguments();
        return mItemId;
    }

    public SimpleItemType getSimpleItem() {
        // Can be called before onCreate() is called.
        ensureArguments();
        return mSimpleItem;
    }

    public boolean hasSimpleItem() {
        // Can be called before onCreate() is called.
        ensureArguments();
        return mSimpleItem != null;
    }

    public ItemType getItem() {
        // Can be called before onCreate() is called.
        ensureArguments();
        return mItem;
    }

    public boolean hasItem() {
        // Can be called before onCreate() is called.
        ensureArguments();
        return mItem != null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        ensureResourcesTarget();
    }

    private void ensureResourcesTarget() {
        if (mItemResource != null) {
            mItemResource.setTarget(this);
        }
        if (mRatingResource != null) {
            mRatingResource.setTarget(this);
        }
        if (mPhotoListResource != null) {
            mPhotoListResource.setTarget(this);
        }
        if (mCelebrityListResource != null) {
            mCelebrityListResource.setTarget(this);
        }
        if (mAwardListResource != null) {
            mAwardListResource.setTarget(this);
        }
        if (mItemCollectionListResource != null) {
            mItemCollectionListResource.setTarget(this);
        }
        if (mGameGuideListResource != null) {
            mGameGuideListResource.setTarget(this);
        }
        if (mReviewListResource != null) {
            mReviewListResource.setTarget(this);
        }
        if (mForumTopicListResource != null) {
            mForumTopicListResource.setTarget(this);
        }
        if (mRecommendationListResource != null) {
            mRecommendationListResource.setTarget(this);
        }
        if (mRelatedDoulistListResource != null) {
            mRelatedDoulistListResource.setTarget(this);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ensureArguments();

        mItemResource = onAttachItemResource(mItemId, mSimpleItem, mItem);
        CollectableItem.Type itemType = getItemType();
        mRatingResource = RatingResource.attachTo(itemType, mItemId, this);
        if (hasPhotoList()) {
            mPhotoListResource = ItemPhotoListResource.attachTo(itemType, mItemId, this);
        }
        if (hasCelebrityList()) {
            mCelebrityListResource = CelebrityListResource.attachTo(itemType, mItemId, this);
        }
        if (hasAwardList()) {
            mAwardListResource = ItemAwardListResource.attachTo(itemType, mItemId, this);
        }
        mItemCollectionListResource = ItemCollectionListResource.attachTo(itemType, mItemId, true,
                this);
        if (hasGameGuideList()) {
            mGameGuideListResource = GameGuideListResource.attachTo(mItemId, this);
        }
        mReviewListResource = ItemReviewListResource.attachTo(itemType, mItemId, this);
        if (hasForumTopicList()) {
            mForumTopicListResource = ItemForumTopicListResource.attachTo(itemType, mItemId, null,
                    this);
        }
        if (hasRecommendationList()) {
            mRecommendationListResource = ItemRecommendationListResource.attachTo(itemType, mItemId,
                    this);
        }
        mRelatedDoulistListResource = ItemRelatedDoulistListResource.attachTo(itemType, mItemId,
                this);
    }

    private void ensureArguments() {
        if (mItemId != ITEM_ID_INVALID) {
            return;
        }
        Bundle arguments = getArguments();
        mItem = arguments.getParcelable(EXTRA_ITEM);
        if (mItem != null) {
            mSimpleItem = mItem;
            mItemId = mItem.id;
        } else {
            mSimpleItem = arguments.getParcelable(EXTRA_SIMPLE_ITEM);
            if (mSimpleItem != null) {
                mItemId = mSimpleItem.id;
            } else {
                mItemId = arguments.getLong(EXTRA_ITEM_ID);
            }
        }
    }

    protected abstract BaseItemResource<SimpleItemType, ItemType> onAttachItemResource(
            long itemId, SimpleItemType simpleItem, ItemType item);

    private CollectableItem.Type getItemType() {
        // Try our best for Movie/TV types.
        if (hasItem()) {
            return getItem().getType();
        } else if (hasSimpleItem()) {
            return getSimpleItem().getType();
        } else {
            return getDefaultItemType();
        }
    }

    protected abstract CollectableItem.Type getDefaultItemType();

    protected abstract boolean hasPhotoList();

    protected abstract boolean hasCelebrityList();

    protected abstract boolean hasAwardList();

    protected boolean hasGameGuideList() {
        return getItemType() == CollectableItem.Type.GAME;
    }

    protected boolean hasForumTopicList() {
        // TODO: Currently there's no forum for game.
        return getItemType() != CollectableItem.Type.GAME;
    }

    protected boolean hasRecommendationList() {
        // TODO: Frodo API currently returns 5xx for recommendation list.
        return getItemType() != CollectableItem.Type.GAME;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mItemResource != null) {
            mItemResource.detach();
        }
        if (mRatingResource != null) {
            mRatingResource.detach();
        }
        if (mPhotoListResource != null) {
            mPhotoListResource.detach();
        }
        if (mCelebrityListResource != null) {
            mCelebrityListResource.detach();
        }
        if (mAwardListResource != null) {
            mAwardListResource.detach();
        }
        if (mItemCollectionListResource != null) {
            mItemCollectionListResource.detach();
        }
        if (mGameGuideListResource != null) {
            mGameGuideListResource.detach();
        }
        if (mReviewListResource != null) {
            mReviewListResource.detach();
        }
        if (mForumTopicListResource != null) {
            mForumTopicListResource.detach();
        }
        if (mRecommendationListResource != null) {
            mRecommendationListResource.detach();
        }
        if (mRelatedDoulistListResource != null) {
            mRelatedDoulistListResource.detach();
        }

        Bundle arguments = getArguments();
        arguments.putLong(EXTRA_ITEM_ID, mItemId);
        arguments.putParcelable(EXTRA_SIMPLE_ITEM, mSimpleItem);
        arguments.putParcelable(EXTRA_ITEM, mItem);
    }

    @Override
    public void onLoadItemStarted(int requestCode) {}

    @Override
    public void onLoadItemFinished(int requestCode) {}

    @Override
    public void onLoadItemError(int requestCode, ApiError error) {
        notifyError(error);
    }

    @Override
    public void onItemChanged(int requestCode, ItemType newItem) {
        mItem = newItem;
        mSimpleItem = newItem;
        mItemId = newItem.id;
        getListener().onItemChanged(getRequestCode(), newItem);
        notifyChanged();
    }

    // TODO: Item collection.
    //@Override
    //public void onItemWriteStarted(int requestCode) {
    //    getListener().onItemWriteStarted(getRequestCode());
    //}
    //
    //@Override
    //public void onItemWriteFinished(int requestCode) {
    //    getListener().onItemWriteFinished(getRequestCode());
    //}

    @Override
    public void onLoadRatingStarted(int requestCode) {}

    @Override
    public void onLoadRatingFinished(int requestCode) {}

    @Override
    public void onLoadRatingError(int requestCode, ApiError error) {
        notifyError(error);
    }

    @Override
    public void onRatingChanged(int requestCode, Rating newRating) {
        notifyChanged();
    }

    @Override
    public void onLoadPhotoListStarted(int requestCode) {}

    @Override
    public void onLoadPhotoListFinished(int requestCode) {}

    @Override
    public void onLoadPhotoListError(int requestCode, ApiError error) {
        notifyError(error);
    }

    @Override
    public void onPhotoListChanged(int requestCode, List<Photo> newPhotoList) {
        notifyChanged();
    }

    @Override
    public void onPhotoListAppended(int requestCode, List<Photo> appendedPhotoList) {
        notifyChanged();
    }

    @Override
    public void onPhotoRemoved(int requestCode, int position) {
        notifyChanged();
    }

    @Override
    public void onLoadCelebrityListStarted(int requestCode) {}

    @Override
    public void onLoadCelebrityListFinished(int requestCode) {}

    @Override
    public void onLoadCelebrityListError(int requestCode, ApiError error) {
        notifyError(error);
    }

    @Override
    public void onCelebrityListChanged(int requestCode, List<SimpleCelebrity> newCelebrityList) {
        notifyChanged();
    }

    @Override
    public void onLoadAwardListStarted(int requestCode) {}

    @Override
    public void onLoadAwardListFinished(int requestCode) {}

    @Override
    public void onLoadAwardListError(int requestCode, ApiError error) {
        notifyError(error);
    }

    @Override
    public void onAwardListChanged(int requestCode, List<ItemAwardItem> newAwardList) {
        notifyChanged();
    }

    @Override
    public void onAwardListAppended(int requestCode, List<ItemAwardItem> appendedAwardList) {
        notifyChanged();
    }

    @Override
    public void onLoadItemCollectionListStarted(int requestCode) {}

    @Override
    public void onLoadItemCollectionListFinished(int requestCode) {}

    @Override
    public void onLoadItemCollectionListError(int requestCode, ApiError error) {
        notifyError(error);
    }

    @Override
    public void onItemCollectionListChanged(int requestCode,
                                            List<SimpleItemCollection> newItemCollectionList) {
        notifyChanged();
    }

    @Override
    public void onItemCollectionListAppended(
            int requestCode, List<SimpleItemCollection> appendedItemCollectionList) {
        notifyChanged();
    }

    @Override
    public void onItemCollectionListItemChanged(int requestCode, int position,
                                                SimpleItemCollection newItemCollection) {
        getListener().onItemCollectionListItemChanged(getRequestCode(), position, newItemCollection);
    }

    @Override
    public void onItemCollectionListItemWriteStarted(int requestCode, int position) {
        getListener().onItemCollectionListItemWriteStarted(getRequestCode(), position);
    }

    @Override
    public void onItemCollectionListItemWriteFinished(int requestCode, int position) {
        getListener().onItemCollectionListItemWriteFinished(getRequestCode(), position);
    }

    @Override
    public void onLoadGameGuideListStarted(int requestCode) {}

    @Override
    public void onLoadGameGuideListFinished(int requestCode) {}

    @Override
    public void onLoadGameGuideListError(int requestCode, ApiError error) {
        notifyError(error);
    }

    @Override
    public void onGameGuideListChanged(int requestCode, List<SimpleReview> newGameGuideList) {
        notifyChanged();
    }

    @Override
    public void onGameGuideListAppended(int requestCode, List<SimpleReview> appendedGameGuideList) {
        notifyChanged();
    }

    @Override
    public void onGameGuideChanged(int requestCode, int position, SimpleReview newGameGuide) {
        notifyChanged();
    }

    @Override
    public void onGameGuideRemoved(int requestCode, int position) {
        notifyChanged();
    }

    @Override
    public void onLoadReviewListStarted(int requestCode) {}

    @Override
    public void onLoadReviewListFinished(int requestCode) {}

    @Override
    public void onLoadReviewListError(int requestCode, ApiError error) {
        notifyError(error);
    }

    @Override
    public void onReviewListChanged(int requestCode, List<SimpleReview> newReviewList) {
        notifyChanged();
    }

    @Override
    public void onReviewListAppended(int requestCode, List<SimpleReview> appendedReviewList) {
        notifyChanged();
    }

    @Override
    public void onReviewChanged(int requestCode, int position, SimpleReview newReview) {
        notifyChanged();
    }

    @Override
    public void onReviewRemoved(int requestCode, int position) {
        notifyChanged();
    }

    @Override
    public void onLoadForumTopicListStarted(int requestCode) {}

    @Override
    public void onLoadForumTopicListFinished(int requestCode) {}

    @Override
    public void onLoadForumTopicListError(int requestCode, ApiError error) {
        notifyError(error);
    }

    @Override
    public void onForumTopicListChanged(int requestCode,
                                        List<SimpleItemForumTopic> newForumTopicList) {
        notifyChanged();
    }

    @Override
    public void onForumTopicListAppended(int requestCode,
                                         List<SimpleItemForumTopic> appendedForumTopicList) {
        notifyChanged();
    }

    @Override
    public void onLoadRecommendationListStarted(int requestCode) {}

    @Override
    public void onLoadRecommendationListFinished(int requestCode) {}

    @Override
    public void onLoadRecommendationListError(int requestCode, ApiError error) {
        notifyError(error);
    }

    @Override
    public void onRecommendationListChanged(int requestCode,
                                            List<CollectableItem> newRecommendationList) {
        notifyChanged();
    }

    @Override
    public void onLoadDoulistListStarted(int requestCode) {}

    @Override
    public void onLoadDoulistListFinished(int requestCode) {}

    @Override
    public void onLoadDoulistListError(int requestCode, ApiError error) {
        notifyError(error);
    }

    @Override
    public void onDoulistListChanged(int requestCode, List<Doulist> newDoulistList) {
        notifyChanged();
    }

    @Override
    public void onDoulistListAppended(int requestCode, List<Doulist> appendedDoulistList) {
        notifyChanged();
    }

    @Override
    public void onDoulistChanged(int requestCode, int position, Doulist newDoulist) {
        notifyChanged();
    }

    @Override
    public void onDoulistRemoved(int requestCode, int position) {
        notifyChanged();
    }

    public boolean isAnyLoaded() {
        // Can be called before onCreate().
        return hasItem()
                || (mRatingResource != null && mRatingResource.has())
                || (mPhotoListResource != null && mPhotoListResource.has())
                || (mCelebrityListResource != null && mCelebrityListResource.has())
                || (mAwardListResource != null && mAwardListResource.has())
                || (mItemCollectionListResource != null && mItemCollectionListResource.has())
                || (mGameGuideListResource != null && mGameGuideListResource.has())
                || (mReviewListResource != null && mReviewListResource.has())
                || (mForumTopicListResource != null && mForumTopicListResource.has())
                || (mRecommendationListResource != null && mRecommendationListResource.has())
                || (mRelatedDoulistListResource != null && mRelatedDoulistListResource.has());
    }

    public void notifyChanged() {
        // HACK: Add SimpleRating to Rating.
        ItemType item = getItem();
        Rating rating = mRatingResource != null ? mRatingResource.get() : null;
        if (rating != null && item != null) {
            rating.rating = item.rating;
            //noinspection deprecation
            rating.ratingUnavailableReason = item.ratingUnavailableReason;
        }
        notifyChanged(getRequestCode(),
                item,
                rating,
                mPhotoListResource != null ? mPhotoListResource.get() : null,
                mCelebrityListResource != null ? mCelebrityListResource.get() : null,
                mAwardListResource != null ? mAwardListResource.get() : null,
                mItemCollectionListResource != null ? mItemCollectionListResource.get() : null,
                mGameGuideListResource != null ? mGameGuideListResource.get() : null,
                mReviewListResource != null ? mReviewListResource.get() : null,
                mForumTopicListResource != null ? mForumTopicListResource.get() : null,
                mRecommendationListResource != null ? mRecommendationListResource.get() : null,
                mRelatedDoulistListResource != null ? mRelatedDoulistListResource.get() : null);
    }

    protected abstract void notifyChanged(int requestCode, ItemType newItem, Rating newRating,
                                          List<Photo> newPhotoList,
                                          List<SimpleCelebrity> newCelebrityList,
                                          List<ItemAwardItem> newAwardList,
                                          List<SimpleItemCollection> newItemCollectionList,
                                          List<SimpleReview> newGameGuideList,
                                          List<SimpleReview> newReviewList,
                                          List<SimpleItemForumTopic> newForumTopicList,
                                          List<CollectableItem> newRecommendationList,
                                          List<Doulist> newRelatedDoulistList);

    private void notifyError(ApiError error) {
        if (!mHasError) {
            mHasError = true;
            getListener().onLoadError(getRequestCode(), error);
        }
    }

    @Override
    public void onItemCollectionChanged(int requestCode) {
        getListener().onItemCollectionChanged(getRequestCode());
    }

    private Listener<ItemType> getListener() {
        //noinspection unchecked
        return (Listener<ItemType>) getTarget();
    }

    public interface Listener<ItemType> {
        void onLoadError(int requestCode, ApiError error);
        void onItemChanged(int requestCode, ItemType newItem);
        void onItemCollectionChanged(int requestCode);
        void onItemCollectionListItemChanged(int requestCode, int position,
                                             SimpleItemCollection newItemCollection);
        void onItemCollectionListItemWriteStarted(int requestCode, int position);
        void onItemCollectionListItemWriteFinished(int requestCode, int position);
    }
}
