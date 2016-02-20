/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.main.ui;

import android.os.StrictMode;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.BuildConfig;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.account.util.AccountUtils;
import me.zhanghai.android.douya.broadcast.ui.BroadcastListFragment;
import me.zhanghai.android.douya.home.HomeFragment;
import me.zhanghai.android.douya.notification.ui.NotificationListFragment;
import me.zhanghai.android.douya.settings.ui.SettingsActivity;
import me.zhanghai.android.douya.ui.ActionItemBadge;
import me.zhanghai.android.douya.ui.AppBarWrapperLayout;
import me.zhanghai.android.douya.ui.TabFragmentPagerAdapter;
import me.zhanghai.android.douya.util.TransitionUtils;

public class MainActivity extends AppCompatActivity
        implements NotificationListFragment.UnreadNotificationCountListener {

    @Bind(R.id.drawer)
    DrawerLayout mDrawerLayout;
    @Bind(R.id.navigation)
    NavigationView mNavigationView;
    private LinearLayout mNavigationHeaderLayout;
    private ImageView mNavigationHeaderAvatarImage;
    private TextView mNavigationHeaderNameText;
    @Bind(R.id.notification_list_drawer)
    View mNotificationDrawer;
    @Bind(R.id.container)
    FrameLayout mContainerLayout;

    private MenuItem mNotificationMenu;
    private int mUnreadNotificationCount;

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

        mNavigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.navigation_settings:
                                onShowSettings();
                                break;
                        }
                        // TODO
                        if (menuItem.getGroupId() == R.id.navigation_group_primary) {
                            menuItem.setChecked(true);
                        }
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
        mNavigationHeaderLayout = (LinearLayout) mNavigationView.getHeaderView(0);
        mNavigationHeaderAvatarImage = ButterKnife.findById(mNavigationHeaderLayout, R.id.avatar);
        mNavigationHeaderNameText = ButterKnife.findById(mNavigationHeaderLayout, R.id.name);
        mNavigationHeaderNameText.setText(AccountUtils.getUserName(this));
        // FIXME: Check remembered checked position.
        mNavigationView.getMenu().getItem(0).setChecked(true);

        mNotificationListFragment = (NotificationListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.notification_list_fragment);
        mNotificationListFragment.setUnreadNotificationCountListener(this);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, HomeFragment.newInstance())
                    .commit();
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
                mDrawerLayout.openDrawer(mNavigationView);
                return true;
            case R.id.action_notification:
                mNotificationListFragment.refresh();
                mDrawerLayout.openDrawer(mNotificationDrawer);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(mNavigationView)) {
            mDrawerLayout.closeDrawer(mNavigationView);
        } else if (mDrawerLayout.isDrawerOpen(mNotificationDrawer)) {
            mDrawerLayout.closeDrawer(mNotificationDrawer);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onUnreadNotificationUpdate(int count) {
        mUnreadNotificationCount = count;
        if (mNotificationMenu != null) {
            ActionItemBadge.update(mNotificationMenu, mUnreadNotificationCount);
        }
    }

    private void onShowSettings() {
        startActivity(SettingsActivity.makeIntent(this));
    }

    public void setToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TransitionUtils.setupTransitionForAppBar(this);
    }

    public void refreshNotificationList() {
        mNotificationListFragment.refresh();
    }
}
