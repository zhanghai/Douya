/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.followship.ui;

public class FollowerListActivityFragment extends FollowshipListActivityFragment {

    /**
     * @deprecated Use {@link #newInstance(String)} instead.
     */
    public FollowerListActivityFragment() {}

    public static FollowerListActivityFragment newInstance(String userIdOrUid) {
        //noinspection deprecation
        FollowerListActivityFragment fragment = new FollowerListActivityFragment();
        fragment.setArguments(userIdOrUid);
        return fragment;
    }

    @Override
    protected FollowshipListFragment onCreateListFragment() {
        return FollowerListFragment.newInstance(getUserIdOrUid());
    }
}
