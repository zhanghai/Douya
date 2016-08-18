/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.account.content;

import android.accounts.Account;
import android.app.Activity;
import android.content.Context;
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

    private static AccountUserInfoResource newInstance(Account account, Context context) {
        //noinspection deprecation
        AccountUserInfoResource resource = new AccountUserInfoResource();
        resource.setArguments(account, context);
        return resource;
    }

    public static AccountUserInfoResource attachTo(Account account, FragmentActivity activity,
                                                   String tag, int requestCode) {
        return attachTo(account, activity, tag, true, null, requestCode);
    }

    public static AccountUserInfoResource attachTo(Account account, FragmentActivity activity) {
        return attachTo(account, activity, FRAGMENT_TAG_DEFAULT, REQUEST_CODE_INVALID);
    }

    public static AccountUserInfoResource attachTo(Account account, Fragment fragment, String tag,
                                                   int requestCode) {
        return attachTo(account, fragment.getActivity(), tag, false, fragment, requestCode);
    }

    public static AccountUserInfoResource attachTo(Account account, Fragment fragment) {
        return attachTo(account, fragment, FRAGMENT_TAG_DEFAULT, REQUEST_CODE_INVALID);
    }

    private static AccountUserInfoResource attachTo(Account account, FragmentActivity activity,
                                                    String tag, boolean targetAtActivity,
                                                    Fragment targetFragment, int requestCode) {
        AccountUserInfoResource resource = FragmentUtils.findByTag(activity, tag);
        if (resource == null) {
            resource = newInstance(account, activity);
            if (targetAtActivity) {
                resource.targetAtActivity(requestCode);
            } else {
                resource.targetAtFragment(targetFragment, requestCode);
            }
            FragmentUtils.add(resource, activity, tag);
        }
        return resource;
    }

    /**
     * @deprecated Use {@code attachTo()} instead.
     */
    @SuppressWarnings("deprecation")
    public AccountUserInfoResource() {}

    private void setArguments(Account account, Context context) {
        FragmentUtils.ensureArguments(this).putParcelable(EXTRA_ACCOUNT, account);
        User user = makePartialUser(account, context);
        setArguments(user.getIdOrUid(), user, AccountUtils.getUserInfo(account, context));
    }

    private User makePartialUser(Account account, Context context) {
        User user = new User();
        //noinspection deprecation
        user.id = AccountUtils.getUserId(account, context);
        //noinspection deprecation
        user.uid = String.valueOf(user.id);
        user.name = AccountUtils.getUserName(account, context);
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
        load();
    }

    @Override
    protected void onUserInfoLoaded(UserInfo userInfo) {
        super.onUserInfoLoaded(userInfo);

        Activity activity = getActivity();
        AccountUtils.setUserName(mAccount, userInfo.name, activity);
        AccountUtils.setUserInfo(mAccount, userInfo, activity);
    }

    @Deprecated
    @Override
    public boolean hasUser() {
        throw new IllegalStateException("We always have a (partial) user");
    }

    @Deprecated
    @Override
    public User getUser() {
        throw new IllegalStateException("Call getPartialUser() instead");
    }

    public User getPartialUser() {
        return super.getUser();
    }
}
