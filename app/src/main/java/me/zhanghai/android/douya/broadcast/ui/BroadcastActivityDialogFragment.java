/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.broadcast.ui;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.eventbus.BroadcastUpdatedEvent;
import me.zhanghai.android.douya.eventbus.EventBusUtils;
import me.zhanghai.android.douya.network.api.info.frodo.Broadcast;
import me.zhanghai.android.douya.ui.TabFragmentPagerAdapter;
import me.zhanghai.android.douya.util.FragmentUtils;

public class BroadcastActivityDialogFragment extends AppCompatDialogFragment {

    private static final String KEY_PREFIX = BroadcastActivityDialogFragment.class.getName() + '.';

    private static final String EXTRA_BROADCAST = KEY_PREFIX + "broadcast";

    @BindView(R.id.tab)
    TabLayout mTabLayout;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    @BindView(android.R.id.button1)
    Button mPositiveButton;
    @BindView(android.R.id.button2)
    Button mNegativeButton;
    @BindView(android.R.id.button3)
    Button mNeutralButton;

    private TabFragmentPagerAdapter mTabAdapter;

    private Broadcast mBroadcast;

    /**
     * @deprecated Use {@link #newInstance(Broadcast)} instead.
     */
    public BroadcastActivityDialogFragment() {}

    public static BroadcastActivityDialogFragment newInstance(Broadcast broadcast) {
        //noinspection deprecation
        BroadcastActivityDialogFragment fragment = new BroadcastActivityDialogFragment();
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

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AppCompatDialog dialog = (AppCompatDialog) super.onCreateDialog(savedInstanceState);
        // We are using a custom title, as in AlertDialog.
        dialog.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.broadcast_activity_dialog_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mTabAdapter = new TabFragmentPagerAdapter(this);
        mTabAdapter.addTab(() -> BroadcastLikerListFragment.newInstance(mBroadcast), null);
        mTabAdapter.addTab(() -> BroadcastRebroadcastListFragment.newInstance(
                mBroadcast), null);
        updateTabTitle();
        mViewPager.setOffscreenPageLimit(mTabAdapter.getCount() - 1);
        mViewPager.setAdapter(mTabAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mPositiveButton.setText(R.string.ok);
        mPositiveButton.setVisibility(View.VISIBLE);
        mPositiveButton.setOnClickListener(view -> dismiss());
        mNegativeButton.setVisibility(View.GONE);
        mNeutralButton.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        EventBusUtils.unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onBroadcastUpdated(BroadcastUpdatedEvent event) {

        if (event.isFromMyself(this)) {
            return;
        }

        Broadcast updatedBroadcast = event.update(mBroadcast, this);
        if (updatedBroadcast != null) {
            mBroadcast = updatedBroadcast;
            updateTabTitle();
        }
    }

    private void updateTabTitle() {
        mTabAdapter.setPageTitle(mTabLayout, 0, getTabTitle(mBroadcast.likeCount,
                R.string.broadcast_likers_title_format, R.string.broadcast_likers_title_empty));
        mTabAdapter.setPageTitle(mTabLayout, 1, getTabTitle(mBroadcast.rebroadcastCount,
                R.string.broadcast_rebroadcasts_title_format,
                R.string.broadcast_rebroadcasts_title_empty));
    }

    private CharSequence getTabTitle(int count, int formatResId, int emptyResId) {
        return count > 0 ? getString(formatResId, count) : getString(emptyResId);
    }

    public static void show(Broadcast broadcast, Fragment fragment) {
        BroadcastActivityDialogFragment.newInstance(broadcast)
                .show(fragment.getChildFragmentManager(), null);
    }
}
