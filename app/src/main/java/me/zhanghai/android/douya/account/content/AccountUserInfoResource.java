/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.account.content;

import android.accounts.Account;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import me.zhanghai.android.douya.account.util.AccountUtils;
import me.zhanghai.android.douya.network.api.info.apiv2.User;
import me.zhanghai.android.douya.network.api.info.apiv2.UserInfo;
import me.zhanghai.android.douya.user.content.UserInfoResource;
import me.zhanghai.android.douya.util.FragmentUtils;

public class AccountUserInfoResource extends UserInfoResource {

    private static final String KEY_PREFIX = AccountUserInfoResource.class.getName() + '.';

    private final String EXTRA_ACCOUNT = KEY_PREFIX + "account";

    private Account mAccount;

    private static final String FRAGMENT_TAG_DEFAULT = AccountUserInfoResource.class.getName();

    private static AccountUserInfoResource newInstance(Account account) {
        //noinspection deprecation
        return new AccountUserInfoResource().setArguments(account);
    }

    public static AccountUserInfoResource attachTo(Account account, Fragment fragment, String tag,
                                                   int requestCode) {
        FragmentActivity activity = fragment.getActivity();
        AccountUserInfoResource instance = FragmentUtils.findByTag(activity, tag);
        if (instance == null) {
            instance = newInstance(account);
            instance.targetAt(fragment, requestCode);
            FragmentUtils.add(instance, activity, tag);
        }
        return instance;
    }

    public static AccountUserInfoResource attachTo(Account account, Fragment fragment) {
        return attachTo(account, fragment, FRAGMENT_TAG_DEFAULT, REQUEST_CODE_INVALID);
    }

    /**
     * @deprecated Use {@code attachTo()} instead.
     */
    @SuppressWarnings("deprecation")
    public AccountUserInfoResource() {}

    protected AccountUserInfoResource setArguments(Account account) {
        User user = makePartialUser(account);
        super.setArguments(user.getIdOrUid(), user, AccountUtils.getUserInfo(account));
        FragmentUtils.ensureArguments(this)
                .putParcelable(EXTRA_ACCOUNT, account);
        return this;
    }

    private User makePartialUser(Account account) {
        User user = new User();
        //noinspection deprecation
        user.id = AccountUtils.getUserId(account);
        //noinspection deprecation
        user.uid = String.valueOf(user.id);
        user.name = AccountUtils.getUserName(account);
        return user;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAccount = getArguments().getParcelable(EXTRA_ACCOUNT);
    }

    @Override
    protected void loadOnStart() {
        // Always load, so that we can ever get refreshed.
        onLoadOnStart();
    }

    @Override
    protected void onLoadSuccess(UserInfo userInfo) {
        super.onLoadSuccess(userInfo);

        AccountUtils.setUserName(mAccount, userInfo.name);
        AccountUtils.setUserInfo(mAccount, userInfo);
    }

    @Deprecated
    @Override
    public boolean hasUser() {
        throw new IllegalStateException("We always have a (partial) user");
    }

    @Deprecated
    @Override
    public User getUser() {
        throw new IllegalStateException("Use getPartialUser() instead");
    }

    public User getPartialUser() {
        return super.getUser();
    }
}
