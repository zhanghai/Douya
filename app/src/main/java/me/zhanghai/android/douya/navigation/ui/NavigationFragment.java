/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.navigation.ui;

import android.accounts.Account;
import android.accounts.OnAccountsUpdateListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.VolleyError;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.account.content.AccountUserInfoResource;
import me.zhanghai.android.douya.account.util.AccountUtils;
import me.zhanghai.android.douya.link.NotImplementedManager;
import me.zhanghai.android.douya.network.api.info.apiv2.User;
import me.zhanghai.android.douya.network.api.info.apiv2.UserInfo;
import me.zhanghai.android.douya.profile.ui.ProfileActivity;
import me.zhanghai.android.douya.settings.ui.SettingsActivity;
import me.zhanghai.android.douya.user.content.UserInfoResource;
import me.zhanghai.android.douya.util.ViewUtils;

public class NavigationFragment extends Fragment implements OnAccountsUpdateListener,
        AccountUserInfoResource.Listener, NavigationHeaderLayout.Adapter,
        NavigationHeaderLayout.Listener, NavigationAccountListLayout.Adapter,
        NavigationAccountListLayout.Listener, ConfirmRemoveCurrentAccountDialogFragment.Listener {

    private static final String KEY_PREFIX = NavigationFragment.class.getName() + '.';

    private static final String KEY_SHOWING_ACCOUNT_LIST = KEY_PREFIX + "showing_account_list";
    private static final String KEY_NEED_RELOAD_FOR_ACTIVE_ACCOUNT_CHANGE =
            KEY_PREFIX + "need_reload_for_active_account_change";

    @BindView(R.id.navigation)
    NavigationView mNavigationView;
    private NavigationHeaderLayout mHeaderLayout;

    private ArrayMap<Account, AccountUserInfoResource> mUserInfoResourceMap;

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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.navigation_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);
        mHeaderLayout = (NavigationHeaderLayout) mNavigationView.getHeaderView(0);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mUserInfoResourceMap = new ArrayMap<>();
        for (Account account : AccountUtils.getAccounts()) {
            mUserInfoResourceMap.put(account, AccountUserInfoResource.attachTo(account, this,
                    account.name, -1));
        }

        AccountUtils.addOnAccountListUpdatedListener(this);

        mHeaderLayout.setAdapter(this);
        mHeaderLayout.setListener(this);
        mHeaderLayout.bind();
        mNavigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.navigation_home:
                                // TODO
                                break;
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
                    }
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

        for (AccountUserInfoResource userInfoResource : mUserInfoResourceMap.values()) {
            userInfoResource.detach();
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

        ArrayMap<Account, AccountUserInfoResource> oldUserInfoResourceMap = mUserInfoResourceMap;
        mUserInfoResourceMap = new ArrayMap<>();
        for (Account account : AccountUtils.getAccounts()) {
            mUserInfoResourceMap.put(account, AccountUserInfoResource.attachTo(account, this,
                    account.name, -1));
            oldUserInfoResourceMap.remove(account);
        }
        for (AccountUserInfoResource userInfoResource : oldUserInfoResourceMap.values()) {
            userInfoResource.detach();
        }

        mHeaderLayout.onAccountListChanged();
        mNavigationViewAdapter.onAccountListChanged();
    }

    @Override
    public void onLoadUserInfoStarted(int requestCode) {}

    @Override
    public void onLoadUserInfoFinished(int requestCode) {}

    @Override
    public void onLoadUserInfoError(int requestCode, VolleyError error) {}

    @Override
    public void onUserInfoChanged(int requestCode, UserInfo newUserInfo) {
        mHeaderLayout.bind();
        mNavigationViewAdapter.onUserInfoChanged();
    }

    @Override
    public void onUserInfoWriteStarted(int requestCode) {}

    @Override
    public void onUserInfoWriteFinished(int requestCode) {}

    @Override
    public User getPartialUser(Account account) {
        return mUserInfoResourceMap.get(account).getPartialUser();
    }

    @Override
    public UserInfo getUserInfo(Account account) {
        return mUserInfoResourceMap.get(account).get();
    }

    @Override
    public void openProfile(Account account) {
        UserInfoResource userInfoResource = mUserInfoResourceMap.get(account);
        Intent intent;
        if (userInfoResource.has()) {
            intent = ProfileActivity.makeIntent(userInfoResource.get(), getActivity());
        } else {
            // If we don't have user info, then user must also be partial. In this case we
            // can only pass user id or uid.
            intent = ProfileActivity.makeIntent(userInfoResource.getUserIdOrUid(), getActivity());
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
