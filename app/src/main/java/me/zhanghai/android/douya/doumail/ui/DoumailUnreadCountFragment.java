/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.doumail.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import me.zhanghai.android.douya.doumail.content.NotificationCountResource;
import me.zhanghai.android.douya.network.api.ApiError;
import me.zhanghai.android.douya.network.api.info.frodo.NotificationCount;
import me.zhanghai.android.douya.util.LogUtils;

public class DoumailUnreadCountFragment extends Fragment
        implements NotificationCountResource.Listener {

    private NotificationCountResource mNotificationCountResource;

    private Listener mListener;

    public static DoumailUnreadCountFragment newInstance() {
        //noinspection deprecation
        return new DoumailUnreadCountFragment();
    }

    /**
     * @deprecated Use {@link #newInstance()} instead.
     */
    public DoumailUnreadCountFragment() {}

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mNotificationCountResource = NotificationCountResource.attachTo(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mNotificationCountResource.has()) {
            mNotificationCountResource.load();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mNotificationCountResource.detach();
    }

    @Override
    public void onLoadNotificationCountStarted(int requestCode) {}

    @Override
    public void onLoadNotificationCountFinished(int requestCode) {}

    @Override
    public void onLoadNotificationCountError(int requestCode, ApiError error) {
        LogUtils.e(error.toString());
    }

    @Override
    public void onNotificationCountChanged(int requestCode,
                                           NotificationCount newNotificationCount) {
        mListener.onDoumailUnreadCountUpdate(newNotificationCount.doumail.count);
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public interface Listener {
        void onDoumailUnreadCountUpdate(int count);
    }
}
