/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.broadcast.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import butterknife.BindDimen;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.broadcast.content.BroadcastListResource;
import me.zhanghai.android.douya.broadcast.content.HomeBroadcastListResource;
import me.zhanghai.android.douya.main.ui.MainActivity;

public class HomeBroadcastListFragment extends BaseBroadcastListFragment {

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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);

        setPaddingTop(mToolbarAndTabHeight);
    }

    @Override
    protected BroadcastListResource onAttachBroadcastListResource() {
        return HomeBroadcastListResource.attachTo(this);
    }

    @Override
    protected void onSwipeRefresh() {
        super.onSwipeRefresh();

        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.refreshNotificationList();
    }
}
