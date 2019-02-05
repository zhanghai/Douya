/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.home;

import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.broadcast.ui.HomeBroadcastListFragment;
import me.zhanghai.android.douya.ui.AppBarHost;
import me.zhanghai.android.douya.ui.AppBarWrapperLayout;
import me.zhanghai.android.douya.ui.DoubleClickToolbar;
import me.zhanghai.android.douya.ui.NotYetImplementedFragment;
import me.zhanghai.android.douya.ui.TabFragmentPagerAdapter;

public class HomeFragment extends Fragment implements AppBarHost {

    @BindView(R.id.appBarWrapper)
    AppBarWrapperLayout mAppBarWrapperLayout;
    @BindView(R.id.toolbar)
    DoubleClickToolbar mToolbar;
    @BindView(R.id.tab)
    TabLayout mTabLayout;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;

    private TabFragmentPagerAdapter mTabAdapter;

    public static HomeFragment newInstance() {
        //noinspection deprecation
        return new HomeFragment();
    }

    /**
     * @deprecated Use {@link #newInstance()} instead.
     */
    public HomeFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);

        mTabAdapter = new TabFragmentPagerAdapter(this);
        mTabAdapter.addTab(HomeBroadcastListFragment::newInstance, getString(R.string.home_broadcast));
        mTabAdapter.addTab(NotYetImplementedFragment::newInstance, getString(R.string.home_discover));
        mTabAdapter.addTab(NotYetImplementedFragment::newInstance, getString(R.string.home_topic));
        mTabAdapter.addTab(NotYetImplementedFragment::newInstance, getString(R.string.home_online));
        mViewPager.setOffscreenPageLimit(mTabAdapter.getCount() - 1);
        mViewPager.setAdapter(mTabAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public void showAppBar() {
        mAppBarWrapperLayout.show();
    }

    @Override
    public void hideAppBar() {
        mAppBarWrapperLayout.hide();
    }

    @Override
    public void setToolBarOnDoubleClickListener(DoubleClickToolbar.OnDoubleClickListener listener) {
        mToolbar.setOnDoubleClickListener(listener);
    }
}
