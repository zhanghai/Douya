/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.navigation.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.account.content.AccountUserInfoResource;
import me.zhanghai.android.douya.account.util.AccountUtils;
import me.zhanghai.android.douya.link.NotImplementedManager;
import me.zhanghai.android.douya.network.api.info.apiv2.User;
import me.zhanghai.android.douya.network.api.info.apiv2.UserInfo;
import me.zhanghai.android.douya.settings.ui.SettingsActivity;
import me.zhanghai.android.douya.ui.DrawerManager;
import me.zhanghai.android.douya.util.ImageUtils;

public class NavigationFragment extends Fragment implements AccountUserInfoResource.Listener {

    @BindView(R.id.navigation)
    NavigationView mNavigationView;
    private LinearLayout mHeaderLayout;
    private ImageView mAvatarImage;
    private TextView mNameText;

    private AccountUserInfoResource mUserInfoResource;

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
        mHeaderLayout = (LinearLayout) mNavigationView.getHeaderView(0);
        mAvatarImage = ButterKnife.findById(mHeaderLayout, R.id.avatar);
        mNameText = ButterKnife.findById(mHeaderLayout, R.id.name);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Activity activity = getActivity();
        mUserInfoResource = AccountUserInfoResource.attachTo(
                AccountUtils.getActiveAccount(activity), this);

        if (mUserInfoResource.hasUserInfo()) {
            bindUserInfo(mUserInfoResource.getUserInfo());
        } else if (mUserInfoResource.hasUser()) {
            bindUser(mUserInfoResource.getUser());
        }
        mNavigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.navigation_home:
                                // TODO
                                break;
                            case R.id.navigation_settings:
                                showSettings();
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
    public void onLoadUserInfoError(int requestCode, VolleyError error) {

    }

    @Override
    public void onUserInfoChanged(int requestCode, UserInfo newUserInfo) {
        bindUserInfo(newUserInfo);
    }

    @Override
    public void onUserInfoWriteStarted(int requestCode) {}

    @Override
    public void onUserInfoWriteFinished(int requestCode) {}

    private void bindUser(User user) {
        // NOTE: The user object may be partial. See
        // {@link AccountUserInfoResource#getPartialUser()}.
        mNameText.setText(user.name);
        // FIXME
        mAvatarImage.setImageResource(R.drawable.avatar_icon_grey600_40dp);
    }

    private void bindUserInfo(UserInfo userInfo) {
        mNameText.setText(userInfo.name);
        // FIXME: Use correct Placeholder.
        ImageUtils.loadAvatar(mAvatarImage, userInfo.getLargeAvatarOrAvatar(), getActivity());
    }

    private void showSettings() {
        startActivity(SettingsActivity.makeIntent(getActivity()));
    }

    private void closeDrawer() {
        ((DrawerManager) getActivity()).closeDrawer(getView());
    }
}
