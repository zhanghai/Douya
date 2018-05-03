/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.item.content;

import android.content.Context;

import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.content.ResourceWriterManager;
import me.zhanghai.android.douya.network.api.info.frodo.CollectableItem;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleItemCollection;
import me.zhanghai.android.douya.util.ToastUtils;

public class VoteItemCollectionManager extends ResourceWriterManager<VoteItemCollectionWriter> {

    private static class InstanceHolder {
        public static final VoteItemCollectionManager VALUE = new VoteItemCollectionManager();
    }

    public static VoteItemCollectionManager getInstance() {
        return InstanceHolder.VALUE;
    }

    /**
     * @deprecated Use {@link #write(CollectableItem.Type, long, SimpleItemCollection, Context)}
     * instead.
     */
    public void write(CollectableItem.Type itemType, long itemId, long itemCollectionId,
                      Context context) {
        add(new VoteItemCollectionWriter(itemType, itemId, itemCollectionId, this), context);
    }

    public boolean write(CollectableItem.Type itemType, long itemId,
                         SimpleItemCollection itemCollection, Context context) {
        if (shouldWrite(itemCollection, context)) {
            //noinspection deprecation
            write(itemType, itemId, itemCollection.id, context);
            return true;
        } else {
            return false;
        }
    }

    private boolean shouldWrite(SimpleItemCollection itemCollection, Context context) {
        if (itemCollection.user.isOneself()) {
            ToastUtils.show(R.string.item_collection_vote_error_cannot_vote_oneself, context);
            return false;
        } else if (itemCollection.isVoted) {
            ToastUtils.show(R.string.item_collection_vote_error_cannot_vote_again, context);
            return false;
        } else {
            return true;
        }
    }

    public boolean isWriting(long itemCollectionId) {
        return findWriter(itemCollectionId) != null;
    }

    private VoteItemCollectionWriter findWriter(long itemCollectionId) {
        for (VoteItemCollectionWriter writer : getWriters()) {
            if (writer.getItemCollectionId() == itemCollectionId) {
                return writer;
            }
        }
        return null;
    }
}
