/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.broadcast.ui;

import butterknife.BindDimen;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.broadcast.content.HomeBroadcastListResource;
import me.zhanghai.android.douya.broadcast.content.TimelineBroadcastListResource;
import me.zhanghai.android.douya.main.ui.MainActivity;

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
    protected int getExtraPaddingTop() {
        return mToolbarAndTabHeight;
    }

    @Override
    protected TimelineBroadcastListResource onAttachResource() {
        return HomeBroadcastListResource.attachTo(this);
    }

    @Override
    protected void onSwipeRefresh() {
        super.onSwipeRefresh();

        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.refreshNotificationList();
    }
}
