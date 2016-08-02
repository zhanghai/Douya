/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.eventbus;

public class ReviewDeletedEvent extends Event {

    public long reviewId;

    public ReviewDeletedEvent(long reviewId, Object source) {
        super(source);

        this.reviewId = reviewId;
    }
}
