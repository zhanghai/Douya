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
import android.support.v4.view.ViewCompat;
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
import me.zhanghai.android.customtabshelper.CustomTabsHelperFragment;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.app.RetainDataFragment;
import me.zhanghai.android.douya.broadcast.content.BroadcastCommentCountFixer;
import me.zhanghai.android.douya.broadcast.content.BroadcastCommentListResource;
import me.zhanghai.android.douya.broadcast.content.BroadcastResource;
import me.zhanghai.android.douya.broadcast.content.DeleteBroadcastCommentManager;
import me.zhanghai.android.douya.broadcast.content.DeleteBroadcastManager;
import me.zhanghai.android.douya.broadcast.content.LikeBroadcastManager;
import me.zhanghai.android.douya.broadcast.content.RebroadcastBroadcastManager;
import me.zhanghai.android.douya.broadcast.content.SendBroadcastCommentManager;
import me.zhanghai.android.douya.eventbus.BroadcastCommentSendErrorEvent;
import me.zhanghai.android.douya.eventbus.BroadcastCommentSentEvent;
import me.zhanghai.android.douya.eventbus.EventBusUtils;
import me.zhanghai.android.douya.network.api.ApiError;
import me.zhanghai.android.douya.network.api.info.Broadcast;
import me.zhanghai.android.douya.network.api.info.Comment;
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

