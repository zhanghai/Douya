/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.broadcast.ui;

import android.content.Intent;
import android.net.Uri;
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

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.customtabshelper.CustomTabsHelperFragment;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.broadcast.content.SendBroadcastManager;
import me.zhanghai.android.douya.eventbus.BroadcastSendErrorEvent;
import me.zhanghai.android.douya.eventbus.BroadcastSentEvent;
import me.zhanghai.android.douya.eventbus.EventBusUtils;
import me.zhanghai.android.douya.ui.ConfirmDiscardContentDialogFragment;
import me.zhanghai.android.douya.util.FragmentUtils;
import me.zhanghai.android.douya.util.ViewUtils;

public class SendBroadcastFragment extends Fragment
        implements ConfirmDiscardContentDialogFragment.Listener {

    private static final String KEY_PREFIX = SendBroadcastFragment.class.getName() + '.';

    private static final String STATE_WRITER_ID = KEY_PREFIX + "writer_id";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.broadcast)
    BroadcastLayout mBroadcastLayout;
    @BindView(R.id.text)
    EditText mTextEdit;

    private MenuItem mSendMenuItem;

    private CharSequence mText;
    private Uri mStream;

    private long mWriterId;

    private boolean mSent;

    public static SendBroadcastFragment newInstance(CharSequence text, Uri stream) {
        //noinspection deprecation
        SendBroadcastFragment fragment = new SendBroadcastFragment();
        Bundle arguments = FragmentUtils.ensureArguments(fragment);
        arguments.putCharSequence(Intent.EXTRA_TEXT, text);
        arguments.putParcelable(Intent.EXTRA_STREAM, stream);
        return fragment;
    }

    /**
     * @deprecated Use {@link #newInstance(CharSequence, Uri)} instead.
     */
    public SendBroadcastFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        mText = arguments.getCharSequence(Intent.EXTRA_TEXT);
        mStream = arguments.getParcelable(Intent.EXTRA_STREAM);

        if (savedInstanceState != null) {
            mWriterId = savedInstanceState.getLong(STATE_WRITER_ID);
        }

        setHasOptionsMenu(true);

        EventBusUtils.register(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.broadcast_send_broadcast_fragment, container, false);
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

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);

        if (savedInstanceState == null) {
            mTextEdit.setText(mText);
        }
        // TODO
        ViewUtils.setVisibleOrGone(mBroadcastLayout, false);
        updateSendStatus();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putLong(STATE_WRITER_ID, mWriterId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        EventBusUtils.unregister(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.broadcast_send_broadcast, menu);
        mSendMenuItem = menu.findItem(R.id.action_send);
        updateSendStatus();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onFinish();
                return true;
            case R.id.action_send:
                onSend();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onSend() {
        String text = mTextEdit.getText().toString();
        send(text);
    }

    private void send(String text) {
        mWriterId = SendBroadcastManager.getInstance().write(text, null, null, null, getActivity());
        updateSendStatus();
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onBroadcastSent(BroadcastSentEvent event) {

        if (event.isFromMyself(this)) {
            return;
        }

        if (event.writerId == mWriterId) {
            mSent = true;
            mWriterId = 0;
            getActivity().finish();
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onBroadcastSendError(BroadcastSendErrorEvent event) {

        if (event.isFromMyself(this)) {
            return;
        }

        if (event.writerId == mWriterId) {
            mWriterId = 0;
            updateSendStatus();
        }
    }

    private void updateSendStatus() {
        if (mSent) {
            return;
        }
        SendBroadcastManager manager = SendBroadcastManager.getInstance();
        boolean sending = manager.isWriting(mWriterId);
        getActivity().setTitle(sending ? R.string.broadcast_send_title_sending
                : R.string.broadcast_send_title);
        boolean enabled = !sending;
        mTextEdit.setEnabled(enabled);
        if (mSendMenuItem != null) {
            mSendMenuItem.setEnabled(enabled);
        }
        if (sending) {
            mTextEdit.setText(manager.getText(mWriterId));
        }
    }

    public void onFinish() {
        if (mTextEdit.getText().length() > 0) {
            ConfirmDiscardContentDialogFragment.show(this);
        } else {
            getActivity().finish();
        }
    }

    @Override
    public void discardContent() {
        getActivity().finish();
    }
}
