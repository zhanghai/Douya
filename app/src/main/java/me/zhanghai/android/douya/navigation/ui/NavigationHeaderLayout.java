/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.navigation.ui;

import android.accounts.Account;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.account.util.AccountUtils;
import me.zhanghai.android.douya.network.api.info.apiv2.User;
import me.zhanghai.android.douya.network.api.info.apiv2.UserInfo;
import me.zhanghai.android.douya.util.DrawableUtils;
import me.zhanghai.android.douya.util.ImageUtils;
import me.zhanghai.android.douya.util.ViewCompat;

public class NavigationHeaderLayout extends FrameLayout {

    @BindView(R.id.backdrop)
    ImageView mBackdropImage;
    @BindView(R.id.scrim)
    View mScrimView;
    @BindView(R.id.avatar)
    ImageView mAvatarImage;
    @BindView(R.id.name)
    TextView mNameText;
    @BindView(R.id.description)
    TextView mDescriptionText;
    @BindView(R.id.secondary_avatar)
    ImageView mSecondaryAvatarImage;
    @BindView(R.id.tertiary_avatar)
    ImageView mTertiaryAvatarImage;

    private Adapter mAdapter;
    private Listener mListener;

    public NavigationHeaderLayout(Context context) {
        super(context);

        init();
    }

    public NavigationHeaderLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public NavigationHeaderLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public NavigationHeaderLayout(Context context, AttributeSet attrs, int defStyleAttr,
                                  int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init();
    }

    private void init() {
        inflate(getContext(), R.layout.navigation_header_layout, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        ButterKnife.bind(this);

        ViewCompat.setBackground(mScrimView, DrawableUtils.makeScrimDrawable());
    }

    public void setAdapter(Adapter adapter) {
        mAdapter = adapter;
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public void bind() {

        if (mAdapter == null) {
            return;
        }

        bindActiveUser();
        bindInactiveUsers();
    }

    private void bindActiveUser() {
        Context context = getContext();
        final Account account = AccountUtils.getActiveAccount(context);
        UserInfo userInfo = mAdapter.getUserInfo(account);
        if (userInfo != null) {
            ImageUtils.loadNavigationAvatar(mAvatarImage, userInfo.getLargeAvatarOrAvatar(),
                    context);
            mNameText.setText(userInfo.name);
            mDescriptionText.setText(userInfo.signature);
        } else {
            User partialUser = mAdapter.getPartialUser(account);
            mAvatarImage.setImageResource(R.drawable.avatar_icon_white_inactive_64dp);
            mNameText.setText(partialUser.name);
            //noinspection deprecation
            mDescriptionText.setText(partialUser.uid);
        }
        mAvatarImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.openProfile(account);
                }
            }
        });
    }

    private void bindInactiveUsers() {
        Context context = getContext();
        Account activeAccount = AccountUtils.getActiveAccount(context);
        boolean secondaryAccountBound = false;
        boolean tertiaryAccountBound = false;
        for (Account account : AccountUtils.getAccounts(context)) {
            if (account.equals(activeAccount)) {
                continue;
            }
            if (!secondaryAccountBound) {
                bindInactiveUser(mSecondaryAvatarImage, account);
                secondaryAccountBound = true;
            } else if (!tertiaryAccountBound) {
                bindInactiveUser(mTertiaryAvatarImage, account);
                tertiaryAccountBound = true;
            }
        }
        if (!secondaryAccountBound) {
            mSecondaryAvatarImage.setVisibility(GONE);
        }
        if (!tertiaryAccountBound) {
            mTertiaryAvatarImage.setVisibility(GONE);
        }
    }

    private void bindInactiveUser(ImageView avatarImage, final Account account) {
        UserInfo userInfo = mAdapter.getUserInfo(account);
        if (userInfo != null) {
            ImageUtils.loadNavigationAvatar(avatarImage, userInfo.getLargeAvatarOrAvatar(),
                    getContext());
        } else {
            avatarImage.setImageResource(R.drawable.avatar_icon_white_inactive_64dp);
        }
        avatarImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToAccount(account);
            }
        });
    }

    public void switchToAccount(Account account) {
        AccountUtils.setActiveAccount(account, getContext());
        bind();
        if (mListener != null) {
            mListener.onActiveAccountChanged(account);
        }
    }

    public interface Adapter {
        User getPartialUser(Account account);
        UserInfo getUserInfo(Account account);
    }

    public interface Listener {
        void openProfile(Account account);
        void onActiveAccountChanged(Account newAccount);
    }
}
