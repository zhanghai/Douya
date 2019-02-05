/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.navigation.ui;

import android.accounts.Account;
import android.accounts.OnAccountsUpdateListener;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.Fragment;
import androidx.collection.ArrayMap;
import androidx.drawerlayout.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.account.content.AccountUserResource;
import me.zhanghai.android.douya.account.util.AccountUtils;
import me.zhanghai.android.douya.calendar.ui.CalendarActivity;
import me.zhanghai.android.douya.link.NotImplementedManager;
import me.zhanghai.android.douya.link.UriHandler;
import me.zhanghai.android.douya.network.api.ApiError;
import me.zhanghai.android.douya.network.api.info.apiv2.SimpleUser;
import me.zhanghai.android.douya.network.api.info.apiv2.User;
import me.zhanghai.android.douya.profile.ui.ProfileActivity;
import me.zhanghai.android.douya.settings.ui.SettingsActivity;
import me.zhanghai.android.douya.util.TintHelper;
import me.zhanghai.android.douya.util.ViewUtils;

public class NavigationFragment extends Fragment implements OnAccountsUpdateListener,
        AccountUserResource.Listener, NavigationHeaderLayout.Adapter,
        NavigationHeaderLayout.Listener, NavigationAccountListLayout.Adapter,
        NavigationAccountListLayout.Listener, ConfirmRemoveCurrentAccountDialogFragment.Listener {

    private static final String KEY_PREFIX = NavigationFragment.class.getName() + '.';

    private static final String KEY_SHOWING_ACCOUNT_LIST = KEY_PREFIX + "showing_account_list";
    private static final String KEY_NEED_RELOAD_FOR_ACTIVE_ACCOUNT_CHANGE =
            KEY_PREFIX + "need_reload_for_active_account_change";

    @BindView(R.id.navigation)
    NavigationView mNavigationView;
    private NavigationHeaderLayout mHeaderLayout;

    private ArrayMap<Account, AccountUserResource> mUserResourceMap;

    private NavigationViewAdapter mNavigationViewAdapter;

    private boolean mHasPendingAccountListChange;

    private boolean mNeedReloadForActiveAccountChange;
    private boolean mWillReloadForActiveAccountChange;

    public static NavigationFragment newInstance() {
        //noinspection deprecation
        return new NavigationFragment();
    }

    /**
     * @deprecated Use {@link #newInstance()} instead.
     */
    public NavigationFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.navigation_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);
        mHeaderLayout = (NavigationHeaderLayout) mNavigationView.getHeaderView(0);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mUserResourceMap = new ArrayMap<>();
        for (Account account : AccountUtils.getAccounts()) {
            mUserResourceMap.put(account, AccountUserResource.attachTo(account, this,
                    account.name, -1));
        }

        AccountUtils.addOnAccountListUpdatedListener(this);

        getDrawer().addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerClosed(View drawerView) {
                mHeaderLayout.setShowingAccountList(false);
            }
        });
        mHeaderLayout.setAdapter(this);
        mHeaderLayout.setListener(this);
        mHeaderLayout.bind();
        Activity activity = getActivity();
        if (!ViewUtils.isLightTheme(activity)) {
            TintHelper.setNavigationItemTint(mNavigationView, ViewUtils.getColorFromAttrRes(
                    android.R.attr.textColorPrimary, Color.BLACK, activity));
        }
        mNavigationView.setNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.navigation_home:
                    break;
                case R.id.navigation_book:
                    // TODO
                    UriHandler.open("https://book.douban.com/", getActivity());
                    break;
                case R.id.navigation_movie:
                    // TODO
                    UriHandler.open("https://movie.douban.com/", getActivity());
                    break;
                case R.id.navigation_music:
                    // TODO
                    UriHandler.open("https://music.douban.com/", getActivity());
                    break;
                //case R.id.navigation_calendar:
                //    openCalendar();
                //    break;
                case R.id.navigation_settings:
                    openSettings();
                    break;
                default:
                    // TODO
                    NotImplementedManager.showNotYetImplementedToast(getActivity());
            }
            // TODO
            if (menuItem.getGroupId() == R.id.navigation_group_primary) {
                menuItem.setChecked(true);
            }
            getDrawer().closeDrawer(getView());
            return true;
        });
        // FIXME: Check remembered checked position.
        mNavigationView.getMenu().getItem(0).setChecked(true);
        mNavigationViewAdapter = NavigationViewAdapter.override(mNavigationView, this, this);

        if (savedInstanceState != null) {
            mHeaderLayout.setShowingAccountList(savedInstanceState.getBoolean(
                    KEY_SHOWING_ACCOUNT_LIST));
            if (savedInstanceState.getBoolean(KEY_NEED_RELOAD_FOR_ACTIVE_ACCOUNT_CHANGE)) {
                reloadForActiveAccountChange();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mHasPendingAccountListChange) {
            mHasPendingAccountListChange = false;
            onAccountListChanged();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // NavigationHeaderLayout resides inside a RecyclerView which cannot save its own instance
        // state.
        outState.putBoolean(KEY_SHOWING_ACCOUNT_LIST, mHeaderLayout.isShowingAccountList());
        outState.putBoolean(KEY_NEED_RELOAD_FOR_ACTIVE_ACCOUNT_CHANGE,
                mNeedReloadForActiveAccountChange);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        AccountUtils.removeOnAccountListUpdatedListener(this);

        for (AccountUserResource userResource : mUserResourceMap.values()) {
            userResource.detach();
        }
    }

    @Override
    public void onAccountsUpdated(Account[] accounts) {

        // In case the only account is removed.
        if (AccountUtils.getActiveAccount() == null) {
            AccountUtils.ensureActiveAccountAvailability(getActivity());
            return;
        }

        if (isResumed()) {
            onAccountListChanged();
        } else {
            mHasPendingAccountListChange = true;
        }
    }

    private void onAccountListChanged() {

        ArrayMap<Account, AccountUserResource> oldUserResourceMap = mUserResourceMap;
        mUserResourceMap = new ArrayMap<>();
        for (Account account : AccountUtils.getAccounts()) {
            mUserResourceMap.put(account, AccountUserResource.attachTo(account, this,
                    account.name, -1));
            oldUserResourceMap.remove(account);
        }
        for (AccountUserResource userResource : oldUserResourceMap.values()) {
            userResource.detach();
        }

        mHeaderLayout.onAccountListChanged();
        mNavigationViewAdapter.onAccountListChanged();
    }

    @Override
    public void onLoadUserStarted(int requestCode) {}

    @Override
    public void onLoadUserFinished(int requestCode) {}

    @Override
    public void onLoadUserError(int requestCode, ApiError error) {}

    @Override
    public void onUserChanged(int requestCode, User newUser) {
        mHeaderLayout.bind();
        mNavigationViewAdapter.onUserChanged();
    }

    @Override
    public void onUserWriteStarted(int requestCode) {}

    @Override
    public void onUserWriteFinished(int requestCode) {}

    @Override
    public SimpleUser getPartialUser(Account account) {
        return mUserResourceMap.get(account).getPartialUser();
    }

    @Override
    public User getUser(Account account) {
        return mUserResourceMap.get(account).get();
    }

    @Override
    public void openProfile(Account account) {
        AccountUserResource userResource = mUserResourceMap.get(account);
        Intent intent;
        if (userResource.has()) {
            // User info contains information such as isFollowed, which is affected by active user.
            intent = ProfileActivity.makeIntent((SimpleUser) userResource.get(), getActivity());
        } else {
            // If we don't have user info, then user must also be partial. In this case we
            // can only pass user id or uid.
            intent = ProfileActivity.makeIntent(userResource.getUserIdOrUid(), getActivity());
        }
        startActivity(intent);
    }

    @Override
    public void showAccountList(boolean show) {
        mNavigationViewAdapter.showAccountList(show);
    }

    @Override
    public void onAccountTransitionStart() {
        mNeedReloadForActiveAccountChange = true;
    }

    @Override
    public void onAccountTransitionEnd() {
        reloadForActiveAccountChange();
    }

    private void reloadForActiveAccountChange() {

        if (getNavigationHost() == null) {
            return;
        }

        DrawerLayout drawerLayout = getDrawer();
        View drawerView = getView();
        boolean drawerVisible = drawerLayout.isDrawerVisible(drawerView);
        if (!mWillReloadForActiveAccountChange) {
            mWillReloadForActiveAccountChange = true;
            Runnable reloadRunnable = new Runnable() {
                @Override
                public void run() {
                    if (getNavigationHost() != null) {
                        getNavigationHost().reloadForActiveAccountChange();
                        mWillReloadForActiveAccountChange = false;
                        mNeedReloadForActiveAccountChange = false;
                    }
                }
            };
            if (drawerVisible) {
                ViewUtils.postOnDrawerClosed(drawerLayout, reloadRunnable);
            } else {
                reloadRunnable.run();
            }
        }
        if (drawerVisible) {
            drawerLayout.closeDrawer(drawerView);
        }
    }

    @Override
    public void switchToAccount(Account account) {
        mHeaderLayout.switchToAccountWithTransitionIfNotRunning(account);
    }

    @Override
    public void onRemoveCurrentAccount() {
        ConfirmRemoveCurrentAccountDialogFragment.show(this);
    }

    @Override
    public void removeCurrentAccount() {
        mHeaderLayout.setShowingAccountList(false);
        Account oldActiveAccount = AccountUtils.getActiveAccount();
        AccountUtils.setActiveAccount(AccountUtils.getRecentOneAccount());
        AccountUtils.removeAccount(oldActiveAccount);
        // Calls onAccountsUpdated() later.
    }

    private void openCalendar() {
        startActivity(CalendarActivity.makeIntent(getActivity()));
    }

    private void openSettings() {
        startActivity(SettingsActivity.makeIntent(getActivity()));
    }

    private DrawerLayout getDrawer() {
        return getNavigationHost().getDrawer();
    }

    private Host getNavigationHost() {
        return (Host) getActivity();
    }

    public interface Host {
        DrawerLayout getDrawer();
        void reloadForActiveAccountChange();
    }
}
