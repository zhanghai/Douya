/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.user.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.network.api.ApiError;
import me.zhanghai.android.douya.network.api.info.apiv2.SimpleUser;
import me.zhanghai.android.douya.ui.LoadMoreAdapter;
import me.zhanghai.android.douya.ui.NoChangeAnimationItemAnimator;
import me.zhanghai.android.douya.ui.OnVerticalScrollListener;
import me.zhanghai.android.douya.user.content.BaseUserListResource;
import me.zhanghai.android.douya.util.LogUtils;
import me.zhanghai.android.douya.util.ToastUtils;
import me.zhanghai.android.douya.util.ViewUtils;

public abstract class UserListFragment extends Fragment implements BaseUserListResource.Listener {

    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.user_list)
    RecyclerView mUserList;
    @BindView(R.id.progress)
    ProgressBar mProgress;

    private BaseUserAdapter mUserAdapter;
    private LoadMoreAdapter mAdapter;

    private BaseUserListResource<?> mUserListResource;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.user_list_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mUserListResource = onAttachUserListResource();

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mUserListResource.load(false);
            }
        });

        // TODO: OK?
        //mUserList.setHasFixedSize(true);
        mUserList.setItemAnimator(new NoChangeAnimationItemAnimator());
        mUserList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mUserAdapter = onCreateAdapter();
        if (mUserListResource.has()) {
            //noinspection unchecked
            mUserAdapter.replace(mUserListResource.get());
        }
        mAdapter = new LoadMoreAdapter(R.layout.load_more_item, mUserAdapter);
        mUserList.setAdapter(mAdapter);
        mUserList.addOnScrollListener(new OnVerticalScrollListener() {
            @Override
            public void onScrolledToBottom() {
                mUserListResource.load(true);
            }
        });

        updateRefreshing();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mUserListResource.detach();
    }

    protected abstract BaseUserListResource<?> onAttachUserListResource();

    @Override
    public void onLoadUserListStarted(int requestCode) {
        updateRefreshing();
    }

    @Override
    public void onLoadUserListFinished(int requestCode) {
        updateRefreshing();
    }

    @Override
    public void onLoadUserListError(int requestCode, ApiError error) {
        LogUtils.e(error.toString());
        Activity activity = getActivity();
        ToastUtils.show(ApiError.getErrorString(error, activity), activity);
    }

    @Override
    public void onUserListChanged(int requestCode, List<SimpleUser> newUserList) {
        mUserAdapter.replace(newUserList);
        //noinspection unchecked
        onUserListUpdated(mUserListResource.get());
    }

    @Override
    public void onUserListAppended(int requestCode, List<SimpleUser> appendedUserList) {
        mUserAdapter.addAll(appendedUserList);
        //noinspection unchecked
        onUserListUpdated(mUserListResource.get());
    }

    abstract protected BaseUserAdapter onCreateAdapter();

    protected void onUserListUpdated(List<SimpleUser> userList) {}

    private void updateRefreshing() {
        boolean loading = mUserListResource.isLoading();
        boolean empty = mUserListResource.isEmpty();
        boolean loadingMore = mUserListResource.isLoadingMore();
        mSwipeRefreshLayout.setRefreshing(loading && (mSwipeRefreshLayout.isRefreshing() || !empty)
                && !loadingMore);
        ViewUtils.setVisibleOrGone(mProgress, loading && empty);
        mAdapter.setProgressVisible(loading && !empty && loadingMore);
    }
}
