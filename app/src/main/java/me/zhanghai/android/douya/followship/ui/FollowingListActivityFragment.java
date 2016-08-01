/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.followship.ui;

public class FollowingListActivityFragment extends FollowshipListActivityFragment {

    /**
     * @deprecated Use {@link #newInstance(String)} instead.
     */
    public FollowingListActivityFragment() {}

    public static FollowingListActivityFragment newInstance(String userIdOrUid) {
        //noinspection deprecation
        FollowingListActivityFragment fragment = new FollowingListActivityFragment();
        fragment.setArguments(userIdOrUid);
        return fragment;
    }

    @Override
    protected FollowshipListFragment onCreateListFragment() {
        return FollowingListFragment.newInstance(getUserIdOrUid());
    }
}
