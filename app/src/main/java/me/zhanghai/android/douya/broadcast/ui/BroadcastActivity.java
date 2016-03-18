/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.broadcast.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
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
import me.zhanghai.android.douya.eventbus.BroadcastCommentDeletedEvent;
import me.zhanghai.android.douya.eventbus.BroadcastDeletedEvent;
import me.zhanghai.android.douya.eventbus.BroadcastUpdatedEvent;
import me.zhanghai.android.douya.network.RequestFragment;
import me.zhanghai.android.douya.network.api.ApiContract.Response.Error.Codes;
import me.zhanghai.android.douya.network.api.ApiError;
import me.zhanghai.android.douya.network.api.ApiRequest;
import me.zhanghai.android.douya.network.api.ApiRequests;
import me.zhanghai.android.douya.network.api.info.Broadcast;
import me.zhanghai.android.douya.network.api.info.Comment;
import me.zhanghai.android.douya.network.api.info.CommentList;
import me.zhanghai.android.douya.ui.ClickableSimpleAdapter;
import me.zhanghai.android.douya.ui.LoadMoreAdapter;
import me.zhanghai.android.douya.ui.NoChangeAnimationItemAnimator;
import me.zhanghai.android.douya.ui.OnVerticalScrollListener;
import me.zhanghai.android.douya.util.CheatSheetUtils;
import me.zhanghai.android.douya.util.ClipboardUtils;
import me.zhanghai.android.douya.util.DoubanUtils;
import me.zhanghai.android.douya.util.ImeUtils;
import me.zhanghai.android.douya.util.LogUtils;
import me.zhanghai.android.douya.util.ToastUtils;
import me.zhanghai.android.douya.util.TransitionUtils;
import me.zhanghai.android.douya.util.ViewUtils;

