/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.broadcast.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.customtabshelper.CustomTabsHelperFragment;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.broadcast.content.BroadcastResource;
import me.zhanghai.android.douya.broadcast.content.RebroadcastBroadcastManager;
import me.zhanghai.android.douya.eventbus.BroadcastRebroadcastErrorEvent;
import me.zhanghai.android.douya.eventbus.BroadcastRebroadcastedEvent;
import me.zhanghai.android.douya.eventbus.EventBusUtils;
import me.zhanghai.android.douya.network.api.ApiError;
import me.zhanghai.android.douya.network.api.info.frodo.Broadcast;
import me.zhanghai.android.douya.util.FragmentUtils;
import me.zhanghai.android.douya.util.LogUtils;
import me.zhanghai.android.douya.util.ToastUtils;
import me.zhanghai.android.douya.util.ViewUtils;

public class RebroadcastBroadcastFragment extends Fragment implements BroadcastResource.Listener {

    private static final String KEY_PREFIX = RebroadcastBroadcastFragment.class.getName() + '.';

    private static final String EXTRA_BROADCAST_ID = KEY_PREFIX + "broadcast_id";
    private static final String EXTRA_BROADCAST = KEY_PREFIX + "broadcast";
    private static final String EXTRA_TEXT = KEY_PREFIX + "text";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.text_and_broadcast)
    ViewGroup mTextAndContentLayout;
    @BindView(R.id.broadcast)
    BroadcastLayout mBroadcastLayout;
    @BindView(R.id.text)
    EditText mTextEdit;
    @BindView(R.id.progress)
    ProgressBar mProgress;

    private MenuItem mRebroadcastMenuItem;

    private long mBroadcastId;
    private Broadcast mBroadcast;
    private CharSequence mText;

    private BroadcastResource mBroadcastResource;

    public static RebroadcastBroadcastFragment newInstance(long broadcastId, Broadcast broadcast,
                                                           CharSequence text) {
        //noinspection deprecation
        RebroadcastBroadcastFragment fragment = new RebroadcastBroadcastFragment();
        Bundle arguments = FragmentUtils.ensureArguments(fragment);
        arguments.putLong(EXTRA_BROADCAST_ID, broadcastId);
        arguments.putParcelable(EXTRA_BROADCAST, broadcast);
        arguments.putCharSequence(EXTRA_TEXT, text);
        return fragment;
    }

    /**
     * @deprecated Use {@link #newInstance(long, Broadcast, CharSequence)} instead.
     */
    public RebroadcastBroadcastFragment() {}

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
        mText = arguments.getCharSequence(EXTRA_TEXT);

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.broadcast_rebroadcast_broadcast_fragment, container,
                false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        CustomTabsHelperFragment.attachTo(this);
        mBroadcastResource = BroadcastResource.attachTo(mBroadcastId, mBroadcast, this);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);

        if (mBroadcastResource.has()) {
            mBroadcastLayout.bind(mBroadcastResource.get());
        }
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

        mBroadcastResource.detach();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.broadcast_rebroadcast_broadcast, menu);
        mRebroadcastMenuItem = menu.findItem(R.id.action_rebroadcast);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        updateOptionsMenu();
    }

    private void updateOptionsMenu() {
        if (mRebroadcastMenuItem == null) {
            return;
        }
        mRebroadcastMenuItem.setEnabled(mBroadcastResource.has());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // TODO: Confirmation
                getActivity().finish();
                return true;
            case R.id.action_rebroadcast:
                onRebroadcastBroadcast();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onLoadBroadcastStarted(int requestCode) {
        updateRefreshing();
    }

    @Override
    public void onLoadBroadcastFinished(int requestCode) {
        updateRefreshing();
        updateOptionsMenu();
    }

    @Override
    public void onLoadBroadcastError(int requestCode, ApiError error) {
        LogUtils.e(error.toString());
        Activity activity = getActivity();
        ToastUtils.show(ApiError.getErrorString(error, activity), activity);
        updateOptionsMenu();
    }

    @Override
    public void onBroadcastChanged(int requestCode, Broadcast newBroadcast) {
        mBroadcastLayout.bind(newBroadcast);
    }

    @Override
    public void onBroadcastRemoved(int requestCode) {}

    @Override
    public void onBroadcastWriteStarted(int requestCode) {}

    @Override
    public void onBroadcastWriteFinished(int requestCode) {}

    private void updateRefreshing() {
        boolean hasBroadcast = mBroadcastResource.has();
        ViewUtils.fadeToVisibility(mProgress, !hasBroadcast);
        ViewUtils.fadeToVisibility(mTextAndContentLayout, hasBroadcast);
    }

    private void onRebroadcastBroadcast() {
        String text = mTextEdit.getText().toString();
        rebroadcastBroadcast(text);
    }

    private void rebroadcastBroadcast(String text) {
        RebroadcastBroadcastManager.getInstance().write(mBroadcastResource.get(), text,
                getActivity());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBroadcastRebroadcasted(BroadcastRebroadcastedEvent event) {

        if (event.isFromMyself(this)) {
            return;
        }

        if (mBroadcastResource.getBroadcastId() == event.broadcastId) {
            getActivity().finish();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBroadcastRebroadcastError(BroadcastRebroadcastErrorEvent event) {

        if (event.isFromMyself(this)) {
            return;
        }

        if (mBroadcastResource.getBroadcastId() == event.broadcastId) {
            // TOOO
            //updateSendCommentStatus();
        }
    }
}
