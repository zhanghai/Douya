/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.eventbus;

import me.zhanghai.android.douya.network.api.info.frodo.SimpleItemCollection;

public class ItemCollectionUpdatedEvent extends Event {

    public SimpleItemCollection itemCollection;

    public ItemCollectionUpdatedEvent(SimpleItemCollection itemCollection, Object source) {
        super(source);

        this.itemCollection = itemCollection;
    }
}
