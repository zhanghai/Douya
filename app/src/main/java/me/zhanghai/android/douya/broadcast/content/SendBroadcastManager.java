/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.broadcast.content;

import android.content.Context;
import android.net.Uri;

import java.util.List;

import me.zhanghai.android.douya.content.ResourceWriterManager;

public class SendBroadcastManager extends ResourceWriterManager<SendBroadcastWriter> {

    private static class InstanceHolder {
        public static final SendBroadcastManager VALUE = new SendBroadcastManager();
    }

    public static SendBroadcastManager getInstance() {
        return InstanceHolder.VALUE;
    }

    public long write(String text, List<Uri> imageUris, String linkTitle, String linkUrl,
                      Context context) {
        SendBroadcastWriter writer = new SendBroadcastWriter(text, imageUris, linkTitle,
                linkUrl, this);
        add(writer, context);
        return writer.getId();
    }

    public boolean isWriting(long writerId) {
        return findWriter(writerId) != null;
    }

    public String getText(long writerId) {
        SendBroadcastWriter writer = findWriter(writerId);
        return writer != null ? writer.getText() : null;
    }

    public List<Uri> getImageUris(long writerId) {
        SendBroadcastWriter writer = findWriter(writerId);
        return writer != null ? writer.getImageUris() : null;
    }

    public String getLinkTitle(long writerId) {
        SendBroadcastWriter writer = findWriter(writerId);
        return writer != null ? writer.getLinkTitle() : null;
    }

    public String getLinkUrl(long writerId) {
        SendBroadcastWriter writer = findWriter(writerId);
        return writer != null ? writer.getLinkUrl() : null;
    }

    private SendBroadcastWriter findWriter(long writerId) {
        for (SendBroadcastWriter writer : getWriters()) {
            if (writer.getId() == writerId) {
                return writer;
            }
        }
        return null;
    }
}
