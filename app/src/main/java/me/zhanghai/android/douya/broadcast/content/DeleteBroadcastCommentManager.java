/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.broadcast.content;

import android.content.Context;

import me.zhanghai.android.douya.content.ResourceWriterManager;

public class DeleteBroadcastCommentManager
        extends ResourceWriterManager<DeleteBroadcastCommentWriter> {

    private static class InstanceHolder {
        public static final DeleteBroadcastCommentManager VALUE =
                new DeleteBroadcastCommentManager();
    }

    public static DeleteBroadcastCommentManager getInstance() {
        return InstanceHolder.VALUE;
    }

    public void write(long broadcastId, long commentId, Context context) {
        add(new DeleteBroadcastCommentWriter(broadcastId, commentId, this), context);
    }

    public boolean isWriting(long broadcastId, long commentId) {
        return findWriter(broadcastId, commentId) != null;
    }

    private DeleteBroadcastCommentWriter findWriter(long broadcastId, long commentId) {
        for (DeleteBroadcastCommentWriter writer : getWriters()) {
            if (writer.getBroadcastId() == broadcastId && writer.getCommentId() == commentId) {
                return writer;
            }
        }
        return null;
    }
}
