/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.item.content;

import android.content.Context;

import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.content.RequestResourceWriter;
import me.zhanghai.android.douya.content.ResourceWriterManager;
import me.zhanghai.android.douya.eventbus.ItemCollectionUpdatedEvent;
import me.zhanghai.android.douya.eventbus.ItemCollectionWriteFinishedEvent;
import me.zhanghai.android.douya.eventbus.ItemCollectionWriteStartedEvent;
import me.zhanghai.android.douya.eventbus.EventBusUtils;
import me.zhanghai.android.douya.network.api.ApiError;
import me.zhanghai.android.douya.network.api.ApiRequest;
import me.zhanghai.android.douya.network.api.ApiService;
import me.zhanghai.android.douya.network.api.info.frodo.ItemCollection;
import me.zhanghai.android.douya.network.api.info.frodo.CollectableItem;
import me.zhanghai.android.douya.util.LogUtils;
import me.zhanghai.android.douya.util.ToastUtils;

class VoteItemCollectionWriter
        extends RequestResourceWriter<VoteItemCollectionWriter, ItemCollection> {

    private CollectableItem.Type mItemType;
    private long mItemId;
    private long mItemCollectionId;

    public VoteItemCollectionWriter(CollectableItem.Type itemType, long itemId,
                                    long itemCollectionId,
                                    ResourceWriterManager<VoteItemCollectionWriter> manager) {
        super(manager);

        mItemType = itemType;
        mItemId = itemId;
        mItemCollectionId = itemCollectionId;
    }

    public CollectableItem.Type getItemType() {
        return mItemType;
    }

    public long getItemId() {
        return mItemId;
    }

    public long getItemCollectionId() {
        return mItemCollectionId;
    }

    @Override
    protected ApiRequest<ItemCollection> onCreateRequest() {
        return ApiService.getInstance().voteItemCollection(mItemType, mItemId, mItemCollectionId);
    }

    @Override
    public void onStart() {
        super.onStart();

        EventBusUtils.postAsync(new ItemCollectionWriteStartedEvent(mItemCollectionId, this));
    }

    @Override
    public void onResponse(ItemCollection response) {

        ToastUtils.show(R.string.item_collection_vote_successful, getContext());

        EventBusUtils.postAsync(new ItemCollectionUpdatedEvent(response, this));

        stopSelf();
    }

    @Override
    public void onErrorResponse(ApiError error) {

        LogUtils.e(error.toString());
        Context context = getContext();
        ToastUtils.show(context.getString(R.string.item_collection_vote_failed_format,
                ApiError.getErrorString(error, context)), context);

        // Must notify to reset pending status. Off-screen items also needs to be invalidated.
        EventBusUtils.postAsync(new ItemCollectionWriteFinishedEvent(mItemCollectionId, this));

        stopSelf();
    }
}
