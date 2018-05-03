/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.eventbus;

public class ItemCollectionWriteFinishedEvent extends Event {

    public long itemCollectionId;

    public ItemCollectionWriteFinishedEvent(long itemCollectionId, Object source) {
        super(source);

        this.itemCollectionId = itemCollectionId;
    }
}
