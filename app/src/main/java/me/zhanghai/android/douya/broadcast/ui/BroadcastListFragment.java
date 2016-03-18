/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.broadcast.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Keep;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.android.volley.VolleyError;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import me.zhanghai.android.customtabshelper.CustomTabsHelperFragment;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.account.util.AccountUtils;
import me.zhanghai.android.douya.app.RetainDataFragment;
import me.zhanghai.android.douya.broadcast.app.HomeBroadcastListCache;
import me.zhanghai.android.douya.eventbus.BroadcastDeletedEvent;
import me.zhanghai.android.douya.eventbus.BroadcastUpdatedEvent;
import me.zhanghai.android.douya.link.UriHandler;
import me.zhanghai.android.douya.main.ui.MainActivity;
import me.zhanghai.android.douya.network.RequestFragment;
import me.zhanghai.android.douya.network.api.ApiContract.Response.Error.Codes;
import me.zhanghai.android.douya.network.api.ApiError;
import me.zhanghai.android.douya.network.api.ApiRequest;
import me.zhanghai.android.douya.network.api.ApiRequests;
import me.zhanghai.android.douya.network.api.info.Broadcast;
import me.zhanghai.android.douya.settings.info.Settings;
import me.zhanghai.android.douya.ui.AppBarManager;
import me.zhanghai.android.douya.ui.FriendlyFloatingActionButton;
import me.zhanghai.android.douya.ui.LoadMoreAdapter;
import me.zhanghai.android.douya.ui.NoChangeAnimationItemAnimator;
import me.zhanghai.android.douya.ui.OnVerticalScrollWithPagingSlopListener;
import me.zhanghai.android.douya.util.Callback;
import me.zhanghai.android.douya.util.CheatSheetUtils;
import me.zhanghai.android.douya.util.RecyclerViewUtils;
import me.zhanghai.android.douya.util.LogUtils;
import me.zhanghai.android.douya.util.ToastUtils;
import me.zhanghai.android.douya.util.TransitionUtils;
import me.zhanghai.android.douya.util.ViewUtils;

