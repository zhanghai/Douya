/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.navigation.ui;

import android.accounts.Account;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
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
import me.zhanghai.android.douya.ui.DrawerManager;
import me.zhanghai.android.douya.user.content.UserInfoResource;

public class NavigationFragment extends Fragment implements AccountUserInfoResource.Listener,
        NavigationHeaderLayout.Adapter, NavigationHeaderLayout.Listener {

    @BindView(R.id.navigation)
    NavigationView mNavigationView;
    private NavigationHeaderLayout mHeaderLayout;

    private ArrayMap<Account, AccountUserInfoResource> mUserInfoResourceMap;

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

        Activity activity = getActivity();
        mUserInfoResourceMap = new ArrayMap<>();
        for (Account account : AccountUtils.getAccounts(activity)) {
            mUserInfoResourceMap.put(account, AccountUserInfoResource.attachTo(account, this,
                    account.name, -1));
        }

        mHeaderLayout.setAdapter(this);
        mHeaderLayout.setListener(this);
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
                        closeDrawer();
                        return true;
                    }
                });
        // FIXME: Check remembered checked position.
        mNavigationView.getMenu().getItem(0).setChecked(true);
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
        return mUserInfoResourceMap.get(account).getUserInfo();
    }

    @Override
    public void openProfile(Account account) {
        UserInfoResource userInfoResource = mUserInfoResourceMap.get(account);
        Intent intent;
        if (userInfoResource.hasUserInfo()) {
            intent = ProfileActivity.makeIntent(userInfoResource.getUserInfo(), getActivity());
        } else {
            // If we don't have user info, then user must also be partial. In this case we
            // can only pass user id or uid.
            intent = ProfileActivity.makeIntent(userInfoResource.getUserIdOrUid(), getActivity());
        }
        startActivity(intent);
    }

    @Override
    public void onActiveAccountChanged(Account newAccount) {
        // TODO
    }

    private void openSettings() {
        startActivity(SettingsActivity.makeIntent(getActivity()));
    }

    private void closeDrawer() {
        ((DrawerManager) getActivity()).closeDrawer(getView());
    }
}
