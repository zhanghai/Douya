/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.broadcast.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
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
import me.zhanghai.android.douya.network.api.info.apiv2.Broadcast;
import me.zhanghai.android.douya.network.api.info.apiv2.Comment;
import me.zhanghai.android.douya.ui.ClickableSimpleAdapter;
import me.zhanghai.android.douya.ui.LoadMoreAdapter;
import me.zhanghai.android.douya.ui.NoChangeAnimationItemAnimator;
import me.zhanghai.android.douya.ui.OnVerticalScrollListener;
import me.zhanghai.android.douya.util.CheatSheetUtils;
import me.zhanghai.android.douya.util.ClipboardUtils;
import me.zhanghai.android.douya.util.DoubanUtils;
import me.zhanghai.android.douya.util.FragmentUtils;
import me.zhanghai.android.douya.util.ImeUtils;
import me.zhanghai.android.douya.util.LogUtils;
import me.zhanghai.android.douya.util.ToastUtils;
import me.zhanghai.android.douya.util.TransitionUtils;
import me.zhanghai.android.douya.util.ViewUtils;

public class BroadcastFragment extends Fragment implements BroadcastAndCommentListResource.Listener,
        SingleBroadcastAdapter.Listener, CommentActionDialogFragment.Listener,
        ConfirmDeleteCommentDialogFragment.Listener, ConfirmDeleteBroadcastDialogFragment.Listener {

    private static final String KEY_PREFIX = BroadcastFragment.class.getName() + '.';

    private static final String EXTRA_BROADCAST_ID = KEY_PREFIX + "broadcast_id";
    private static final String EXTRA_BROADCAST = KEY_PREFIX + "broadcast";
    private static final String EXTRA_SHOW_SEND_COMMENT = KEY_PREFIX + "show_send_comment";
    private static final String EXTRA_TITLE = KEY_PREFIX + "title";

    @BindView(R.id.container)
    FrameLayout mContainerLayout;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
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
    ImageButton mSendButton;

    private MenuItem mCopyTextMenuItem;
    private MenuItem mDeleteMenuItem;

    private long mBroadcastId;
    private Broadcast mBroadcast;
    private boolean mShowSendComment;
    private String mTitle;

    private BroadcastAndCommentListResource mBroadcastAndCommentListResource;

    private SingleBroadcastAdapter mBroadcastAdapter;
    private CommentAdapter mCommentAdapter;
    private LoadMoreAdapter mAdapter;

    public static BroadcastFragment newInstance(long broadcastId, Broadcast broadcast,
                                                boolean showSendComment, String title) {
        //noinspection deprecation
        BroadcastFragment fragment = new BroadcastFragment();
        Bundle arguments = FragmentUtils.ensureArguments(fragment);
        arguments.putLong(EXTRA_BROADCAST_ID, broadcastId);
        arguments.putParcelable(EXTRA_BROADCAST, broadcast);
        arguments.putBoolean(EXTRA_SHOW_SEND_COMMENT, showSendComment);
        arguments.putString(EXTRA_TITLE, title);
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
        mBroadcastAndCommentListResource = BroadcastAndCommentListResource.attachTo(mBroadcastId,
                mBroadcast, this);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setTitle(getTitle());
        activity.setSupportActionBar(mToolbar);

        mContainerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.finishAfterTransition(getActivity());
            }
        });
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

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mBroadcastAndCommentListResource.loadBroadcast();
                mBroadcastAndCommentListResource.loadCommentList(false);
            }
        });

        mBroadcastCommentList.setHasFixedSize(true);
        mBroadcastCommentList.setItemAnimator(new NoChangeAnimationItemAnimator());
        mBroadcastCommentList.setLayoutManager(new LinearLayoutManager(activity));
        mBroadcastAdapter = new SingleBroadcastAdapter(null, this);
        setBroadcast(mBroadcastAndCommentListResource.getBroadcast());
        mCommentAdapter = new CommentAdapter(mBroadcastAndCommentListResource.getCommentList(),
                new ClickableSimpleAdapter.OnItemClickListener<Comment,
                        CommentAdapter.ViewHolder>() {
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
                mBroadcastAndCommentListResource.loadCommentList(true);
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

        if (savedInstanceState == null) {
            if (mShowSendComment) {
                TransitionUtils.postAfterTransition(this, new Runnable() {
                    @Override
                    public void run() {
                        onShowSendComment();
                    }
                });
            }
        }

        TransitionUtils.setEnterReturnExplode(this);
        TransitionUtils.setupTransitionOnActivityCreated(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        EventBusUtils.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        EventBusUtils.unregister(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mBroadcastAndCommentListResource.detach();
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
        Broadcast broadcast = mBroadcastAndCommentListResource.getBroadcast();
        boolean hasBroadcast = broadcast != null;
        mCopyTextMenuItem.setVisible(hasBroadcast);
        boolean canDelete = hasBroadcast && broadcast.isAuthorOneself(getActivity());
        mDeleteMenuItem.setVisible(canDelete);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ActivityCompat.finishAfterTransition(getActivity());
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
        getActivity().finish();
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
        BroadcastCommentCountFixer.onCommentListChanged(
                mBroadcastAndCommentListResource.getBroadcast(),
                mBroadcastAndCommentListResource.getCommentList(), this);
    }

    @Override
    public void onCommentListAppended(int requestCode, List<Comment> appendedCommentList) {
        mCommentAdapter.addAll(appendedCommentList);
        BroadcastCommentCountFixer.onCommentListChanged(
                mBroadcastAndCommentListResource.getBroadcast(),
                mBroadcastAndCommentListResource.getCommentList(), this);
    }

    @Override
    public void onCommentRemoved(int requestCode, int position) {
        mCommentAdapter.remove(position);
        BroadcastCommentCountFixer.onCommentRemoved(
                mBroadcastAndCommentListResource.getBroadcast(), this);
    }

    private void updateRefreshing() {
        boolean loadingBroadcast = mBroadcastAndCommentListResource.isLoadingBroadcast();
        boolean hasBroadcast = mBroadcastAndCommentListResource.hasBroadcast();
        boolean loadingCommentList = mBroadcastAndCommentListResource.isLoadingCommentList();
        mSwipeRefreshLayout.setRefreshing(loadingBroadcast
                && (mSwipeRefreshLayout.isRefreshing() || hasBroadcast));
        ViewUtils.setVisibleOrGone(mProgress, loadingBroadcast && !hasBroadcast);
        mAdapter.setProgressVisible(hasBroadcast && loadingCommentList);
    }

    @Override
    public void onLike(Broadcast broadcast, boolean like) {
        LikeBroadcastManager.getInstance().write(broadcast, like, getActivity());
    }

    @Override
    public void onRebroadcast(Broadcast broadcast, boolean rebroadcast) {
        RebroadcastBroadcastManager.getInstance().write(broadcast, rebroadcast, getActivity());
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
        Activity activity = getActivity();
        boolean canDelete = (mBroadcastAdapter.hasBroadcast()
                && mBroadcastAdapter.getBroadcast().isAuthorOneself(activity))
                || comment.isAuthorOneself(activity);
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
        Activity activity = getActivity();
        ClipboardUtils.copyText(comment.getClipboardLabel(), comment.getClipboardText(activity),
                activity);
    }

    @Override
    public void onDeleteComment(Comment comment) {
        ConfirmDeleteCommentDialogFragment.show(comment, this);
    }

    @Override
    public void deleteComment(Comment comment) {
        DeleteBroadcastCommentManager.getInstance().write(
                mBroadcastAndCommentListResource.getBroadcastId(), comment.id, getActivity());
    }

    private void onShowSendComment() {
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

        SendBroadcastCommentManager.getInstance().write(
                mBroadcastAndCommentListResource.getBroadcastId(), comment, getActivity());

        updateSendCommentStatus();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBroadcastCommentSent(BroadcastCommentSentEvent event) {

        if (event.isFromMyself(this)) {
            return;
        }

        if (event.broadcastId == mBroadcastAndCommentListResource.getBroadcastId()) {
            mBroadcastCommentList.scrollToPosition(mAdapter.getItemCount() - 1);
            mCommentEdit.setText(null);
            updateSendCommentStatus();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBroadcastCommentSendError(BroadcastCommentSendErrorEvent event) {

        if (event.isFromMyself(this)) {
            return;
        }

        if (event.broadcastId == mBroadcastAndCommentListResource.getBroadcastId()) {
            updateSendCommentStatus();
        }
    }

    private boolean canSendComment() {
        Broadcast broadcast = mBroadcastAndCommentListResource.getBroadcast();
        return broadcast != null && broadcast.canComment();
    }

    private void updateSendCommentStatus() {
        boolean canSendComment = canSendComment();
        SendBroadcastCommentManager manager = SendBroadcastCommentManager.getInstance();
        long broadcastId = mBroadcastAndCommentListResource.getBroadcastId();
        boolean sendingComment = manager.isWriting(broadcastId);
        boolean enabled = canSendComment && !sendingComment;
        mCommentEdit.setEnabled(enabled);
        mSendButton.setEnabled(enabled);
        boolean hasBroadcast = mBroadcastAndCommentListResource.hasBroadcast();
        mCommentEdit.setHint(!hasBroadcast || canSendComment ? R.string.broadcast_send_comment_hint
                : R.string.broadcast_send_comment_hint_disabled);
        if (sendingComment) {
            mCommentEdit.setText(manager.getComment(broadcastId));
        }
    }

    private void copyText() {

        Broadcast broadcast = mBroadcastAdapter.getBroadcast();
        Activity activity = getActivity();
        if (broadcast == null) {
            ToastUtils.show(R.string.broadcast_copy_text_not_loaded, activity);
            return;
        }

        ClipboardUtils.copyText(broadcast.getClipboradLabel(), broadcast.getClipboardText(activity),
                activity);
    }

    private void onDeleteBroadcast() {
        ConfirmDeleteBroadcastDialogFragment.show(this);
    }

    @Override
    public void deleteBroadcast() {
        DeleteBroadcastManager.getInstance().write(
                mBroadcastAndCommentListResource.getBroadcastId(), getActivity());
    }
}
