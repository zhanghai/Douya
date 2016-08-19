/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.main.ui;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.BuildConfig;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.account.util.AccountUtils;
import me.zhanghai.android.douya.home.HomeFragment;
import me.zhanghai.android.douya.link.NotImplementedManager;
import me.zhanghai.android.douya.navigation.ui.NavigationFragment;
import me.zhanghai.android.douya.notification.ui.NotificationListFragment;
import me.zhanghai.android.douya.scalpel.ScalpelHelperFragment;
import me.zhanghai.android.douya.ui.ActionItemBadge;
import me.zhanghai.android.douya.ui.DrawerManager;
import me.zhanghai.android.douya.util.FragmentUtils;
import me.zhanghai.android.douya.util.TransitionUtils;

public class MainActivity extends AppCompatActivity
        implements DrawerManager, NotificationListFragment.UnreadNotificationCountListener {

    @BindView(R.id.drawer)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.notification_list_drawer)
    View mNotificationDrawer;
    @BindView(R.id.container)
    FrameLayout mContainerLayout;

    private MenuItem mNotificationMenu;
    private int mUnreadNotificationCount;

    private NavigationFragment mNavigationFragment;
    private NotificationListFragment mNotificationListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build());
        }

        // Was Theme.Douya.MainActivity.ColdStart.
        setTheme(R.style.Theme_Douya_MainActivity);

        TransitionUtils.setupTransitionBeforeDecorate(this);

        super.onCreate(savedInstanceState);

        if (!AccountUtils.ensureAccountAvailability(this)) {
            return;
        }

        setContentView(R.layout.main_activity);
        TransitionUtils.setupTransitionAfterSetContentView(this);
        ButterKnife.bind(this);

        ScalpelHelperFragment.attachTo(this);

        mNavigationFragment = FragmentUtils.findById(this, R.id.navigation_fragment);
        mNotificationListFragment = FragmentUtils.findById(this, R.id.notification_list_fragment);
        mNotificationListFragment.setUnreadNotificationCountListener(this);

        if (savedInstanceState == null) {
            FragmentUtils.add(HomeFragment.newInstance(), this, R.id.container);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        mNotificationMenu = menu.findItem(R.id.action_notification);
        ActionItemBadge.setup(mNotificationMenu, R.drawable.notifications_icon_white_24dp,
                mUnreadNotificationCount, this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(mNavigationFragment.getView());
                return true;
            case R.id.action_notification:
                mNotificationListFragment.refresh();
                mDrawerLayout.openDrawer(mNotificationDrawer);
                return true;
            case R.id.action_doumail:
                NotImplementedManager.openDoumail(this);
                return true;
            case R.id.action_search:
                // TODO
                NotImplementedManager.openSearch(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(mNavigationFragment.getView())) {
            mDrawerLayout.closeDrawer(mNavigationFragment.getView());
        } else if (mDrawerLayout.isDrawerOpen(mNotificationDrawer)) {
            mDrawerLayout.closeDrawer(mNotificationDrawer);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void setSupportActionBar(@Nullable Toolbar toolbar) {
        super.setSupportActionBar(toolbar);

        TransitionUtils.setupTransitionForAppBar(this);
    }

    @Override
    public void openDrawer(View drawerView) {
        mDrawerLayout.openDrawer(drawerView);
    }

    @Override
    public void closeDrawer(View drawerView) {
        mDrawerLayout.closeDrawer(drawerView);
    }

    @Override
    public void onUnreadNotificationUpdate(int count) {
        mUnreadNotificationCount = count;
        if (mNotificationMenu != null) {
            ActionItemBadge.update(mNotificationMenu, mUnreadNotificationCount);
        }
    }

    private void onShowSettings() {

    }

    public void refreshNotificationList() {
        mNotificationListFragment.refresh();
    }
}
