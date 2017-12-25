/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.broadcast.ui;

import java.util.List;

import me.zhanghai.android.douya.broadcast.content.BaseBroadcastListResource;
import me.zhanghai.android.douya.network.api.ApiError;
import me.zhanghai.android.douya.network.api.info.frodo.Broadcast;
import me.zhanghai.android.douya.ui.BaseListFragment;

public abstract class BaseBroadcastListFragment extends BaseListFragment<Broadcast>
        implements BaseBroadcastListResource.Listener {

    @Override
    public void onLoadBroadcastListStarted(int requestCode) {
        onLoadListStarted();
    }

    @Override
    public void onLoadBroadcastListFinished(int requestCode) {
        onLoadListFinished();
    }

    @Override
    public void onLoadBroadcastListError(int requestCode, ApiError error) {
        onLoadListError(error);
    }

    @Override
    public void onBroadcastListChanged(int requestCode, List<Broadcast> newBroadcastList) {
        onListChanged(newBroadcastList);
    }

    @Override
    public void onBroadcastListAppended(int requestCode, List<Broadcast> appendedBroadcastList) {
        onListAppended(appendedBroadcastList);
    }

    @Override
    public void onBroadcastChanged(int requestCode, int position, Broadcast newBroadcast) {
        onItemChanged(position, newBroadcast);
    }

    @Override
    public void onBroadcastRemoved(int requestCode, int position) {
        onItemRemoved(position);
    }
}
