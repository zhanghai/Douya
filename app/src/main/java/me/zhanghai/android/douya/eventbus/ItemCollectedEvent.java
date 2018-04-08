/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.eventbus;

import me.zhanghai.android.douya.network.api.info.frodo.ItemCollection;

public class ItemCollectedEvent extends Event {

    public long writerId;

    public ItemCollection collection;

    public ItemCollectedEvent(long writerId, ItemCollection collection, Object source) {
        super(source);

        this.writerId = writerId;
        this.collection = collection;
    }
}
