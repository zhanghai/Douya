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

import com.android.volley.VolleyError;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.app.RetainDataFragment;
import me.zhanghai.android.douya.network.api.ApiError;
import me.zhanghai.android.douya.network.api.info.apiv2.User;
import me.zhanghai.android.douya.user.content.RawUserListResource;
import me.zhanghai.android.douya.ui.LoadMoreAdapter;
import me.zhanghai.android.douya.ui.NoChangeAnimationItemAnimator;
import me.zhanghai.android.douya.ui.OnVerticalScrollListener;
import me.zhanghai.android.douya.util.LogUtils;
import me.zhanghai.android.douya.util.ToastUtils;
import me.zhanghai.android.douya.util.ViewUtils;

public abstract class UserListFragment extends Fragment implements RawUserListResource.Listener {

    // Not static because we are to be subclassed.
    private final String KEY_PREFIX = getClass().getName() + '.';

    private final String RETAIN_DATA_KEY_VIEW_STATE = KEY_PREFIX + "view_state";

    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.user_list)
    RecyclerView mUserList;
    @BindView(R.id.progress)
    ProgressBar mProgress;

    private UserAdapter mUserAdapter;
    private LoadMoreAdapter mAdapter;

    private RawUserListResource mUserListResource;
    private RetainDataFragment mRetainDataFragment;

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
        mRetainDataFragment = RetainDataFragment.attachTo(this);

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
        mUserAdapter = new UserAdapter(mUserListResource.get());
        mAdapter = new LoadMoreAdapter(R.layout.load_more_item, mUserAdapter);
        mUserList.setAdapter(mAdapter);
        mUserList.addOnScrollListener(new OnVerticalScrollListener() {
            @Override
            public void onScrolledToBottom() {
                mUserListResource.load(true);
            }
        });

        // View only saves state influenced by user action, so we have to do this ourselves.
        ViewState viewState = mRetainDataFragment.remove(RETAIN_DATA_KEY_VIEW_STATE);
        if (viewState != null) {
            onRestoreViewState(viewState);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        mRetainDataFragment.put(RETAIN_DATA_KEY_VIEW_STATE, onSaveViewState());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mUserListResource.detach();
    }

    protected abstract RawUserListResource onAttachUserListResource();

    @Override
    public void onLoadUserListStarted(int requestCode, boolean loadMore) {
        setRefreshing(true, loadMore);
    }

    @Override
    public void onLoadUserListFinished(int requestCode, boolean loadMore) {
        setRefreshing(false, loadMore);
    }

    @Override
    public void onLoadUserListError(int requestCode, VolleyError error) {
        LogUtils.e(error.toString());
        Activity activity = getActivity();
        ToastUtils.show(ApiError.getErrorString(error, activity), activity);
    }

    @Override
    public void onUserListChanged(int requestCode, List<User> newUserList) {
        mUserAdapter.replace(newUserList);
        onUserListUpdated(mUserListResource.get());
    }

    @Override
    public void onUserListAppended(int requestCode, List<User> appendedUserList) {
        mUserAdapter.addAll(appendedUserList);
        onUserListUpdated(mUserListResource.get());
    }

    protected void onUserListUpdated(List<User> userList) {}

    private void setRefreshing(boolean refreshing, boolean loadMore) {
        mSwipeRefreshLayout.setEnabled(!refreshing);
        if (!refreshing) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
        ViewUtils.setVisibleOrGone(mProgress, refreshing && mUserAdapter.getItemCount() == 0);
        mAdapter.setProgressVisible(refreshing && mUserAdapter.getItemCount() > 0
                && loadMore);
    }

    private ViewState onSaveViewState() {
        return new ViewState(mProgress.getVisibility(), mAdapter.isProgressVisible());
    }

    private void onRestoreViewState(ViewState state) {
        mProgress.setVisibility(state.progressVisibility);
        mAdapter.setProgressVisible(state.adapterProgressVisible);
    }

    private static class ViewState {

        public int progressVisibility;
        public boolean adapterProgressVisible;

        public ViewState(int progressVisibility, boolean adapterProgressVisible) {
            this.progressVisibility = progressVisibility;
            this.adapterProgressVisible = adapterProgressVisible;
        }
    }
}