public class BroadcastActivity extends AppCompatActivity implements RequestFragment.Listener,
        SingleBroadcastAdapter.OnActionListener, CommentActionDialogFragment.Listener,
        ConfirmDeleteCommentDialogFragment.Listener, ConfirmDeleteBroadcastDialogFragment.Listener {

    private static final int COMMENT_COUNT_PER_LOAD = 20;

    private static final int REQUEST_CODE_LOAD_BROADCAST = 0;
    private static final int REQUEST_CODE_LOAD_COMMENT_LIST = 1;
    private static final int REQUEST_CODE_LIKE = 2;
    private static final int REQUEST_CODE_REBROADCAST = 3;
    private static final int REQUEST_CODE_DELETE_COMMENT = 4;
    private static final int REQUEST_CODE_SEND_COMMENT = 5;
    private static final int REQUEST_CODE_DELETE_BROADCAST = 6;

    private static final String KEY_PREFIX = BroadcastActivity.class.getName() + '.';

    public static final String EXTRA_BROADCAST = KEY_PREFIX + "broadcast";
    public static final String EXTRA_BROADCAST_ID = KEY_PREFIX + "broadcast_id";
    public static final String EXTRA_COMMENT = KEY_PREFIX + "comment";

    private static final String RETAIN_DATA_KEY_BROADCAST = KEY_PREFIX + "broadcast";
    private static final String RETAIN_DATA_KEY_COMMENT_LIST = KEY_PREFIX + "comment_list";
    private static final String RETAIN_DATA_KEY_CAN_LOAD_MORE_COMMENTS = KEY_PREFIX
            + "can_load_more_comments";
    private static final String RETAIN_DATA_KEY_LOADING_BROADCAST_OR_COMMENT_LIST = KEY_PREFIX
            + "loading_broadcast_or_comment_list";
    private static final String RETAIN_DATA_KEY_SENDING_COMMENT = KEY_PREFIX + "sending_comment";
    private static final String RETAIN_DATA_KEY_VIEW_STATE = KEY_PREFIX + "view_state";

    @Bind(android.R.id.content)
    FrameLayout mContentLayout;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.swipe_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.broadcast_comment_list)
    RecyclerView mBroadcastCommentList;
    @Bind(R.id.progress)
    ProgressBar mProgress;
    @Bind(R.id.comment)
    EditText mCommentEdit;
    @Bind(R.id.send)
    ImageButton mSendButton;

    private long mBroadcastId;

    private RetainDataFragment mRetainDataFragment;

    private SingleBroadcastAdapter mBroadcastAdapter;
    private CommentAdapter mCommentAdapter;
    private LoadMoreAdapter mAdapter;
    private boolean mCanLoadMoreComments;

    private boolean mLoadingBroadcastOrCommentList;

    private boolean mSendingComment;

    public static Intent makeIntent(Broadcast broadcast, Context context) {
        return new Intent(context, BroadcastActivity.class)
                .putExtra(BroadcastActivity.EXTRA_BROADCAST, broadcast);
    }

    public static Intent makeIntent(Broadcast broadcast, boolean comment, Context context) {
        return makeIntent(broadcast, context)
                .putExtra(BroadcastActivity.EXTRA_COMMENT, comment);
    }

    public static Intent makeIntent(long broadcastId, Context context) {
        return new Intent(context, BroadcastActivity.class)
                .putExtra(BroadcastActivity.EXTRA_BROADCAST_ID, broadcastId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        TransitionUtils.setupTransitionBeforeDecorate(this);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.broadcast_activity);
        TransitionUtils.setEnterReturnExplode(this);
        TransitionUtils.setupTransitionAfterSetContentView(this);
        ButterKnife.bind(this);

        CustomTabsHelperFragment.attachTo(this);
        mRetainDataFragment = RetainDataFragment.attachTo(this);

        mContentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                supportFinishAfterTransition();
            }
        });

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadBroadcast(true);
            }
        });

        mBroadcastCommentList.setHasFixedSize(true);
        mBroadcastCommentList.setItemAnimator(new NoChangeAnimationItemAnimator());
        mBroadcastCommentList.setLayoutManager(new LinearLayoutManager(this));
        Broadcast broadcast = mRetainDataFragment.remove(RETAIN_DATA_KEY_BROADCAST);
        Intent intent = getIntent();
        // Be consistent with what the user will see first.
        if (broadcast == null) {
            broadcast = intent.getParcelableExtra(EXTRA_BROADCAST);
        }
        if (broadcast == null) {
            if (intent.hasExtra(EXTRA_BROADCAST_ID)) {
                mBroadcastId = intent.getLongExtra(EXTRA_BROADCAST_ID, -1);
            } else {
                // TODO: Read from uri.
                //broadcastId = intent.getData();
            }
        } else {
            mBroadcastId = broadcast.id;
        }
        mBroadcastAdapter = new SingleBroadcastAdapter(null, this);
        setBroadcast(broadcast);
        List<Comment> commentList = mRetainDataFragment.remove(RETAIN_DATA_KEY_COMMENT_LIST);
        mCommentAdapter = new CommentAdapter(commentList,
                new ClickableSimpleAdapter.OnItemClickListener<Comment, CommentAdapter.ViewHolder>() {
                    @Override
                    public void onItemClick(RecyclerView parent, Comment item,
                                            CommentAdapter.ViewHolder holder) {
                        onShowCommentAction(item);
                    }
                });
        mAdapter = new LoadMoreAdapter(R.layout.load_more_item, mBroadcastAdapter, mCommentAdapter);
        mBroadcastCommentList.setAdapter(mAdapter);
        mBroadcastCommentList.addOnScrollListener(new OnVerticalScrollListener() {
            public void onScrolledToBottom() {
                loadCommentList(true);
            }
        });

        mCanLoadMoreComments = mRetainDataFragment.removeBoolean(
                RETAIN_DATA_KEY_CAN_LOAD_MORE_COMMENTS, true);

        CheatSheetUtils.setup(mSendButton);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSendComment();
            }
        });

        mLoadingBroadcastOrCommentList = mRetainDataFragment.removeBoolean(
                RETAIN_DATA_KEY_LOADING_BROADCAST_OR_COMMENT_LIST, false);

        Boolean sendingComment = mRetainDataFragment.remove(RETAIN_DATA_KEY_SENDING_COMMENT);
        if (sendingComment != null) {
            setSendingComment(sendingComment);
        }

        // View only saves state influenced by user action, so we have to do this ourselves.
        ViewState viewState = mRetainDataFragment.remove(RETAIN_DATA_KEY_VIEW_STATE);
        if (viewState != null) {
            onRestoreViewState(viewState);
        }

        if (savedInstanceState == null) {
            boolean comment = getIntent().getBooleanExtra(EXTRA_COMMENT, false);
            if (comment) {
                TransitionUtils.postAfterTransition(this, new Runnable() {
                    @Override
                    public void run() {
                        onComment();
                    }
                });
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        mRetainDataFragment.put(RETAIN_DATA_KEY_BROADCAST, mBroadcastAdapter.getBroadcast());
        mRetainDataFragment.put(RETAIN_DATA_KEY_COMMENT_LIST, mCommentAdapter.getList());
        mRetainDataFragment.put(RETAIN_DATA_KEY_CAN_LOAD_MORE_COMMENTS, mCanLoadMoreComments);
        mRetainDataFragment.put(RETAIN_DATA_KEY_LOADING_BROADCAST_OR_COMMENT_LIST,
                mLoadingBroadcastOrCommentList);
        mRetainDataFragment.put(RETAIN_DATA_KEY_SENDING_COMMENT, mSendingComment);
        mRetainDataFragment.put(RETAIN_DATA_KEY_VIEW_STATE, onSaveViewState());
    }

    private ViewState onSaveViewState() {
        return new ViewState(mProgress.getVisibility(), mAdapter.isProgressVisible());
    }

    private void onRestoreViewState(ViewState state) {
        mProgress.setVisibility(state.progressVisibility);
        mAdapter.setProgressVisible(state.adapterProgressVisible);
    }

    @Override
    public void onStart(){
        super.onStart();

        // Only auto-load when initially empty, not loaded but empty.
        boolean autoLoadComments = mCommentAdapter.getItemCount() == 0 && mCanLoadMoreComments;
        if (mBroadcastAdapter.getItemCount() == 0) {
            loadBroadcast(autoLoadComments);
        } else if (autoLoadComments) {
            loadCommentList(false);
        }

        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        EventBus.getDefault().unregister(this);
    }

    @Override
    public void finishAfterTransition() {

        // This magically gives better visual effect when the broadcast is partially visible. Using
        // setEnterSharedElementCallback() disables this hack when no transition is used to start
        // this Activity.
        setEnterSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onSharedElementEnd(List<String> sharedElementNames,
                                           List<View> sharedElements,
                                           List<View> sharedElementSnapshots) {
                mBroadcastCommentList.scrollToPosition(0);
            }
        });

        super.finishAfterTransition();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.broadcast, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        Broadcast broadcast = mBroadcastAdapter.getBroadcast();
        boolean hasBroadcast = broadcast != null;
        menu.findItem(R.id.action_copy_text).setVisible(hasBroadcast);
        boolean canDelete = hasBroadcast && broadcast.author.id == AccountUtils.getUserId(this);
        menu.findItem(R.id.action_delete).setVisible(canDelete);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
            case R.id.action_copy_text:
                copyText();
                return true;
            case R.id.action_delete:
                onDeleteBroadcast();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onVolleyResponse(int requestCode, boolean successful, Object result,
                                 VolleyError error, Object requestState) {
        switch (requestCode) {
            case REQUEST_CODE_LOAD_BROADCAST:
                onLoadBroadcastResponse(successful, (Broadcast) result, error,
                        (LoadBroadcastState) requestState);
                break;
            case REQUEST_CODE_LOAD_COMMENT_LIST:
                onLoadCommentListResponse(successful, (CommentList) result, error,
                        (LoadCommentListState) requestState);
                break;
            case REQUEST_CODE_LIKE:
                onLikeResponse(successful, (Broadcast) result, error, (LikeState) requestState);
                break;
            case REQUEST_CODE_REBROADCAST:
                onRebroadcastResponse(successful, (Broadcast) result, error,
                        (RebroadcastState) requestState);
                break;
            case REQUEST_CODE_DELETE_COMMENT:
                onDeleteCommentResponse(successful, (Boolean) result, error,
                        (DeleteCommentState) requestState);
                break;
            case REQUEST_CODE_SEND_COMMENT:
                onSendCommentResponse(successful, (Comment) result, error);
                break;
            case REQUEST_CODE_DELETE_BROADCAST:
                onDeleteBroadcastResponse(successful, (Broadcast) result, error);
                break;
            default:
                LogUtils.w("Unknown request code " + requestCode + ", with successful=" + successful
                        + ", result=" + result + ", error=" + error);
        }
    }

    private void loadBroadcast(boolean loadCommentList) {

        if (mLoadingBroadcastOrCommentList) {
            return;
        }

        ApiRequest<Broadcast> request = ApiRequests.newBroadcastRequest(mBroadcastId, this);
        LoadBroadcastState state = new LoadBroadcastState(loadCommentList);
        RequestFragment.startRequest(REQUEST_CODE_LOAD_BROADCAST, request, state, this);

        mLoadingBroadcastOrCommentList = true;
        setBroadcastRefreshing(true);
    }

    private void onLoadBroadcastResponse(boolean successful, Broadcast result, VolleyError error,
                                         LoadBroadcastState state) {

        if (successful) {
            setBroadcast(result);
        } else {
            LogUtils.e(error.toString());
            ToastUtils.show(ApiError.getErrorString(error, this), this);
        }

        setBroadcastRefreshing(false);
        mLoadingBroadcastOrCommentList = false;

        if (successful && state.loadCommentList) {
            loadCommentList(false);
        }
    }

    private void setBroadcast(Broadcast broadcast) {
        mBroadcastAdapter.setBroadcast(broadcast);
        updateSendCommentEnabled();
    }

    private void setBroadcastRefreshing(boolean refreshing) {
        mSwipeRefreshLayout.setEnabled(!refreshing);
        if (!refreshing) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
        ViewUtils.setVisibleOrGone(mProgress, refreshing
                && mBroadcastAdapter.getItemCount() == 0);
    }

    private void loadCommentList(boolean loadMore) {

        if (mLoadingBroadcastOrCommentList || (loadMore && !mCanLoadMoreComments)) {
            return;
        }

        Integer start = loadMore ? mCommentAdapter.getItemCount() : null;
        final int count = COMMENT_COUNT_PER_LOAD;
        ApiRequest<CommentList> request = ApiRequests.newBroadcastCommentListRequest(mBroadcastId,
                start, count, this);
        LoadCommentListState state = new LoadCommentListState(loadMore, count);
        RequestFragment.startRequest(REQUEST_CODE_LOAD_COMMENT_LIST, request, state, this);

        mLoadingBroadcastOrCommentList = true;
        setCommentsRefreshing(true, loadMore);
    }

    private void onLoadCommentListResponse(boolean successful, CommentList result,
                                           VolleyError error, LoadCommentListState state) {

        if (successful) {
            List<Comment> commentList = result.comments;
            mCanLoadMoreComments = commentList.size() == state.count;
            if (state.loadMore) {
                mCommentAdapter.addAll(commentList);
            } else {
                mCommentAdapter.replace(commentList);
            }
        } else {
            LogUtils.e(error.toString());
            ToastUtils.show(ApiError.getErrorString(error, this), this);
        }

        setCommentsRefreshing(false, state.loadMore);
        mLoadingBroadcastOrCommentList = false;

        if (successful) {
            fixCommentCount();
        }
    }

    private void setCommentsRefreshing(boolean refreshing, boolean loadMore) {
        mAdapter.setProgressVisible(refreshing && (mCommentAdapter.getItemCount() == 0
                || loadMore));
    }

    @Override
    public boolean onLike(boolean like) {

        if (mBroadcastAdapter.hasBroadcast()
                && mBroadcastAdapter.getBroadcast().author.id == AccountUtils.getUserId(this)) {
            ToastUtils.show(R.string.broadcast_like_error_cannot_like_oneself, this);
            return false;
        }

        ApiRequest<Broadcast> request = ApiRequests.newLikeBroadcastRequest(mBroadcastId, like,
                this);
        LikeState state = new LikeState(like);
        RequestFragment.startRequest(REQUEST_CODE_LIKE, request, state, this);
        return true;
    }

    private void onLikeResponse(boolean successful, Broadcast result, VolleyError error,
                                LikeState state) {

        if (successful) {

            EventBus.getDefault().post(new BroadcastUpdatedEvent(result));
            ToastUtils.show(state.like ? R.string.broadcast_like_successful
                    : R.string.broadcast_unlike_successful, this);

        } else {

            LogUtils.e(error.toString());
            Broadcast broadcast = mBroadcastAdapter.getBroadcast();
            if (broadcast != null) {
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
                        broadcast.fixLiked(shouldBeLiked);
                        EventBus.getDefault().post(new BroadcastUpdatedEvent(broadcast));
                        notified = true;
                    }
                }
                if (!notified) {
                    // Must notify changed to reset pending status so that off-screen
                    // items will be invalidated.
                    mBroadcastAdapter.notifyBroadcastChanged();
                }
            }
            ToastUtils.show(getString(state.like ? R.string.broadcast_like_failed_format
                            : R.string.broadcast_unlike_failed_format,
                    ApiError.getErrorString(error, this)), this);
        }
    }

    @Override
    public boolean onRebroadcast(boolean rebroadcast) {

        if (mBroadcastAdapter.hasBroadcast()
                && mBroadcastAdapter.getBroadcast().author.id == AccountUtils.getUserId(this)) {
            ToastUtils.show(R.string.broadcast_rebroadcast_error_cannot_rebroadcast_oneself, this);
            return false;
        }

        ApiRequest<Broadcast> request = ApiRequests.newRebroadcastBroadcastRequest(mBroadcastId,
                rebroadcast, this);
        RebroadcastState state = new RebroadcastState(rebroadcast);
        RequestFragment.startRequest(REQUEST_CODE_REBROADCAST, request, state, this);
        return true;
    }

    private void onRebroadcastResponse(boolean successful, Broadcast result, VolleyError error,
                                       RebroadcastState state) {

        if (successful) {

            if (!state.rebroadcast) {
                // Delete the rebroadcast broadcast by user. Must be done before we update the
                // broadcast so that we can retrieve rebroadcastId for the old one.
                // This will not finish this activity, because this activity displays the
                // rebroadcasted broadcast instead of the rebroadcast broadcast itself, and this is
                // the desired behavior since it won't surprise user, and user can have the chance
                // to undo it.
                Broadcast broadcast = mBroadcastAdapter.getBroadcast();
                if (broadcast != null && broadcast.rebroadcastId != null) {
                    EventBus.getDefault().post(new BroadcastDeletedEvent(broadcast.rebroadcastId));
                }
            }
            EventBus.getDefault().post(new BroadcastUpdatedEvent(result));
            ToastUtils.show(state.rebroadcast ? R.string.broadcast_rebroadcast_successful
                    : R.string.broadcast_unrebroadcast_successful, this);

        } else {

            LogUtils.e(error.toString());
            Broadcast broadcast = mBroadcastAdapter.getBroadcast();
            if (broadcast != null) {
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
                        broadcast.fixRebroacasted(shouldBeRebroadcasted);
                        EventBus.getDefault().post(new BroadcastUpdatedEvent(broadcast));
                        notified = true;
                    }
                }
                if (!notified) {
                    // Must notify changed to reset pending status so that off-screen
                    // items will be invalidated.
                    mBroadcastAdapter.notifyBroadcastChanged();
                }
            }
            ToastUtils.show(getString(state.rebroadcast ?
                            R.string.broadcast_rebroadcast_failed_format
                            : R.string.broadcast_unrebroadcast_failed_format,
                    ApiError.getErrorString(error, this)), this);
        }
    }

    @Override
    public void onComment() {
        if (canSendComment()) {
            ImeUtils.showIme(mCommentEdit);
        } else {
            ToastUtils.show(R.string.broadcast_send_comment_disabled, this);
        }
    }

    @Override
    public void onViewActivity() {
        BroadcastActivityDialogFragment.show(mBroadcastAdapter.getBroadcast(), this);
    }

    private void onShowCommentAction(Comment comment) {
        boolean canReplyTo = canSendComment();
        long userId = AccountUtils.getUserId(this);
        boolean canDelete = (mBroadcastAdapter.hasBroadcast()
                && mBroadcastAdapter.getBroadcast().author.id == userId)
                || comment.author.id == userId;
        CommentActionDialogFragment.show(comment, canReplyTo, canDelete, this);
    }

    @Override
    public void onReplyToComment(Comment comment) {
        mCommentEdit.getText().replace(mCommentEdit.getSelectionStart(),
                mCommentEdit.getSelectionEnd(), DoubanUtils.getAtUserString(comment.author));
        onComment();
    }

    @Override
    public void onCopyCommentText(Comment comment) {
        ClipboardUtils.copyText(comment.getClipboardLabel(), comment.getClipboardText(this), this);
    }

    @Override
    public void onDeleteComment(Comment comment) {
        ConfirmDeleteCommentDialogFragment.show(comment, this);
    }

    @Override
    public void deleteComment(Comment comment) {
        RequestFragment.startRequest(REQUEST_CODE_DELETE_COMMENT,
                ApiRequests.newDeleteBroadcastCommentRequest(mBroadcastId, comment.id, this),
                new DeleteCommentState(comment.id), this);
    }

    private void onDeleteCommentResponse(boolean successful, Boolean result, VolleyError error,
                                         DeleteCommentState state) {
        if (successful) {
            ToastUtils.show(R.string.broadcast_comment_delete_successful, this);
            EventBus.getDefault().post(new BroadcastCommentDeletedEvent(mBroadcastId,
                    state.commentId));
            if (mBroadcastAdapter.hasBroadcast()) {
                Broadcast broadcast = mBroadcastAdapter.getBroadcast();
                --broadcast.commentCount;
                EventBus.getDefault().post(new BroadcastUpdatedEvent(broadcast));
            }
        } else {
            LogUtils.e(error.toString());
            ToastUtils.show(getString(R.string.broadcast_comment_delete_failed_format,
                    ApiError.getErrorString(error, this)), this);
        }
    }

    private void onSendComment() {

        String comment = mCommentEdit.getText().toString();
        if (TextUtils.isEmpty(comment)) {
            ToastUtils.show(R.string.broadcast_send_comment_error_empty, this);
            return;
        }

        sendComment(comment);
    }

    private void sendComment(String comment) {

        ApiRequest<Comment> request = ApiRequests.newSendBroadcastCommentRequest(mBroadcastId,
                comment, this);
        RequestFragment.startRequest(REQUEST_CODE_SEND_COMMENT, request, null, this);

        setSendingComment(true);
    }

    private void onSendCommentResponse(boolean successful, Comment result, VolleyError error) {

        if (successful) {
            if (!mCanLoadMoreComments) {
                mCommentAdapter.add(result);
                fixCommentCount();
            } else {
                ToastUtils.show(R.string.broadcast_send_comment_successful, this);
            }
            mBroadcastCommentList.scrollToPosition(mAdapter.getItemCount() - 1);
            mCommentEdit.setText(null);
        } else {
            LogUtils.e(error.toString());
            ToastUtils.show(getString(R.string.broadcast_send_comment_failed_format,
                    ApiError.getErrorString(error, this)), this);
        }

        setSendingComment(false);
    }

    private boolean canSendComment() {
        return mBroadcastAdapter.hasBroadcast() && mBroadcastAdapter.getBroadcast().canComment();
    }

    private void updateSendCommentEnabled() {
        boolean canSendComment = canSendComment();
        boolean enabled = canSendComment && !mSendingComment;
        mCommentEdit.setEnabled(enabled);
        mSendButton.setEnabled(enabled);
        mCommentEdit.setHint(canSendComment ? R.string.broadcast_send_comment_hint
                : R.string.broadcast_send_comment_hint_disabled);
    }

    private void setSendingComment(boolean sendingComment) {
        mSendingComment = sendingComment;
        updateSendCommentEnabled();
    }

    private void fixCommentCount() {
        Broadcast broadcast = mBroadcastAdapter.getBroadcast();
        if (broadcast != null) {
            int commentCount = mCommentAdapter.getItemCount();
            if (broadcast.commentCount < commentCount) {
                broadcast.commentCount = commentCount;
                EventBus.getDefault().post(new BroadcastUpdatedEvent(broadcast));
            }
        }
    }

    private void copyText() {

        Broadcast broadcast = mBroadcastAdapter.getBroadcast();
        if (broadcast == null) {
            ToastUtils.show(R.string.broadcast_copy_text_not_loaded, this);
            return;
        }

        ClipboardUtils.copyText(broadcast.getClipboradLabel(), broadcast.getClipboardText(this),
                this);
    }

    private void onDeleteBroadcast() {
        ConfirmDeleteBroadcastDialogFragment.show(this);
    }

    @Override
    public void deleteBroadcast() {
        RequestFragment.startRequest(REQUEST_CODE_DELETE_BROADCAST,
                ApiRequests.newDeleteBroadcastRequest(mBroadcastId, this), null, this);
    }

    private void onDeleteBroadcastResponse(boolean successful, Broadcast result,
                                           VolleyError error) {
        if (successful) {
            ToastUtils.show(R.string.broadcast_delete_successful, this);
            EventBus.getDefault().post(new BroadcastDeletedEvent(mBroadcastId));
            finish();
        } else {
            LogUtils.e(error.toString());
            ToastUtils.show(getString(R.string.broadcast_delete_failed_format,
                    ApiError.getErrorString(error, this)), this);
        }
    }

    @Keep
    public void onEventMainThread(BroadcastUpdatedEvent event) {
        Broadcast broadcast = event.broadcast;
        if (broadcast.id == mBroadcastId) {
            setBroadcast(broadcast);
        }
    }

    @Keep
    public void onEventMainThread(BroadcastCommentDeletedEvent event) {
        if (event.broadcastId == mBroadcastId) {
            mCommentAdapter.removeById(event.commentId);
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

    private static class LoadBroadcastState {

        public boolean loadCommentList;

        public LoadBroadcastState(boolean loadCommentList) {
            this.loadCommentList = loadCommentList;
        }
    }

    private static class LoadCommentListState {

        public boolean loadMore;
        public int count;

        public LoadCommentListState(boolean loadMore, int count) {
            this.loadMore = loadMore;
            this.count = count;
        }
    }

    private static class LikeState {

        public boolean like;

        public LikeState(boolean like) {
            this.like = like;
        }
    }

    private static class RebroadcastState {

        public boolean rebroadcast;

        public RebroadcastState(boolean rebroadcast) {
            this.rebroadcast = rebroadcast;
        }
    }

    private static class DeleteCommentState {

        public long commentId;

        public DeleteCommentState(long commentId) {
            this.commentId = commentId;
        }
    }
}
