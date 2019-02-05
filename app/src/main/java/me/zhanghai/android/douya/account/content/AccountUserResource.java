/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.account.content;

import android.accounts.Account;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import me.zhanghai.android.douya.account.util.AccountUtils;
import me.zhanghai.android.douya.network.api.info.apiv2.SimpleUser;
import me.zhanghai.android.douya.network.api.info.apiv2.User;
import me.zhanghai.android.douya.user.content.UserResource;
import me.zhanghai.android.douya.util.FragmentUtils;

public class AccountUserResource extends UserResource {

    private static final String KEY_PREFIX = AccountUserResource.class.getName() + '.';

    private final String EXTRA_ACCOUNT = KEY_PREFIX + "account";

    private Account mAccount;

    private static final String FRAGMENT_TAG_DEFAULT = AccountUserResource.class.getName();

    private static AccountUserResource newInstance(Account account) {
        //noinspection deprecation
        return new AccountUserResource().setArguments(account);
    }

    public static AccountUserResource attachTo(Account account, Fragment fragment, String tag,
                                               int requestCode) {
        FragmentActivity activity = fragment.getActivity();
        AccountUserResource instance = FragmentUtils.findByTag(activity, tag);
        if (instance == null) {
            instance = newInstance(account);
            FragmentUtils.add(instance, activity, tag);
        }
        instance.setTarget(fragment, requestCode);
        return instance;
    }

    public static AccountUserResource attachTo(Account account, Fragment fragment) {
        return attachTo(account, fragment, FRAGMENT_TAG_DEFAULT, REQUEST_CODE_INVALID);
    }

    /**
     * @deprecated Use {@code attachTo()} instead.
     */
    @SuppressWarnings("deprecation")
    public AccountUserResource() {}

    protected AccountUserResource setArguments(Account account) {
        SimpleUser partialUser = makePartialUser(account);
        super.setArguments(partialUser.getIdOrUid(), partialUser, AccountUtils.getUser(account));
        FragmentUtils.getArgumentsBuilder(this)
                .putParcelable(EXTRA_ACCOUNT, account);
        return this;
    }

    private SimpleUser makePartialUser(Account account) {
        SimpleUser partialUser = new SimpleUser();
        //noinspection deprecation
        partialUser.id = AccountUtils.getUserId(account);
        //noinspection deprecation
        partialUser.uid = String.valueOf(partialUser.id);
        partialUser.name = AccountUtils.getUserName(account);
        return partialUser;
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
    protected void onLoadSuccess(User user) {
        super.onLoadSuccess(user);

        AccountUtils.setUserName(mAccount, user.name);
        AccountUtils.setUser(mAccount, user);
    }

    @Deprecated
    @Override
    public boolean hasSimpleUser() {
        throw new IllegalStateException("We always have a (partial) user");
    }

    @Deprecated
    @Override
    public SimpleUser getSimpleUser() {
        throw new IllegalStateException("Use getPartialUser() instead");
    }

    public SimpleUser getPartialUser() {
        return super.getSimpleUser();
    }
}
