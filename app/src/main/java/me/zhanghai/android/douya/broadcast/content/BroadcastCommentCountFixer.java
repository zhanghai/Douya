/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.broadcast.content;

import java.util.List;

import me.zhanghai.android.douya.eventbus.BroadcastUpdatedEvent;
import me.zhanghai.android.douya.eventbus.EventBusUtils;
import me.zhanghai.android.douya.network.api.info.Broadcast;
import me.zhanghai.android.douya.network.api.info.Comment;

public class BroadcastCommentCountFixer {

    private BroadcastCommentCountFixer() {}

    public static void onCommentRemoved(Broadcast broadcast) {

        if (broadcast == null) {
            return;
        }

        --broadcast.commentCount;
        EventBusUtils.postAsync(new BroadcastUpdatedEvent(broadcast));
    }

    public static void onCommentListChanged(Broadcast broadcast,
                                            List<Comment> commentList) {

        if (broadcast == null || commentList == null) {
            return;
        }

        if (broadcast.commentCount < commentList.size()) {
            broadcast.commentCount = commentList.size();
            EventBusUtils.postAsync(new BroadcastUpdatedEvent(broadcast));
        }
    }
}
