/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.eventbus;

import me.zhanghai.android.douya.network.api.info.frodo.CollectableItem;
import me.zhanghai.android.douya.network.api.info.frodo.ItemCollection;

public class ItemCollectedEvent extends Event {

    public CollectableItem.Type itemType;

    public long itemId;

    public ItemCollection collection;

    public ItemCollectedEvent(CollectableItem.Type itemType, long itemId, ItemCollection collection,
                              Object source) {
        super(source);

        this.itemType = itemType;
        this.itemId = itemId;
        this.collection = collection;
    }
}
