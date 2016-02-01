/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.broadcast.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.eventbus.BroadcastUpdatedEvent;
import me.zhanghai.android.douya.network.api.info.Broadcast;
import me.zhanghai.android.douya.ui.TabFragmentPagerAdapter;

public class BroadcastActivityDialogFragment extends AppCompatDialogFragment {

    private static final String KEY_PREFIX = BroadcastActivityDialogFragment.class.getName() + '.';

    public static final String EXTRA_BROADCAST = KEY_PREFIX + "broadcast";

    @Bind(R.id.tab)
    TabLayout mTabLayout;
    @Bind(R.id.viewPager)
    ViewPager mViewPager;
    @Bind(android.R.id.button1)
    Button mPositiveButton;
    @Bind(android.R.id.button2)
    Button mNegativeButton;
    @Bind(android.R.id.button3)
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
        Bundle arguments = new Bundle();
        arguments.putParcelable(EXTRA_BROADCAST, broadcast);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBroadcast = getArguments().getParcelable(EXTRA_BROADCAST);
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

        mTabAdapter = new TabFragmentPagerAdapter(getChildFragmentManager());
        mTabAdapter.addTab(BroadcastLikerListFragment.newInstance(mBroadcast), null);
        mTabAdapter.addTab(BroadcastRebroadcastersListFragment.newInstance(mBroadcast), null);
        updateTabTitle();
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setAdapter(mTabAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mPositiveButton.setText(R.string.ok);
        mPositiveButton.setVisibility(View.VISIBLE);
        mPositiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        mNegativeButton.setVisibility(View.GONE);
        mNeutralButton.setVisibility(View.GONE);
    }

    @Override
    public void onStart(){
        super.onStart();

        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        EventBus.getDefault().unregister(this);
    }

    @Keep
    public void onEventMainThread(BroadcastUpdatedEvent event) {
        Broadcast broadcast = event.broadcast;
        if (broadcast.id == mBroadcast.id) {
            mBroadcast = broadcast;
            updateTabTitle();
        }
    }

    private void updateTabTitle() {
        mTabAdapter.setPageTitle(mTabLayout, 0, getTabTitle(mBroadcast.likeCount,
                R.string.broadcast_likers_title_format, R.string.broadcast_likers_title_empty));
        mTabAdapter.setPageTitle(mTabLayout, 1, getTabTitle(mBroadcast.rebroadcastCount,
                R.string.broadcast_rebroadcasters_title_format,
                R.string.broadcast_rebroadcasters_title_empty));
    }

    private CharSequence getTabTitle(int count, int formatResId, int emptyResId) {
        return count > 0 ? getString(formatResId, count) : getString(emptyResId);
    }

    public static void show(Broadcast broadcast, FragmentActivity activity) {
        BroadcastActivityDialogFragment.newInstance(broadcast)
                .show(activity.getSupportFragmentManager(), null);
    }
}
