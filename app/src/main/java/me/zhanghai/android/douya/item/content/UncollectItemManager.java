/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.item.content;

import android.content.Context;

import me.zhanghai.android.douya.content.ResourceWriterManager;
import me.zhanghai.android.douya.network.api.info.frodo.CollectableItem;

public class UncollectItemManager extends ResourceWriterManager<UncollectItemWriter> {

    private static class InstanceHolder {
        public static final UncollectItemManager VALUE = new UncollectItemManager();
    }

    public static UncollectItemManager getInstance() {
        return InstanceHolder.VALUE;
    }

    public void write(CollectableItem.Type itemType, long itemId, Context context) {
        add(new UncollectItemWriter(itemType, itemId, this), context);
    }

    public void write(CollectableItem item, Context context) {
        add(new UncollectItemWriter(item, this), context);
    }

    public boolean isWriting(CollectableItem.Type itemType, long itemId) {
        return findWriter(itemType, itemId) != null;
    }

    public boolean isWriting(CollectableItem item) {
        return isWriting(item.getType(), item.id);
    }

    private UncollectItemWriter findWriter(CollectableItem.Type itemType, long itemId) {
        for (UncollectItemWriter writer : getWriters()) {
            if (writer.getItemType() == itemType && writer.getItemId() == itemId) {
                return writer;
            }
        }
        return null;
    }
}
