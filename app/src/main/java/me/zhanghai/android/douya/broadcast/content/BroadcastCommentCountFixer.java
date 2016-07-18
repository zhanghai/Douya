/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.broadcast.content;

import java.util.List;

import me.zhanghai.android.douya.eventbus.BroadcastUpdatedEvent;
import me.zhanghai.android.douya.eventbus.EventBusUtils;
import me.zhanghai.android.douya.network.api.info.apiv2.Broadcast;
import me.zhanghai.android.douya.network.api.info.apiv2.Comment;

public class BroadcastCommentCountFixer {

    private BroadcastCommentCountFixer() {}

    public static void onCommentRemoved(Broadcast broadcast, Object eventSource) {

        if (broadcast == null) {
            return;
        }

        --broadcast.commentCount;
        EventBusUtils.postAsync(new BroadcastUpdatedEvent(broadcast, eventSource));
    }

    public static void onCommentListChanged(Broadcast broadcast, List<Comment> commentList,
                                            Object eventSource) {

        if (broadcast == null || commentList == null) {
            return;
        }

        if (broadcast.commentCount < commentList.size()) {
            broadcast.commentCount = commentList.size();
            EventBusUtils.postAsync(new BroadcastUpdatedEvent(broadcast, eventSource));
        }
    }
}
