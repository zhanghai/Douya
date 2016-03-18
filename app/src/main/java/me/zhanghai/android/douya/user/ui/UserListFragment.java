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

import butterknife.Bind;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.app.RetainDataFragment;
import me.zhanghai.android.douya.network.RequestFragment;
import me.zhanghai.android.douya.network.api.ApiError;
import me.zhanghai.android.douya.network.api.ApiRequest;
import me.zhanghai.android.douya.network.api.info.User;
import me.zhanghai.android.douya.ui.LoadMoreAdapter;
import me.zhanghai.android.douya.ui.NoChangeAnimationItemAnimator;
import me.zhanghai.android.douya.ui.OnVerticalScrollListener;
import me.zhanghai.android.douya.util.LogUtils;
import me.zhanghai.android.douya.util.ToastUtils;
import me.zhanghai.android.douya.util.ViewUtils;

public abstract class UserListFragment extends Fragment implements RequestFragment.Listener {

    private static final int USER_COUNT_PER_LOAD = 20;

    private static final int REQUEST_CODE_LOAD_USER_LIST = 0;

    // We are the base class so we cannot use constants here.
    private final String KEY_PREFIX = getClass().getName() + '.';

    private final String RETAIN_DATA_KEY_USER_LIST = KEY_PREFIX + "user_list";
    private final String RETAIN_DATA_KEY_CAN_LOAD_MORE = KEY_PREFIX + "can_load_more";
    private final String RETAIN_DATA_KEY_LOADING_USER_LIST = KEY_PREFIX + "loading_user_list";
    private final String RETAIN_DATA_KEY_VIEW_STATE = KEY_PREFIX + "view_state";

    @Bind(R.id.swipe_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.user_list)
    RecyclerView mUserList;
    @Bind(R.id.progress)
    ProgressBar mProgress;

    private RetainDataFragment mRetainDataFragment;

    private UserAdapter mUserAdapter;
    private LoadMoreAdapter mAdapter;
    private boolean mCanLoadMore;

    private boolean mLoadingUserList;

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

        mRetainDataFragment = RetainDataFragment.attachTo(this);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadUserList(false);
            }
        });

        // TODO: OK?
        //mUserList.setHasFixedSize(true);
        mUserList.setItemAnimator(new NoChangeAnimationItemAnimator());
        mUserList.setLayoutManager(new LinearLayoutManager(getActivity()));
        List<User> userList = mRetainDataFragment.remove(RETAIN_DATA_KEY_USER_LIST);
        mUserAdapter = new UserAdapter(userList);
        mAdapter = new LoadMoreAdapter(R.layout.load_more_item, mUserAdapter);
        mUserList.setAdapter(mAdapter);
        mUserList.addOnScrollListener(new OnVerticalScrollListener() {
            @Override
            public void onScrolledToBottom() {
                loadUserList(true);
            }
        });

        mCanLoadMore = mRetainDataFragment.removeBoolean(RETAIN_DATA_KEY_CAN_LOAD_MORE, true);
        mLoadingUserList = mRetainDataFragment.removeBoolean(RETAIN_DATA_KEY_LOADING_USER_LIST,
                false);

        // View only saves state influenced by user action, so we have to do this ourselves.
        ViewState viewState = mRetainDataFragment.remove(RETAIN_DATA_KEY_VIEW_STATE);
        if (viewState != null) {
            onRestoreViewState(viewState);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        mRetainDataFragment.put(RETAIN_DATA_KEY_USER_LIST, mUserAdapter.getList());
        mRetainDataFragment.put(RETAIN_DATA_KEY_CAN_LOAD_MORE, mCanLoadMore);
        mRetainDataFragment.put(RETAIN_DATA_KEY_LOADING_USER_LIST, mLoadingUserList);
        mRetainDataFragment.put(RETAIN_DATA_KEY_VIEW_STATE, onSaveViewState());
    }

    @Override
    public void onStart() {
        super.onStart();

        // Only auto-load when initially empty, not loaded but empty.
        if (mUserAdapter.getItemCount() == 0 && mCanLoadMore) {
            loadUserList(false);
        }
    }

    private ViewState onSaveViewState() {
        return new ViewState(mProgress.getVisibility(), mAdapter.isProgressVisible());
    }

    private void onRestoreViewState(ViewState state) {
        mProgress.setVisibility(state.progressVisibility);
        mAdapter.setProgressVisible(state.adapterProgressVisible);
    }

    @Override
    public void onVolleyResponse(int requestCode, boolean successful, Object result,
                                 VolleyError error, Object requestState) {
        switch (requestCode) {
            case REQUEST_CODE_LOAD_USER_LIST:
                //noinspection unchecked
                onLoadUserListResponse(successful, (List<User>) result, error,
                        (LoadUserListState) requestState);
                break;
            default:
                LogUtils.w("Unknown request code " + requestCode + ", with successful=" + successful
                        + ", result=" + result + ", error=" + error);
        }
    }

    private void loadUserList(boolean loadMore) {

        if (mLoadingUserList || (loadMore && !mCanLoadMore)) {
            return;
        }

        // Flawed API design: should use untilId instead of start.
        Integer start = loadMore ? mUserAdapter.getItemCount() : null;
        int count = USER_COUNT_PER_LOAD;
        ApiRequest<List<User>> request = onCreateRequest(start, count);
        LoadUserListState state = new LoadUserListState(loadMore, count);
        RequestFragment.startRequest(REQUEST_CODE_LOAD_USER_LIST, request, state, this);

        mLoadingUserList = true;
        setRefreshing(true, loadMore);
    }

    protected abstract ApiRequest<List<User>> onCreateRequest(Integer start, Integer count);

    private void onLoadUserListResponse(boolean successful, List<User> result, VolleyError error,
                                        LoadUserListState state) {

        Activity activity = getActivity();
        if (successful) {

            mCanLoadMore = result.size() == state.count;
            if (state.loadMore) {
                mUserAdapter.addAll(result);
            } else {
                mUserAdapter.replace(result);
            }
            setRefreshing(false, state.loadMore);
            mLoadingUserList = false;

            onUserListUpdated(mUserAdapter.getList());
        } else {

            LogUtils.e(error.toString());
            ToastUtils.show(ApiError.getErrorString(error, activity), activity);
            setRefreshing(false, state.loadMore);
            mLoadingUserList = false;
        }
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

    private static class LoadUserListState {

        public boolean loadMore;
        public int count;

        public LoadUserListState(boolean loadMore, int count) {
            this.loadMore = loadMore;
            this.count = count;
        }
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
