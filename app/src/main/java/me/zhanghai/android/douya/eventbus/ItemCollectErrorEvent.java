/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.eventbus;

import me.zhanghai.android.douya.network.api.info.frodo.CollectableItem;

public class ItemCollectErrorEvent extends Event {

    public CollectableItem.Type itemType;

    public long itemId;

    public ItemCollectErrorEvent(CollectableItem.Type itemType, long itemId, Object source) {
        super(source);

        this.itemType = itemType;
        this.itemId = itemId;
    }
}