// TODO: Split into BroadcastFragment.
public class BroadcastActivity extends AppCompatActivity implements BroadcastResource.Listener,
        SingleBroadcastAdapter.Listener, BroadcastCommentListResource.Listener,
        CommentActionDialogFragment.Listener, ConfirmDeleteCommentDialogFragment.Listener,
        ConfirmDeleteBroadcastDialogFragment.Listener {

    private static final String KEY_PREFIX = BroadcastActivity.class.getName() + '.';

    public static final String EXTRA_BROADCAST = KEY_PREFIX + "broadcast";
    public static final String EXTRA_BROADCAST_ID = KEY_PREFIX + "broadcast_id";
    public static final String EXTRA_COMMENT = KEY_PREFIX + "comment";

    private static final String RETAIN_DATA_KEY_VIEW_STATE = KEY_PREFIX + "view_state";

    @Bind(android.R.id.content)
    FrameLayout mContentLayout;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.shared)
    View mSharedView;
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

    private BroadcastResource mBroadcastResource;
    private BroadcastCommentListResource mCommentListResource;
    private RetainDataFragment mRetainDataFragment;

    private SingleBroadcastAdapter mBroadcastAdapter;
    private CommentAdapter mCommentAdapter;
    private LoadMoreAdapter mAdapter;

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
        Intent intent = getIntent();
        long broadcastId = intent.getLongExtra(EXTRA_BROADCAST_ID, -1);
        Broadcast broadcast = intent.getParcelableExtra(EXTRA_BROADCAST);
        if (broadcast != null) {
            // Be consistent with what the user will see first.
            broadcastId = broadcast.id;
        }
        mBroadcastResource = BroadcastResource.attachTo(broadcastId, broadcast, this);
        mCommentListResource = BroadcastCommentListResource.attachTo(broadcastId, this);
        mRetainDataFragment = RetainDataFragment.attachTo(this);

        mContentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                supportFinishAfterTransition();
            }
        });

        setSupportActionBar(mToolbar);

        ViewCompat.setTransitionName(mSharedView, Broadcast.makeTransitionName(broadcastId));

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mBroadcastResource.load();
                mCommentListResource.load(false);
            }
        });

        mBroadcastCommentList.setHasFixedSize(true);
        mBroadcastCommentList.setItemAnimator(new NoChangeAnimationItemAnimator());
        mBroadcastCommentList.setLayoutManager(new LinearLayoutManager(this));
        mBroadcastAdapter = new SingleBroadcastAdapter(null, this);
        setBroadcast(mBroadcastResource.get());
        mCommentAdapter = new CommentAdapter(mCommentListResource.get(),
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
                mCommentListResource.load(true);
            }
        });

        CheatSheetUtils.setup(mSendButton);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSendComment();
            }
        });
        updateSendCommentStatus();

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
                        onShowSendComment();
                    }
                });
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        EventBusUtils.register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        EventBusUtils.unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mBroadcastResource.detach();
        mCommentListResource.detach();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

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

        Broadcast broadcast = mBroadcastResource.get();
        boolean hasBroadcast = broadcast != null;
        menu.findItem(R.id.action_copy_text).setVisible(hasBroadcast);
        boolean canDelete = hasBroadcast && broadcast.isAuthorOneself(this);
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
    public void onLoadBroadcastStarted(int requestCode) {
        setBroadcastRefreshing(true);
    }

    @Override
    public void onLoadBroadcastFinished(int requestCode) {
        setBroadcastRefreshing(false);
    }

    @Override
    public void onLoadBroadcastError(int requestCode, VolleyError error) {
        LogUtils.e(error.toString());
        ToastUtils.show(ApiError.getErrorString(error, this), this);
    }

    @Override
    public void onBroadcastChanged(int requestCode, Broadcast newBroadcast) {
        setBroadcast(newBroadcast);
    }

    @Override
    public void onBroadcastRemoved(int requestCode) {
        finish();
    }

    @Override
    public void onBroadcastWriteStarted(int requestCode) {
        mBroadcastAdapter.notifyBroadcastChanged();
    }

    @Override
    public void onBroadcastWriteFinished(int requestCode) {
        mBroadcastAdapter.notifyBroadcastChanged();
    }

    @Override
    public void onLoadCommentListStarted(int requestCode, boolean loadMore) {
        setCommentsRefreshing(true, loadMore);
    }

    @Override
    public void onLoadCommentListFinished(int requestCode, boolean loadMore) {
        setCommentsRefreshing(false, loadMore);
    }

    @Override
    public void onLoadCommentListError(int requestCode, VolleyError error) {
        LogUtils.e(error.toString());
        ToastUtils.show(ApiError.getErrorString(error, this), this);
    }

    @Override
    public void onCommentListChanged(int requestCode, List<Comment> newCommentList) {
        mCommentAdapter.replace(newCommentList);
        BroadcastCommentCountFixer.onCommentListChanged(mBroadcastResource, mCommentListResource);
    }

    @Override
    public void onCommentListAppended(int requestCode, List<Comment> appendedCommentList) {
        mCommentAdapter.addAll(appendedCommentList);
        BroadcastCommentCountFixer.onCommentListChanged(mBroadcastResource, mCommentListResource);
    }

    @Override
    public void onCommentRemoved(int requestCode, int position) {
        mCommentAdapter.remove(position);
        BroadcastCommentCountFixer.onCommentRemoved(mBroadcastResource);
    }

    private void setBroadcast(Broadcast broadcast) {
        mBroadcastAdapter.setBroadcast(broadcast);
        updateSendCommentStatus();
    }

    private void setBroadcastRefreshing(boolean refreshing) {
        mSwipeRefreshLayout.setEnabled(!refreshing);
        if (!refreshing) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
        ViewUtils.setVisibleOrGone(mProgress, refreshing
                && mBroadcastAdapter.getItemCount() == 0);
    }

    private void setCommentsRefreshing(boolean refreshing, boolean loadMore) {
        mAdapter.setProgressVisible(refreshing && (mCommentAdapter.getItemCount() == 0
                || loadMore));
    }

    @Override
    public void onLike(Broadcast broadcast, boolean like) {
        LikeBroadcastManager.getInstance().write(broadcast, like, this);
    }

    @Override
    public void onRebroadcast(Broadcast broadcast, boolean rebroadcast) {
        RebroadcastBroadcastManager.getInstance().write(broadcast, rebroadcast, this);
    }

    @Override
    public void onComment(Broadcast broadcast) {
        onShowSendComment();
    }

    @Override
    public void onViewActivity(Broadcast broadcast) {
        BroadcastActivityDialogFragment.show(broadcast, this);
    }

    private void onShowCommentAction(Comment comment) {
        boolean canReplyTo = canSendComment();
        boolean canDelete = (mBroadcastAdapter.hasBroadcast()
                && mBroadcastAdapter.getBroadcast().isAuthorOneself(this))
                || comment.isAuthorOneself(this);
        CommentActionDialogFragment.show(comment, canReplyTo, canDelete, this);
    }

    @Override
    public void onReplyToComment(Comment comment) {
        mCommentEdit.getText().replace(mCommentEdit.getSelectionStart(),
                mCommentEdit.getSelectionEnd(), DoubanUtils.getAtUserString(comment.author));
        onShowSendComment();
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
        DeleteBroadcastCommentManager.getInstance().write(mBroadcastResource.getBroadcastId(),
                comment.id, this);
    }

    private void onShowSendComment() {
        if (canSendComment()) {
            ImeUtils.showIme(mCommentEdit);
        } else {
            ToastUtils.show(R.string.broadcast_send_comment_disabled, this);
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

        SendBroadcastCommentManager.getInstance().write(mBroadcastResource.getBroadcastId(),
                comment, this);

        updateSendCommentStatus();
    }

    @Keep
    public void onEventMainThread(BroadcastCommentSentEvent event) {
        if (event.broadcastId == mBroadcastResource.getBroadcastId()) {
            mBroadcastCommentList.scrollToPosition(mAdapter.getItemCount() - 1);
            mCommentEdit.setText(null);
            updateSendCommentStatus();
        }
    }

    @Keep
    public void onEventMainThread(BroadcastCommentSendErrorEvent event) {
        if (event.broadcastId == mBroadcastResource.getBroadcastId()) {
            updateSendCommentStatus();
        }
    }

    private boolean canSendComment() {
        Broadcast broadcast = mBroadcastResource.get();
        return broadcast != null && broadcast.canComment();
    }

    private void updateSendCommentStatus() {
        boolean canSendComment = canSendComment();
        SendBroadcastCommentManager manager = SendBroadcastCommentManager.getInstance();
        long broadcastId = mBroadcastResource.getBroadcastId();
        boolean sendingComment = manager.isWriting(broadcastId);
        boolean enabled = canSendComment && !sendingComment;
        mCommentEdit.setEnabled(enabled);
        mSendButton.setEnabled(enabled);
        mCommentEdit.setHint(canSendComment ? R.string.broadcast_send_comment_hint
                : R.string.broadcast_send_comment_hint_disabled);
        if (sendingComment) {
            mCommentEdit.setText(manager.getComment(broadcastId));
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
        DeleteBroadcastManager.getInstance().write(mBroadcastResource.getBroadcastId(), this);
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
