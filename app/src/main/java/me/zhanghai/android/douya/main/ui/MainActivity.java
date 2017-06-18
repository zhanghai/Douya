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
import me.zhanghai.android.douya.util.FragmentUtils;
import me.zhanghai.android.douya.util.TransitionUtils;

public class MainActivity extends AppCompatActivity implements NavigationFragment.Host,
        NotificationListFragment.Listener {

    @BindView(R.id.drawer)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.notification_list_drawer)
    View mNotificationDrawer;
    @BindView(R.id.container)
    FrameLayout mContainerLayout;

    private MenuItem mNotificationMenuItem;

    private NavigationFragment mNavigationFragment;
    private NotificationListFragment mNotificationListFragment;
    // FIXME
    private HomeFragment mMainFragment;

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

        if (!AccountUtils.ensureActiveAccountAvailability(this)) {
            return;
        }

        setContentView(R.layout.main_activity);
        TransitionUtils.setupTransitionAfterSetContentView(this);
        ButterKnife.bind(this);

        ScalpelHelperFragment.attachTo(this);

        mNavigationFragment = FragmentUtils.findById(this, R.id.navigation_fragment);

        if (savedInstanceState == null) {
            addFragments();
        } else {
            mMainFragment = FragmentUtils.findById(this, R.id.container);
            mNotificationListFragment = FragmentUtils.findById(this, R.id.notification_list_drawer);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main, menu);
        mNotificationMenuItem = menu.findItem(R.id.action_notification);
        ActionItemBadge.setup(mNotificationMenuItem, R.drawable.notifications_icon_white_24dp,
                mNotificationListFragment.getUnreadNotificationCount(), this);
        MenuItem mDouMailMenuItem = menu.findItem(R.id.action_doumail);
        ActionItemBadge.setup(mDouMailMenuItem, R.drawable.mail_icon_white_24dp, 0, this);
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
    public DrawerLayout getDrawer() {
        return mDrawerLayout;
    }

    @Override
    public void reloadForActiveAccountChange() {
        FragmentUtils.remove(mMainFragment);
        FragmentUtils.remove(mNotificationListFragment);
        FragmentUtils.execPendingTransactions(this);
        addFragments();
        FragmentUtils.execPendingTransactions(this);
    }

    private void addFragments() {
        mMainFragment = HomeFragment.newInstance();
        FragmentUtils.add(mMainFragment, this, R.id.container);
        mNotificationListFragment = NotificationListFragment.newInstance();
        mNotificationListFragment.setListener(this);
        FragmentUtils.add(mNotificationListFragment, this, R.id.notification_list_drawer);
    }

    @Override
    public void onUnreadNotificationUpdate(int count) {
        if (mNotificationMenuItem != null) {
            ActionItemBadge.update(mNotificationMenuItem, count);
        }
    }

    public void refreshNotificationList() {
        mNotificationListFragment.refresh();
    }
}
