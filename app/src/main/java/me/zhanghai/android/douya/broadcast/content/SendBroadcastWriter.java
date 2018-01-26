/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.broadcast.content;

import android.content.Context;

import java.util.List;

import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.content.ResourceWriter;
import me.zhanghai.android.douya.content.ResourceWriterManager;
import me.zhanghai.android.douya.eventbus.BroadcastSendErrorEvent;
import me.zhanghai.android.douya.eventbus.BroadcastSentEvent;
import me.zhanghai.android.douya.eventbus.BroadcastWriteStartedEvent;
import me.zhanghai.android.douya.eventbus.EventBusUtils;
import me.zhanghai.android.douya.network.api.ApiError;
import me.zhanghai.android.douya.network.api.ApiRequest;
import me.zhanghai.android.douya.network.api.ApiService;
import me.zhanghai.android.douya.network.api.info.frodo.Broadcast;
import me.zhanghai.android.douya.util.LogUtils;
import me.zhanghai.android.douya.util.ToastUtils;

class SendBroadcastWriter extends ResourceWriter<SendBroadcastWriter, Broadcast> {

    private static int sNextId = 1;

    private long mId;

    private String mText;
    private List<String> mImageUrls;
    private String mLinkTitle;
    private String mLinkUrl;

    SendBroadcastWriter(String text, List<String> imageUrls, String linkTitle, String linkUrl,
                        ResourceWriterManager<SendBroadcastWriter> manager) {
        super(manager);

        mId = sNextId;
        ++sNextId;

        mText = text;
        mImageUrls = imageUrls;
        mLinkTitle = linkTitle;
        mLinkUrl = linkUrl;
    }

    public long getId() {
        return mId;
    }

    public String getText() {
        return mText;
    }

    public List<String> getImageUrls() {
        return mImageUrls;
    }

    public String getLinkTitle() {
        return mLinkTitle;
    }

    public String getLinkUrl() {
        return mLinkUrl;
    }

    @Override
    protected ApiRequest<Broadcast> onCreateRequest() {
        return ApiService.getInstance().sendBroadcast(mText, mImageUrls, mLinkTitle,
                mLinkUrl);
    }

    @Override
    public void onStart() {
        super.onStart();

        EventBusUtils.postAsync(new BroadcastWriteStartedEvent(mId, this));
    }

    @Override
    public void onResponse(Broadcast response) {

        ToastUtils.show(R.string.broadcast_send_successful, getContext());

        EventBusUtils.postAsync(new BroadcastSentEvent(mId, response, this));

        stopSelf();
    }

    @Override
    public void onErrorResponse(ApiError error) {

        LogUtils.e(error.toString());
        Context context = getContext();
        ToastUtils.show(context.getString(R.string.broadcast_send_failed_format,
                ApiError.getErrorString(error, context)), context);

        EventBusUtils.postAsync(new BroadcastSendErrorEvent(mId, this));

        stopSelf();
    }
}
