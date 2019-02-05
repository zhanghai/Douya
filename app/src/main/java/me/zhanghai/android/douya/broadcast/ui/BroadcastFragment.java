/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.broadcast.ui;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.app.SharedElementCallback;
import androidx.core.view.ViewCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.customtabshelper.CustomTabsHelperFragment;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.broadcast.content.BroadcastAndCommentListResource;
import me.zhanghai.android.douya.broadcast.content.BroadcastCommentCountFixer;
import me.zhanghai.android.douya.broadcast.content.DeleteBroadcastCommentManager;
import me.zhanghai.android.douya.broadcast.content.DeleteBroadcastManager;
import me.zhanghai.android.douya.broadcast.content.LikeBroadcastManager;
import me.zhanghai.android.douya.broadcast.content.RebroadcastBroadcastManager;
import me.zhanghai.android.douya.broadcast.content.SendBroadcastCommentManager;
import me.zhanghai.android.douya.eventbus.BroadcastCommentSendErrorEvent;
import me.zhanghai.android.douya.eventbus.BroadcastCommentSentEvent;
import me.zhanghai.android.douya.eventbus.EventBusUtils;
import me.zhanghai.android.douya.network.api.ApiError;
import me.zhanghai.android.douya.network.api.info.frodo.Broadcast;
import me.zhanghai.android.douya.network.api.info.frodo.Comment;
import me.zhanghai.android.douya.settings.info.Settings;
import me.zhanghai.android.douya.ui.ConfirmDiscardContentDialogFragment;
import me.zhanghai.android.douya.ui.DoubleClickToolbar;
import me.zhanghai.android.douya.ui.FragmentFinishable;
import me.zhanghai.android.douya.ui.GetOnLongClickListenerImageButton;
import me.zhanghai.android.douya.ui.LoadMoreAdapter;
import me.zhanghai.android.douya.ui.NoChangeAnimationItemAnimator;
import me.zhanghai.android.douya.ui.OnVerticalScrollListener;
import me.zhanghai.android.douya.ui.WebViewActivity;
import me.zhanghai.android.douya.util.ClipboardUtils;
import me.zhanghai.android.douya.util.DoubanUtils;
import me.zhanghai.android.douya.util.FragmentUtils;
import me.zhanghai.android.douya.util.ImeUtils;
import me.zhanghai.android.douya.util.LogUtils;
import me.zhanghai.android.douya.util.ShareUtils;
import me.zhanghai.android.douya.util.ToastUtils;
import me.zhanghai.android.douya.util.TooltipUtils;
import me.zhanghai.android.douya.util.TransitionUtils;
import me.zhanghai.android.douya.util.ViewUtils;

