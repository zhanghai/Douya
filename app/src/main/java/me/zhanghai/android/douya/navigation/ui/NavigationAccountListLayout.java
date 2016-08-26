/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.navigation.ui;

import android.accounts.Account;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.TextViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.account.util.AccountUtils;
import me.zhanghai.android.douya.network.api.info.apiv2.User;
import me.zhanghai.android.douya.network.api.info.apiv2.UserInfo;
import me.zhanghai.android.douya.util.AppUtils;
import me.zhanghai.android.douya.util.ImageUtils;
import me.zhanghai.android.douya.util.ViewUtils;

public class NavigationAccountListLayout extends LinearLayout {

    @BindView(R.id.account_list)
    LinearLayout mAccountList;
    @BindView(R.id.divider)
    View mDividerView;
    @BindViews({R.id.add_account, R.id.manage_accounts})
    TextView[] mMenuItems;
    @BindView(R.id.add_account)
    TextView mAddAccountItem;
    @BindView(R.id.manage_accounts)
    TextView mManageAccountsItem;

    private Adapter mAdapter;
    private Listener mListener;

    public NavigationAccountListLayout(Context context) {
        super(context);

        init();
    }

    public NavigationAccountListLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public NavigationAccountListLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public NavigationAccountListLayout(Context context, AttributeSet attrs, int defStyleAttr,
                                       int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init();
    }

    private void init() {

        ViewUtils.inflateInto(R.layout.navigation_account_list_layout, this);
        ButterKnife.bind(this);

        final Context context = getContext();
        ColorStateList iconTintList = ViewUtils.getColorStateListFromAttrRes(
                android.R.attr.textColorSecondary, context);
        for (TextView menuItem : mMenuItems) {
            Drawable icon = TextViewCompat.getCompoundDrawablesRelative(menuItem)[0];
            icon = DrawableCompat.wrap(icon);
            DrawableCompat.setTintList(icon, iconTintList);
            TextViewCompat.setCompoundDrawablesRelative(menuItem, icon, null, null, null);
        }

        mAddAccountItem.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                AccountUtils.addAccount(AppUtils.getActivityFromContext(context));
            }
        });
        mManageAccountsItem.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                AppUtils.startActivity(new Intent(Settings.ACTION_SYNC_SETTINGS), context);
            }
        });
    }

    public void setAdapter(Adapter adapter) {
        mAdapter = adapter;
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public void bind() {

        Context context = getContext();
        List<Account> accountList = new ArrayList<>(Arrays.asList(AccountUtils.getAccounts(
                context)));
        accountList.remove(AccountUtils.getActiveAccount(context));
        Account recentOneAccount = AccountUtils.getRecentOneAccount(context);
        if (recentOneAccount != null) {
            accountList.remove(recentOneAccount);
        }
        Account recentTwoAccount = AccountUtils.getRecentTwoAccount(context);
        if (recentTwoAccount != null) {
            accountList.remove(recentTwoAccount);
        }
        if (recentOneAccount != null) {
            accountList.add(recentOneAccount);
        }
        if (recentTwoAccount != null) {
            accountList.add(recentTwoAccount);
        }

        int i = 0;
        for (final Account account : accountList) {

            if (i >= mAccountList.getChildCount()) {
                ViewUtils.inflateInto(R.layout.navigation_account_item, mAccountList);
            }
            View accountLayout = mAccountList.getChildAt(i);
            accountLayout.setVisibility(VISIBLE);
            AccountLayoutHolder holder = (AccountLayoutHolder) accountLayout.getTag();
            if (holder == null) {
                holder = new AccountLayoutHolder(accountLayout);
                accountLayout.setTag(holder);
            }

            UserInfo userInfo = mAdapter.getUserInfo(account);
            if (userInfo != null) {
                ImageUtils.loadNavigationAccountListAvatar(holder.avatarImage,
                        userInfo.getLargeAvatarOrAvatar());
            } else {
                holder.avatarImage.setImageResource(R.drawable.avatar_icon_grey600_40dp);
            }
            holder.nameText.setText(mAdapter.getPartialUser(account).name);
            accountLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        mListener.switchToAccount(account);
                    }
                }
            });

            ++i;
        }

        ViewUtils.setVisibleOrGone(mDividerView, i > 0);

        for (int count = mAccountList.getChildCount(); i < count; ++i) {
            mAccountList.getChildAt(i).setVisibility(GONE);
        }
    }

    class AccountLayoutHolder {

        @BindView(R.id.avatar)
        ImageView avatarImage;
        @BindView(R.id.name)
        TextView nameText;

        public AccountLayoutHolder(View accountLayout) {
            ButterKnife.bind(this, accountLayout);
        }
    }

    public interface Adapter {
        User getPartialUser(Account account);
        UserInfo getUserInfo(Account account);
    }

    public interface Listener {
        void switchToAccount(Account account);
    }
}
