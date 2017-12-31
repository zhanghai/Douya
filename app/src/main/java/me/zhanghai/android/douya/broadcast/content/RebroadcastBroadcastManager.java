/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.broadcast.content;

import android.content.Context;

import me.zhanghai.android.douya.content.ResourceWriterManager;
import me.zhanghai.android.douya.network.api.info.frodo.Broadcast;

public class RebroadcastBroadcastManager extends ResourceWriterManager<RebroadcastBroadcastWriter> {

    private static class InstanceHolder {
        public static final RebroadcastBroadcastManager VALUE = new RebroadcastBroadcastManager();
    }

    public static RebroadcastBroadcastManager getInstance() {
        return InstanceHolder.VALUE;
    }

    /**
     * @deprecated Use {@link #write(Broadcast, String, Context)} instead.
     */
    public void write(long broadcastId, String text, Context context) {
        add(new RebroadcastBroadcastWriter(broadcastId, text, this), context);
    }

    public void write(Broadcast broadcast, String text, Context context) {
        add(new RebroadcastBroadcastWriter(broadcast, text, this), context);
    }

    public boolean isWriting(long broadcastId) {
        return findWriter(broadcastId) != null;
    }

    public boolean isWritingQuickRebroadcast(long broadcastId) {
        RebroadcastBroadcastWriter writer = findWriter(broadcastId);
        return writer != null && writer.getText() == null;
    }

    public String getText(long broadcastId) {
        RebroadcastBroadcastWriter writer = findWriter(broadcastId);
        return writer != null ? writer.getText() : null;
    }

    private RebroadcastBroadcastWriter findWriter(long broadcastId) {
        for (RebroadcastBroadcastWriter writer : getWriters()) {
            if (writer.getBroadcastId() == broadcastId) {
                return writer;
            }
        }
        return null;
    }
}
