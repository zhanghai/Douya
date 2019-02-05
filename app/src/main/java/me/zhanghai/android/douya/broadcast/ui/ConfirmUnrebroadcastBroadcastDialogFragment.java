/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.broadcast.ui;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.eventbus.BroadcastUpdatedEvent;
import me.zhanghai.android.douya.eventbus.EventBusUtils;
import me.zhanghai.android.douya.network.api.info.frodo.Broadcast;
import me.zhanghai.android.douya.util.FragmentUtils;

public class ConfirmUnrebroadcastBroadcastDialogFragment extends AppCompatDialogFragment {

    private static final String KEY_PREFIX =
            ConfirmUnrebroadcastBroadcastDialogFragment.class.getName() + '.';

    private static final String EXTRA_BROADCAST = KEY_PREFIX + "broadcast";

    private Broadcast mBroadcast;

    /**
     * @deprecated Use {@link #newInstance(Broadcast)} instead.
     */
    public ConfirmUnrebroadcastBroadcastDialogFragment() {}

    public static ConfirmUnrebroadcastBroadcastDialogFragment newInstance(Broadcast broadcast) {
        //noinspection deprecation
        ConfirmUnrebroadcastBroadcastDialogFragment fragment =
                new ConfirmUnrebroadcastBroadcastDialogFragment();
        FragmentUtils.getArgumentsBuilder(fragment)
                .putParcelable(EXTRA_BROADCAST, broadcast);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBroadcast = getArguments().getParcelable(EXTRA_BROADCAST);

        EventBusUtils.register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        EventBusUtils.unregister(this);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity(), getTheme())
                .setMessage(R.string.broadcast_unrebroadcast_confirm)
                .setPositiveButton(R.string.ok, (dialog, which) ->
                        getListener().unrebroadcastBroadcast(mBroadcast))
                .setNegativeButton(R.string.cancel, null)
                .create();
    }

    private Listener getListener() {
        return (Listener) getParentFragment();
    }

    public static void show(Broadcast broadcast, Fragment fragment) {
        ConfirmUnrebroadcastBroadcastDialogFragment.newInstance(broadcast)
                .show(fragment.getChildFragmentManager(), null);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onBroadcastUpdated(BroadcastUpdatedEvent event) {

        if (event.isFromMyself(this)) {
            return;
        }

        Broadcast updatedBroadcast = event.update(mBroadcast, this);
        if (updatedBroadcast != null) {
            mBroadcast = updatedBroadcast;
        }
    }

    public interface Listener {
        void unrebroadcastBroadcast(Broadcast broadcast);
    }
}
