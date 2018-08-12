/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.broadcast.ui;

import android.os.Bundle;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import me.zhanghai.android.douya.broadcast.content.BroadcastRebroadcastListResource;
import me.zhanghai.android.douya.content.MoreListResourceFragment;
import me.zhanghai.android.douya.eventbus.BroadcastUpdatedEvent;
import me.zhanghai.android.douya.eventbus.EventBusUtils;
import me.zhanghai.android.douya.network.api.ApiError;
import me.zhanghai.android.douya.network.api.info.frodo.Broadcast;
import me.zhanghai.android.douya.network.api.info.frodo.RebroadcastItem;
import me.zhanghai.android.douya.ui.BaseListFragment;
import me.zhanghai.android.douya.ui.SimpleAdapter;
import me.zhanghai.android.douya.util.FragmentUtils;

public class BroadcastRebroadcastListFragment extends BaseListFragment<RebroadcastItem>
        implements BroadcastRebroadcastListResource.Listener {

    private final String KEY_PREFIX = BroadcastRebroadcastListFragment.class.getName() + '.';

    private final String EXTRA_BROADCAST = KEY_PREFIX + "broadcast";

    private Broadcast mBroadcast;

    public static BroadcastRebroadcastListFragment newInstance(Broadcast broadcast) {
        //noinspection deprecation
        return new BroadcastRebroadcastListFragment().setArguments(broadcast);
    }

    /**
     * @deprecated Use {@link #newInstance(Broadcast)} instead.
     */
    public BroadcastRebroadcastListFragment() {}

    protected BroadcastRebroadcastListFragment setArguments(Broadcast broadcast) {
        FragmentUtils.getArgumentsBuilder(this)
                .putParcelable(EXTRA_BROADCAST, broadcast);
        return this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBroadcast = getArguments().getParcelable(EXTRA_BROADCAST);

        EventBusUtils.register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        EventBusUtils.unregister(this);
    }

    @Override
    protected MoreListResourceFragment<?, List<RebroadcastItem>> onAttachResource() {
        return BroadcastRebroadcastListResource.attachTo(mBroadcast.id, this);
    }

    @Override
    protected SimpleAdapter<RebroadcastItem, ?> onCreateAdapter() {
        return new RebroadcastItemAdapter();
    }

    @Override
    public void onLoadRebroadcastListStarted(int requestCode) {
        onLoadListStarted();
    }

    @Override
    public void onLoadRebroadcastListFinished(int requestCode) {
        onLoadListFinished();
    }

    @Override
    public void onLoadRebroadcastListError(int requestCode, ApiError error) {
        onLoadListError(error);
    }

    @Override
    public void onRebroadcastListChanged(int requestCode,
                                         List<RebroadcastItem> newRebroadcastList) {
        onListChanged(newRebroadcastList);
    }

    @Override
    public void onRebroadcastListAppended(int requestCode,
                                          List<RebroadcastItem> appendedRebroadcastList) {
        onListAppended(appendedRebroadcastList);
    }

    @Override
    public void onRebroadcastItemRemoved(int requestCode, int position) {
        onItemRemoved(position);
    }

    @Override
    protected void onListUpdated(List<RebroadcastItem> rebroadcastList) {
        if (mBroadcast.rebroadcastCount < rebroadcastList.size()) {
            mBroadcast.rebroadcastCount = rebroadcastList.size();
            EventBusUtils.postAsync(new BroadcastUpdatedEvent(mBroadcast, this));
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onBroadcastUpdated(BroadcastUpdatedEvent event) {

        if (event.isFromMyself(this)) {
            return;
        }

        Broadcast updatedBroadcast = event.update(mBroadcast, this);
        if (updatedBroadcast != null) {
            mBroadcast = updatedBroadcast;
        }
    }
}