public class BroadcastListFragment extends Fragment implements RequestFragment.Listener,
        BroadcastAdapter.Listener {

    private static final int BROADCAST_COUNT_PER_LOAD = 20;

    private static final int REQUEST_CODE_LOAD_BROADCAST_LIST = 0;
    private static final int REQUEST_CODE_LIKE_BROADCAST = 1;
    private static final int REQUEST_CODE_REBROADCAST_BROADCAST = 2;

    private static final String KEY_PREFIX = BroadcastListFragment.class.getName() + '.';

    private static final String RETAIN_DATA_KEY_BROADCAST_LIST = KEY_PREFIX + "broadcast_list";
    private static final String RETAIN_DATA_KEY_CAN_LOAD_MORE = KEY_PREFIX + "can_load_more";
    private static final String RETAIN_DATA_KEY_LOADING_BROADCAST_LIST = KEY_PREFIX
            + "loading_broadcast_list";
    private static final String RETAIN_DATA_KEY_VIEW_STATE = KEY_PREFIX + "view_state";

    private final Handler mHandler = new Handler();

    @Bind(R.id.swipe_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.broadcast_list)
    RecyclerView mBroadcastList;
    @Bind(R.id.progress)
    ProgressBar mProgress;
    @Bind(R.id.send)
    FriendlyFloatingActionButton mSendFab;

    private RetainDataFragment mRetainDataFragment;

    private BroadcastAdapter mBroadcastAdapter;
    private LoadMoreAdapter mAdapter;
    private boolean mCanLoadMore;

    private boolean mLoadingBroadcastList;

    public static BroadcastListFragment newInstance() {
        //noinspection deprecation
        return new BroadcastListFragment();
    }

    /**
     * @deprecated Use {@link #newInstance()} instead.
     */
    public BroadcastListFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.broadcast_list_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final MainActivity activity = (MainActivity) getActivity();

        CustomTabsHelperFragment.attachTo(this);
        mRetainDataFragment = RetainDataFragment.attachTo(this);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadBroadcastList(false);
                activity.refreshNotificationList();
            }
        });

        mBroadcastList.setHasFixedSize(true);
        mBroadcastList.setItemAnimator(new NoChangeAnimationItemAnimator());
        if (ViewUtils.hasSw600dp(activity)) {
            int columnCount = ViewUtils.isInLandscape(activity) ? 3 : 2;
            mBroadcastList.setLayoutManager(new StaggeredGridLayoutManager(columnCount,
                    StaggeredGridLayoutManager.VERTICAL));
        } else {
            mBroadcastList.setLayoutManager(new LinearLayoutManager(activity));
        }
        List<Broadcast> broadcastList = mRetainDataFragment.remove(RETAIN_DATA_KEY_BROADCAST_LIST);
        mBroadcastAdapter = new BroadcastAdapter(broadcastList, this);
        mAdapter = new LoadMoreAdapter(R.layout.load_more_card_item, mBroadcastAdapter);
        mBroadcastList.setAdapter(mAdapter);
        final AppBarManager appBarManager = (AppBarManager) getParentFragment();
        mBroadcastList.addOnScrollListener(new OnVerticalScrollWithPagingSlopListener(activity) {
            @Override
            public void onScrolledUp(int dy) {
                if (!RecyclerViewUtils.hasFirstChildReachedTop(mBroadcastList)) {
                    onShow();
                } else {
                    super.onScrolledUp(dy);
                }
            }
            @Override
            public void onScrolledUp() {
                onShow();
            }
            private void onShow() {
                appBarManager.showAppBar();
                mSendFab.show();
            }
            @Override
            public void onScrolledDown() {
                if (RecyclerViewUtils.hasFirstChildReachedTop(mBroadcastList)) {
                    appBarManager.hideAppBar();
                    mSendFab.hide();
                }
            }
            @Override
            public void onScrolledToBottom() {
                loadBroadcastList(true);
            }
        });

        CheatSheetUtils.setup(mSendFab);
        mSendFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSendBroadcast();
            }
        });

        mCanLoadMore = mRetainDataFragment.removeBoolean(RETAIN_DATA_KEY_CAN_LOAD_MORE, true);
        mLoadingBroadcastList = mRetainDataFragment.removeBoolean(
                RETAIN_DATA_KEY_LOADING_BROADCAST_LIST, false);

        // View only saves state influenced by user action, so we have to do this ourselves.
        ViewState viewState = mRetainDataFragment.remove(RETAIN_DATA_KEY_VIEW_STATE);
        if (viewState != null) {
            onRestoreViewState(viewState);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        mRetainDataFragment.put(RETAIN_DATA_KEY_BROADCAST_LIST, mBroadcastAdapter.getList());
        mRetainDataFragment.put(RETAIN_DATA_KEY_CAN_LOAD_MORE, mCanLoadMore);
        mRetainDataFragment.put(RETAIN_DATA_KEY_LOADING_BROADCAST_LIST, mLoadingBroadcastList);
        mRetainDataFragment.put(RETAIN_DATA_KEY_VIEW_STATE, onSaveViewState());
    }

    @Override
    public void onStart() {
        super.onStart();

        EventBus.getDefault().register(this);

        // Only auto-load when initially empty, not loaded but empty.
        if (mBroadcastAdapter.getItemCount() == 0 && mCanLoadMore) {
            loadHomeBroadcastList();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        EventBus.getDefault().unregister(this);

        if (mBroadcastAdapter.getItemCount() > 0) {
            saveHomeBroadcastListToCache(mBroadcastAdapter.getList());
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
            case REQUEST_CODE_LOAD_BROADCAST_LIST:
                //noinspection unchecked
                onLoadBroadcastListResponse(successful, (List<Broadcast>) result, error,
                        (LoadBroadcastListState) requestState);
                break;
            case REQUEST_CODE_LIKE_BROADCAST:
                onLikeBroadcastResponse(successful, (Broadcast) result, error,
                        (LikeBroadcastState) requestState);
                break;
            case REQUEST_CODE_REBROADCAST_BROADCAST:
                onRebroadcastBroadcastResponse(successful, (Broadcast) result, error,
                        (RebroadcastBroadcastState) requestState);
                break;
            default:
                LogUtils.w("Unknown request code " + requestCode + ", with successful=" + successful
                        + ", result=" + result + ", error=" + error);
        }
    }

    private void loadBroadcastList(boolean loadMore) {

        if (mLoadingBroadcastList || (loadMore && !mCanLoadMore)) {
            return;
        }

        Long untilId = null;
        int itemCount = mBroadcastAdapter.getItemCount();
        if (loadMore && itemCount > 0) {
            untilId = mBroadcastAdapter.getItemId(itemCount - 1);
        }
        int count = BROADCAST_COUNT_PER_LOAD;
        ApiRequest<List<Broadcast>> request = ApiRequests.newBroadcastListRequest(null, null,
                untilId, count, getActivity());
        LoadBroadcastListState state = new LoadBroadcastListState(loadMore, count);
        RequestFragment.startRequest(REQUEST_CODE_LOAD_BROADCAST_LIST, request, state, this);

        mLoadingBroadcastList = true;
        setRefreshing(true, loadMore);
    }

    private void onLoadBroadcastListResponse(boolean successful, List<Broadcast> result,
                                             VolleyError error, LoadBroadcastListState state) {

        Activity activity = getActivity();
        if (successful) {

            mCanLoadMore = result.size() == state.count;
            if (state.loadMore) {
                mBroadcastAdapter.addAll(result);
            } else {
                mBroadcastAdapter.replace(result);
            }
            setRefreshing(false, state.loadMore);
            mLoadingBroadcastList = false;
        } else {

            LogUtils.e(error.toString());
            ToastUtils.show(ApiError.getErrorString(error, activity), activity);
            setRefreshing(false, state.loadMore);
            mLoadingBroadcastList = false;
        }
    }

    private void setRefreshing(boolean refreshing, boolean loadMore) {
        mSwipeRefreshLayout.setEnabled(!refreshing);
        if (!refreshing) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
        ViewUtils.setVisibleOrGone(mProgress, refreshing
                && mBroadcastAdapter.getItemCount() == 0);
        mAdapter.setProgressVisible(refreshing && mBroadcastAdapter.getItemCount() > 0 && loadMore);
    }

    @Override
    public boolean onLikeBroadcast(long itemId, Broadcast broadcast, boolean like) {

        Activity activity = getActivity();

        if (broadcast.author.id == AccountUtils.getUserId(activity)) {
            ToastUtils.show(R.string.broadcast_like_error_cannot_like_oneself, activity);
            return false;
        }

        ApiRequest<Broadcast> request = ApiRequests.newLikeBroadcastRequest(broadcast.id, like,
                activity);
        LikeBroadcastState state = new LikeBroadcastState(itemId, broadcast.id, like);
        RequestFragment.startRequest(REQUEST_CODE_LIKE_BROADCAST, request, state, this);
        return true;
    }

    private void onLikeBroadcastResponse(boolean successful, Broadcast result, VolleyError error,
                                         LikeBroadcastState state) {

        Activity activity = getActivity();
        if (successful) {

            EventBus.getDefault().post(new BroadcastUpdatedEvent(result));
            ToastUtils.show(state.like ? R.string.broadcast_like_successful
                    : R.string.broadcast_unlike_successful, getActivity());
        } else {

            LogUtils.e(error.toString());
            boolean notified = false;
            if (error instanceof ApiError) {
                // Correct our local state if needed.
                ApiError apiError = (ApiError) error;
                Boolean shouldBeLiked = null;
                if (apiError.code == Codes.LikeBroadcast.ALREADY_LIKED) {
                    shouldBeLiked = true;
                } else if (apiError.code == Codes.LikeBroadcast.NOT_LIKED_YET) {
                    shouldBeLiked = false;
                }
                if (shouldBeLiked != null) {
                    Broadcast broadcast = mBroadcastAdapter.findBroadcastById(state.broadcastId);
                    if (broadcast != null) {
                        broadcast.fixLiked(shouldBeLiked);
                        EventBus.getDefault().post(new BroadcastUpdatedEvent(broadcast));
                        notified = true;
                    }
                }
            }
            if (!notified) {
                // Must notify changed to reset pending status so that off-screen
                // items will be invalidated.
                mBroadcastAdapter.notifyItemChangedById(state.itemId);
            }
            ToastUtils.show(activity.getString(state.like ? R.string.broadcast_like_failed_format
                            : R.string.broadcast_unlike_failed_format,
                    ApiError.getErrorString(error, activity)), activity);
        }
    }

    @Override
    public boolean onRebroadcastBroadcast(long itemId, Broadcast broadcast, boolean rebroadcast) {

        Activity activity = getActivity();

        if (broadcast.author.id == AccountUtils.getUserId(activity)) {
            ToastUtils.show(R.string.broadcast_rebroadcast_error_cannot_rebroadcast_oneself,
                    activity);
            return false;
        }

        ApiRequest<Broadcast> request = ApiRequests.newRebroadcastBroadcastRequest(broadcast.id,
                rebroadcast, getActivity());
        RebroadcastBroadcastState state = new RebroadcastBroadcastState(itemId, broadcast.id,
                rebroadcast);
        RequestFragment.startRequest(REQUEST_CODE_REBROADCAST_BROADCAST, request, state, this);
        return true;
    }

    private void onRebroadcastBroadcastResponse(boolean successful, Broadcast result,
                                                VolleyError error,
                                                RebroadcastBroadcastState state) {

        Activity activity = getActivity();
        if (successful) {

            if (!state.rebroadcast) {
                // Delete the rebroadcast broadcast by user. Must be done before we
                // update the broadcast so that we can retrieve rebroadcastId for the
                // old one.
                Broadcast broadcast = mBroadcastAdapter.findBroadcastById(state.broadcastId);
                if (broadcast != null && broadcast.rebroadcastId != null) {
                    EventBus.getDefault().post(new BroadcastDeletedEvent(broadcast.rebroadcastId));
                }
            }
            EventBus.getDefault().post(new BroadcastUpdatedEvent(result));
            ToastUtils.show(state.rebroadcast ? R.string.broadcast_rebroadcast_successful
                    : R.string.broadcast_unrebroadcast_successful, activity);
        } else {

            LogUtils.e(error.toString());
            boolean notified = false;
            if (error instanceof ApiError) {
                // Correct our local state if needed.
                ApiError apiError = (ApiError) error;
                Boolean shouldBeRebroadcasted = null;
                if (apiError.code == Codes.RebroadcastBroadcast.ALREADY_REBROADCASTED) {
                    shouldBeRebroadcasted = true;
                } else if (apiError.code == Codes.RebroadcastBroadcast.NOT_REBROADCASTED_YET) {
                    shouldBeRebroadcasted = false;
                }
                if (shouldBeRebroadcasted != null) {
                    Broadcast broadcast = mBroadcastAdapter.findBroadcastById(state.broadcastId);
                    if (broadcast != null) {
                        broadcast.fixRebroacasted(shouldBeRebroadcasted);
                        EventBus.getDefault().post(new BroadcastUpdatedEvent(broadcast));
                        notified = true;
                    }
                }
            }
            if (!notified) {
                // Must notify changed to reset pending status so that off-screen
                // items will be invalidated.
                mBroadcastAdapter.notifyItemChangedById(state.itemId);
            }
            ToastUtils.show(activity.getString(state.rebroadcast ?
                            R.string.broadcast_rebroadcast_failed_format
                            : R.string.broadcast_unrebroadcast_failed_format,
                    ApiError.getErrorString(error, activity)), activity);
        }
    }

    @Override
    public void onCommentBroadcast(Broadcast broadcast, View sharedView) {
        // Open ime for comment if there is none; otherwise we always let the user see what others
        // have already said first, to help to make the world a better place.
        openBroadcast(broadcast, sharedView, broadcast.canComment() && broadcast.commentCount == 0);
    }

    @Override
    public void onOpenBroadcast(Broadcast broadcast, View sharedView) {
        openBroadcast(broadcast, sharedView, false);
    }

    private void openBroadcast(Broadcast broadcast, View sharedView, boolean comment) {
        Activity activity = getActivity();
        Intent intent = BroadcastActivity.makeIntent(broadcast, comment, getActivity());
        Bundle options = TransitionUtils.makeActivityOptionsBundle(activity, sharedView);
        ActivityCompat.startActivity(activity, intent, options);
    }

    @Keep
    public void onEventMainThread(BroadcastUpdatedEvent event) {
        mBroadcastAdapter.updateBroadcast(event.broadcast);
    }

    @Keep
    public void onEventMainThread(BroadcastDeletedEvent event) {
        mBroadcastAdapter.removeBroadcastById(event.broadcastId);
    }

    private void loadHomeBroadcastList() {
        HomeBroadcastListCache.get(mHandler, new Callback<List<Broadcast>>() {
            @Override
            public void onValue(List<Broadcast> broadcastList) {
                if (isRemoving()) {
                    return;
                }
                boolean hasCache = broadcastList != null && broadcastList.size() > 0;
                if (hasCache) {
                    mBroadcastAdapter.replace(broadcastList);
                }
                if (!hasCache || Settings.AUTO_REFRESH_HOME.getValue(getActivity())) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (isRemoving()) {
                                return;
                            }
                            if (!mBroadcastAdapter.getList().isEmpty()) {
                                mSwipeRefreshLayout.setRefreshing(true);
                            }
                            loadBroadcastList(false);
                        }
                    });
                }
            }
        }, getActivity());
    }

    private void saveHomeBroadcastListToCache(List<Broadcast> broadcastList) {
        HomeBroadcastListCache.put(broadcastList, getActivity());
    }

    private void onSendBroadcast() {
        // FIXME: Create a SendBroadcastActivity.
        UriHandler.open("https://www.douban.com/#isay-cont", getActivity());
    }

    private static class ViewState {

        public int progressVisibility;
        public boolean adapterProgressVisible;

        public ViewState(int progressVisibility, boolean adapterProgressVisible) {
            this.progressVisibility = progressVisibility;
            this.adapterProgressVisible = adapterProgressVisible;
        }
    }

    private static class LoadBroadcastListState {

        public boolean loadMore;
        public int count;

        public LoadBroadcastListState(boolean loadMore, int count) {
            this.loadMore = loadMore;
            this.count = count;
        }
    }

    private static class LikeBroadcastState {

        public long itemId;
        public long broadcastId;
        public boolean like;

        public LikeBroadcastState(long itemId, long broadcastId, boolean like) {
            this.itemId = itemId;
            this.broadcastId = broadcastId;
            this.like = like;
        }
    }

    private static class RebroadcastBroadcastState {

        public long itemId;
        public long broadcastId;
        public boolean rebroadcast;

        public RebroadcastBroadcastState(long itemId, long broadcastId, boolean rebroadcast) {
            this.itemId = itemId;
            this.broadcastId = broadcastId;
            this.rebroadcast = rebroadcast;
        }
    }
}
