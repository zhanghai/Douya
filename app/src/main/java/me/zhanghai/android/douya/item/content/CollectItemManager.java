/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.item.content;

import android.content.Context;

import java.util.List;

import me.zhanghai.android.douya.content.ResourceWriterManager;
import me.zhanghai.android.douya.network.api.info.frodo.CollectableItem;
import me.zhanghai.android.douya.network.api.info.frodo.ItemCollectionState;

public class CollectItemManager extends ResourceWriterManager<CollectItemWriter> {

    private static class InstanceHolder {
        public static final CollectItemManager VALUE = new CollectItemManager();
    }

    public static CollectItemManager getInstance() {
        return InstanceHolder.VALUE;
    }

    public void write(CollectableItem.Type itemType, long itemId, ItemCollectionState state,
                      int rating, List<String> tags, String comment, List<Long> gamePlatformIds,
                      boolean shareToBroadcast, boolean shareToWeibo, boolean shareToWeChatMoments,
                      Context context) {
        add(new CollectItemWriter(itemType, itemId, state, rating, tags, comment, gamePlatformIds,
                shareToBroadcast, shareToWeibo, shareToWeChatMoments, this), context);
    }

    public void write(CollectableItem item, ItemCollectionState state, int rating,
                      List<String> tags, String comment, List<Long> gamePlatformIds,
                      boolean shareToBroadcast, boolean shareToWeibo, boolean shareToWeChatMoments,
                      Context context) {
        add(new CollectItemWriter(item, state, rating, tags, comment, gamePlatformIds,
                shareToBroadcast, shareToWeibo, shareToWeChatMoments, this), context);
    }

    public boolean isWriting(CollectableItem.Type itemType, long itemId) {
        return findWriter(itemType, itemId) != null;
    }

    public boolean isWriting(CollectableItem item) {
        return isWriting(item.getType(), item.id);
    }

    private CollectItemWriter findWriter(CollectableItem.Type itemType, long itemId) {
        for (CollectItemWriter writer : getWriters()) {
            if (writer.getItemType() == itemType && writer.getItemId() == itemId) {
                return writer;
            }
        }
        return null;
    }

    public ItemCollectionState getState(CollectableItem.Type itemType, long itemId) {
        return findWriter(itemType, itemId).getState();
    }

    public ItemCollectionState getState(CollectableItem item) {
        return getState(item.getType(), item.id);
    }

    public int getRating(CollectableItem.Type itemType, long itemId) {
        return findWriter(itemType, itemId).getRating();
    }

    public int getRating(CollectableItem item) {
        return getRating(item.getType(), item.id);
    }

    public List<String> getTags(CollectableItem.Type itemType, long itemId) {
        return findWriter(itemType, itemId).getTags();
    }

    public List<String> getTags(CollectableItem item) {
        return getTags(item.getType(), item.id);
    }

    public String getComment(CollectableItem.Type itemType, long itemId) {
        return findWriter(itemType, itemId).getComment();
    }

    public String getComment(CollectableItem item) {
        return getComment(item.getType(), item.id);
    }

    public List<Long> getGamePlatformIds(CollectableItem.Type itemType, long itemId) {
        return findWriter(itemType, itemId).getGamePlatformIds();
    }

    public List<Long> getGamePlatformIds(CollectableItem item) {
        return getGamePlatformIds(item.getType(), item.id);
    }

    public boolean getShareToBroadcast(CollectableItem.Type itemType, long itemId) {
        return findWriter(itemType, itemId).getShareToBroadcast();
    }

    public boolean getShareToBroadcast(CollectableItem item) {
        return getShareToBroadcast(item.getType(), item.id);
    }

    public boolean getShareToWeibo(CollectableItem.Type itemType, long itemId) {
        return findWriter(itemType, itemId).getShareToWeibo();
    }

    public boolean getShareToWeibo(CollectableItem item) {
        return getShareToWeibo(item.getType(), item.id);
    }

    public boolean getShareToWeChatMoments(CollectableItem.Type itemType, long itemId) {
        return findWriter(itemType, itemId).getShareToWeChatMoments();
    }

    public boolean getShareToWeChatMoments(CollectableItem item) {
        return getShareToWeChatMoments(item.getType(), item.id);
    }
}
