/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.broadcast.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import java.util.List;

import butterknife.BindDimen;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.broadcast.content.HomeMergedBroadcastListResource;
import me.zhanghai.android.douya.content.MoreListResource;
import me.zhanghai.android.douya.main.ui.MainActivity;
import me.zhanghai.android.douya.network.api.info.frodo.Broadcast;

public class HomeBroadcastListFragment extends BaseTimelineBroadcastListFragment {

    @BindDimen(R.dimen.toolbar_and_tab_height)
    int mToolbarAndTabHeight;

    public static HomeBroadcastListFragment newInstance() {
        //noinspection deprecation
        return new HomeBroadcastListFragment();
    }

    /**
     * @deprecated Use {@link #newInstance()} instead.
     */
    public HomeBroadcastListFragment() {}

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);

        setPaddingTop(mToolbarAndTabHeight);
    }

    @Override
    protected MoreListResource<List<Broadcast>> onAttachResource() {
        return HomeMergedBroadcastListResource.attachTo(this);
    }

    @Override
    protected void onSwipeRefresh() {
        super.onSwipeRefresh();

        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.refreshNotificationList();
    }
}