public class BroadcastFragment extends Fragment implements BroadcastAndCommentListResource.Listener,
        SingleBroadcastAdapter.Listener, ConfirmUnrebroadcastBroadcastDialogFragment.Listener,
        CommentActionDialogFragment.Listener, ConfirmDeleteCommentDialogFragment.Listener,
        ConfirmDeleteBroadcastDialogFragment.Listener,
        ConfirmDiscardContentDialogFragment.Listener {

    private static final String KEY_PREFIX = BroadcastFragment.class.getName() + '.';

    private static final String EXTRA_BROADCAST_ID = KEY_PREFIX + "broadcast_id";
    private static final String EXTRA_BROADCAST = KEY_PREFIX + "broadcast";
    private static final String EXTRA_SHOW_SEND_COMMENT = KEY_PREFIX + "show_send_comment";
    private static final String EXTRA_TITLE = KEY_PREFIX + "title";

    @BindView(R.id.container)
    FrameLayout mContainerLayout;
    @BindView(R.id.toolbar)
    DoubleClickToolbar mToolbar;
    @BindView(R.id.shared)
    View mSharedView;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.broadcast_comment_list)
    RecyclerView mBroadcastCommentList;
    @BindView(R.id.progress)
    ProgressBar mProgress;
    @BindView(R.id.comment)
    EditText mCommentEdit;
    @BindView(R.id.send)
    GetOnLongClickListenerImageButton mSendButton;

    private MenuItem mCopyTextMenuItem;
    private MenuItem mDeleteMenuItem;

    private long mBroadcastId;
    private Broadcast mBroadcast;
    private boolean mShowSendComment;
    private String mTitle;

    private BroadcastAndCommentListResource mResource;

    private SingleBroadcastAdapter mBroadcastAdapter;
    private CommentAdapter mCommentAdapter;
    private LoadMoreAdapter mAdapter;

    public static BroadcastFragment newInstance(long broadcastId, Broadcast broadcast,
                                                boolean showSendComment, String title) {
        //noinspection deprecation
        BroadcastFragment fragment = new BroadcastFragment();
        FragmentUtils.getArgumentsBuilder(fragment)
                .putLong(EXTRA_BROADCAST_ID, broadcastId)
                .putParcelable(EXTRA_BROADCAST, broadcast)
                .putBoolean(EXTRA_SHOW_SEND_COMMENT, showSendComment)
                .putString(EXTRA_TITLE, title);
        return fragment;
    }

    /**
     * @deprecated Use {@link #newInstance(long, Broadcast, boolean, String)} instead.
     */
    public BroadcastFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        mBroadcast = arguments.getParcelable(EXTRA_BROADCAST);
        if (mBroadcast != null) {
            mBroadcastId = mBroadcast.id;
        } else {
            mBroadcastId = arguments.getLong(EXTRA_BROADCAST_ID);
        }
        mShowSendComment = arguments.getBoolean(EXTRA_SHOW_SEND_COMMENT);
        mTitle = arguments.getString(EXTRA_TITLE);

        setHasOptionsMenu(true);

        EventBusUtils.register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.broadcast_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        CustomTabsHelperFragment.attachTo(this);
        mResource = BroadcastAndCommentListResource.attachTo(mBroadcastId,
                mBroadcast, this);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setTitle(getTitle());
        activity.setSupportActionBar(mToolbar);

        mContainerLayout.setOnClickListener(view -> onFinish());
        ViewCompat.setTransitionName(mSharedView, Broadcast.makeTransitionName(mBroadcastId));
        // This magically gives better visual effect when the broadcast is partially visible. Using
        // setEnterSharedElementCallback() disables this hack when no transition is used to start
        // this Activity.
        ActivityCompat.setEnterSharedElementCallback(activity, new SharedElementCallback() {
            @Override
            public void onSharedElementEnd(List<String> sharedElementNames,
                                           List<View> sharedElements,
                                           List<View> sharedElementSnapshots) {
                mBroadcastCommentList.scrollToPosition(0);
            }
        });

        mToolbar.setOnDoubleClickListener(view -> {
            mBroadcastCommentList.smoothScrollToPosition(0);
            return true;
        });

        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            mResource.loadBroadcast();
            mResource.loadCommentList(false);
        });

        mBroadcastCommentList.setHasFixedSize(true);
        mBroadcastCommentList.setItemAnimator(new NoChangeAnimationItemAnimator());
        mBroadcastCommentList.setLayoutManager(new LinearLayoutManager(activity));
        mBroadcastAdapter = new SingleBroadcastAdapter(null, this);
        // BroadcastLayout will take care of showing the effective broadcast.
        //noinspection deprecation
        setBroadcast(mResource.getBroadcast());
        mCommentAdapter = new CommentAdapter(mResource.getCommentList(),
                (parent, itemView, item, position) -> onShowCommentAction(item));
        mAdapter = new LoadMoreAdapter(mBroadcastAdapter, mCommentAdapter);
        mBroadcastCommentList.setAdapter(mAdapter);
        mBroadcastCommentList.addOnScrollListener(new OnVerticalScrollListener() {
            public void onScrolledToBottom() {
                mResource.loadCommentList(true);
            }
        });

        mSendButton.setOnClickListener(view -> onSendComment());
        TooltipUtils.setup(mSendButton);
        View.OnLongClickListener sendTooltipListener = mSendButton.getOnLongClickListener();
        mSendButton.setOnLongClickListener(view -> {
            if (!Settings.LONG_CLICK_TO_SHOW_SEND_COMMENT_ACTIVITY.getValue()) {
                return sendTooltipListener.onLongClick(view);
            }
            onShowSendCommentActivity();
            return true;
        });
        updateSendCommentStatus();

        if (savedInstanceState == null) {
            if (mShowSendComment) {
                TransitionUtils.postAfterTransition(this, this::onShowCommentIme);
            }
        }

        TransitionUtils.setEnterReturnExplode(this);
        TransitionUtils.setupTransitionOnActivityCreated(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        EventBusUtils.unregister(this);

        mResource.detach();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.broadcast, menu);
        mCopyTextMenuItem = menu.findItem(R.id.action_copy_text);
        mDeleteMenuItem = menu.findItem(R.id.action_delete);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        updateOptionsMenu();
    }

    private void updateOptionsMenu() {
        if (mCopyTextMenuItem == null && mDeleteMenuItem == null) {
            return;
        }
        Broadcast broadcast = mResource.getEffectiveBroadcast();
        boolean hasBroadcast = broadcast != null;
        mCopyTextMenuItem.setEnabled(hasBroadcast);
        boolean canDelete = hasBroadcast && broadcast.isAuthorOneself();
        mDeleteMenuItem.setVisible(canDelete);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onFinish();
                return true;
            case R.id.action_copy_text:
                copyText();
                return true;
            case R.id.action_delete:
                onDeleteBroadcast();
                return true;
            case R.id.action_share:
                share();
                return true;
            case R.id.action_view_on_web:
                viewOnWeb();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private String getTitle() {
        return !TextUtils.isEmpty(mTitle) ? mTitle : getString(R.string.broadcast_title);
    }

    @Override
    public void onLoadBroadcastStarted(int requestCode) {
        updateRefreshing();
    }

    @Override
    public void onLoadBroadcastFinished(int requestCode) {
        updateRefreshing();
    }

    @Override
    public void onLoadBroadcastError(int requestCode, ApiError error) {
        LogUtils.e(error.toString());
        Activity activity = getActivity();
        ToastUtils.show(ApiError.getErrorString(error, activity), activity);
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

    private void setBroadcast(Broadcast broadcast) {
        mBroadcastAdapter.setBroadcast(broadcast);
        updateOptionsMenu();
        updateSendCommentStatus();
    }

    @Override
    public void onLoadCommentListStarted(int requestCode) {
        updateRefreshing();
    }

    @Override
    public void onLoadCommentListFinished(int requestCode) {
        updateRefreshing();
    }

    @Override
    public void onLoadCommentListError(int requestCode, ApiError error) {
        LogUtils.e(error.toString());
        Activity activity = getActivity();
        ToastUtils.show(ApiError.getErrorString(error, activity), activity);
    }

    @Override
    public void onCommentListChanged(int requestCode, List<Comment> newCommentList) {
        mCommentAdapter.replace(newCommentList);
        BroadcastCommentCountFixer.onCommentListChanged(mResource.getEffectiveBroadcast(),
                mResource.getCommentList(), this);
    }

    @Override
    public void onCommentListAppended(int requestCode, List<Comment> appendedCommentList) {
        mCommentAdapter.addAll(appendedCommentList);
        BroadcastCommentCountFixer.onCommentListChanged(mResource.getEffectiveBroadcast(),
                mResource.getCommentList(), this);
    }

    @Override
    public void onCommentRemoved(int requestCode, int position) {
        mCommentAdapter.remove(position);
        BroadcastCommentCountFixer.onCommentRemoved(mResource.getEffectiveBroadcast(), this);
    }

    private void updateRefreshing() {
        boolean loadingBroadcast = mResource.isLoadingBroadcast();
        //noinspection deprecation
        boolean hasBroadcast = mResource.hasBroadcast();
        boolean loadingCommentList = mResource.isLoadingCommentList();
        mSwipeRefreshLayout.setRefreshing(loadingBroadcast
                && (mSwipeRefreshLayout.isRefreshing() || hasBroadcast));
        ViewUtils.setVisibleOrGone(mProgress, loadingBroadcast && !hasBroadcast);
        mAdapter.setLoading(hasBroadcast && loadingCommentList);
    }

    @Override
    public void onLike(Broadcast broadcast, boolean like) {
        LikeBroadcastManager.getInstance().write(broadcast, like, getActivity());
    }

    @Override
    public void onRebroadcast(Broadcast broadcast, boolean rebroadcast, boolean quick) {
        if (rebroadcast) {
            if (quick) {
                RebroadcastBroadcastManager.getInstance().write(broadcast.getEffectiveBroadcast(),
                        null, getActivity());
            } else {
                startActivity(RebroadcastBroadcastActivity.makeIntent(broadcast, getActivity()));
            }
        } else {
            if (quick) {
                DeleteBroadcastManager.getInstance().write(broadcast, getActivity());
            } else {
                ConfirmUnrebroadcastBroadcastDialogFragment.show(broadcast, this);
            }
        }
    }

    @Override
    public void unrebroadcastBroadcast(Broadcast broadcast) {
        DeleteBroadcastManager.getInstance().write(broadcast, getActivity());
    }

    @Override
    public void onComment(Broadcast broadcast) {
        onShowCommentIme();
    }

    @Override
    public void onViewActivity(Broadcast broadcast) {
        BroadcastActivityDialogFragment.show(broadcast, this);
    }

    private void onShowCommentAction(Comment comment) {
        boolean canReplyTo = canSendComment();
        boolean canDelete = (mBroadcastAdapter.hasBroadcast()
                && mBroadcastAdapter.getBroadcast().isAuthorOneself())
                || comment.isAuthorOneself();
        CommentActionDialogFragment.show(comment, canReplyTo, canDelete, this);
    }

    @Override
    public void onReplyToComment(Comment comment) {
        mCommentEdit.getText().replace(mCommentEdit.getSelectionStart(),
                mCommentEdit.getSelectionEnd(), DoubanUtils.makeMentionString(comment.author));
        onShowCommentIme();
    }

    @Override
    public void onCopyCommentText(Comment comment) {
        ClipboardUtils.copy(comment, getActivity());
    }

    @Override
    public void onDeleteComment(Comment comment) {
        ConfirmDeleteCommentDialogFragment.show(comment.id, this);
    }

    @Override
    public void deleteComment(long commentId) {
        DeleteBroadcastCommentManager.getInstance().write(mResource.getEffectiveBroadcastId(),
                commentId, getActivity());
    }

    private void onShowCommentIme() {
        if (canSendComment()) {
            ImeUtils.showIme(mCommentEdit);
        } else {
            ToastUtils.show(R.string.broadcast_send_comment_disabled, getActivity());
        }
    }

    private void onSendComment() {

        String comment = mCommentEdit.getText().toString();
        if (TextUtils.isEmpty(comment)) {
            ToastUtils.show(R.string.broadcast_send_comment_error_empty, getActivity());
            return;
        }

        sendComment(comment);
    }

    private void sendComment(String comment) {

        SendBroadcastCommentManager.getInstance().write(mResource.getEffectiveBroadcastId(),
                comment, getActivity());

        updateSendCommentStatus();
    }

    private void onShowSendCommentActivity() {
        startActivity(SendCommentActivity.makeIntent(mResource.getEffectiveBroadcastId(),
                mCommentEdit.getText(), getActivity()));
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onBroadcastCommentSent(BroadcastCommentSentEvent event) {

        if (event.isFromMyself(this)) {
            return;
        }

        if (mResource.isEffectiveBroadcastId(event.broadcastId)) {
            mBroadcastCommentList.scrollToPosition(mAdapter.getItemCount() - 1);
            mCommentEdit.setText(null);
            updateSendCommentStatus();
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onBroadcastCommentSendError(BroadcastCommentSendErrorEvent event) {

        if (event.isFromMyself(this)) {
            return;
        }

        if (mResource.isEffectiveBroadcastId(event.broadcastId)) {
            updateSendCommentStatus();
        }
    }

    private boolean canSendComment() {
        Broadcast broadcast = mResource.getEffectiveBroadcast();
        return broadcast != null && broadcast.canComment();
    }

    private void updateSendCommentStatus() {
        boolean canSendComment = canSendComment();
        SendBroadcastCommentManager manager = SendBroadcastCommentManager.getInstance();
        boolean hasBroadcast = mResource.hasEffectiveBroadcast();
        boolean sendingComment = hasBroadcast && manager.isWriting(
                mResource.getEffectiveBroadcastId());
        boolean enabled = canSendComment && !sendingComment;
        mCommentEdit.setEnabled(enabled);
        mSendButton.setEnabled(enabled);
        mCommentEdit.setHint(!hasBroadcast || canSendComment ? R.string.broadcast_send_comment_hint
                : R.string.broadcast_send_comment_hint_disabled);
        if (sendingComment) {
            mCommentEdit.setText(manager.getComment(mResource.getEffectiveBroadcastId()));
        }
    }

    private void copyText() {

        Broadcast broadcast = mBroadcastAdapter.getBroadcast();
        Activity activity = getActivity();
        if (broadcast == null) {
            ToastUtils.show(R.string.broadcast_copy_text_not_loaded, activity);
            return;
        }

        ClipboardUtils.copy(broadcast, activity);
    }

    private void onDeleteBroadcast() {
        ConfirmDeleteBroadcastDialogFragment.show(this);
    }

    @Override
    public void deleteBroadcast() {
        DeleteBroadcastManager.getInstance().write(mResource.getEffectiveBroadcast(),
                getActivity());
    }

    private void share() {
        ShareUtils.shareText(makeUrl(), getActivity());
    }

    private void viewOnWeb() {
        startActivity(WebViewActivity.makeIntent(makeUrl(), true, getActivity()));
    }

    private String makeUrl() {
        if (mResource.hasEffectiveBroadcast()) {
            return mResource.getEffectiveBroadcast().getUrl();
        } else {
            //noinspection deprecation
            return DoubanUtils.makeBroadcastUrl(mResource.getBroadcastId());
        }
    }

    public void onFinish() {
        if (mCommentEdit.getText().length() > 0) {
            ConfirmDiscardContentDialogFragment.show(this);
        } else {
            finishAfterTransition();
        }
    }

    @Override
    public void discardContent() {
        finishAfterTransition();
    }

    private void finish() {
        FragmentFinishable.finish(getActivity());
    }

    private void finishAfterTransition() {
        FragmentFinishable.finishAfterTransition(getActivity());
    }
}
