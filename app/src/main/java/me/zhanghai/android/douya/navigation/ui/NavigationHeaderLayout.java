/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.navigation.ui;

import android.accounts.Account;
import android.animation.TimeInterpolator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.transitionseverywhere.ChangeTransform;
import com.transitionseverywhere.Fade;
import com.transitionseverywhere.TransitionManager;
import com.transitionseverywhere.TransitionSet;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.account.util.AccountUtils;
import me.zhanghai.android.douya.network.api.info.apiv2.User;
import me.zhanghai.android.douya.network.api.info.apiv2.UserInfo;
import me.zhanghai.android.douya.ui.CrossfadeText;
import me.zhanghai.android.douya.util.DrawableUtils;
import me.zhanghai.android.douya.util.ImageUtils;
import me.zhanghai.android.douya.util.ViewCompat;
import me.zhanghai.android.douya.util.ViewUtils;

public class NavigationHeaderLayout extends FrameLayout {

    @BindView(R.id.backdrop)
    ImageView mBackdropImage;
    @BindView(R.id.scrim)
    View mScrimView;
    @BindView(R.id.avatar)
    ImageView mAvatarImage;
    @BindView(R.id.fade_out_avatar)
    ImageView mFadeOutAvatarImage;
    @BindView(R.id.recent_one_avatar)
    ImageView mRecentOneAvatarImage;
    @BindView(R.id.recent_two_avatar)
    ImageView mRecentTwoAvatarImage;
    @BindViews({R.id.avatar, R.id.fade_out_avatar, R.id.recent_one_avatar, R.id.recent_two_avatar})
    ImageView[] mAvatarImages;
    @BindView(R.id.info)
    LinearLayout mInfoLayout;
    @BindView(R.id.name)
    TextView mNameText;
    @BindView(R.id.description)
    TextView mDescriptionText;
    @BindView(R.id.dropDown)
    ImageView mDropDownImage;

    private Adapter mAdapter;
    private Listener mListener;

    private Account mActiveAccount;
    private Account mRecentOneAccount;
    private Account mRecentTwoAccount;

    private boolean mShowingAccountList;

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
        mInfoLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleShowingAccountList();
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

        if (mAdapter == null) {
            return;
        }

        bindActiveUser();
        bindRecentUsers();
    }

    private void bindActiveUser() {

        Context context = getContext();
        mActiveAccount = AccountUtils.getActiveAccount(context);

        UserInfo userInfo = mAdapter.getUserInfo(mActiveAccount);
        if (userInfo != null) {
            bindAvatarImage(mAvatarImage, userInfo.getLargeAvatarOrAvatar());
            mNameText.setText(userInfo.name);
            mDescriptionText.setText(userInfo.signature);
        } else {
            User partialUser = mAdapter.getPartialUser(mActiveAccount);
            bindAvatarImage(mAvatarImage, null);
            mNameText.setText(partialUser.name);
            //noinspection deprecation
            mDescriptionText.setText(partialUser.uid);
        }
        mAvatarImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.openProfile(mActiveAccount);
                }
            }
        });
        mBackdropImage.setImageResource(R.color.grey_200);
    }

    private void bindRecentUsers() {
        Context context = getContext();
        mRecentOneAccount = AccountUtils.getRecentOneAccount(context);
        bindRecentUser(mRecentOneAvatarImage, mRecentOneAccount);
        mRecentTwoAccount = AccountUtils.getRecentTwoAccount(context);
        bindRecentUser(mRecentTwoAvatarImage, mRecentTwoAccount);
    }

    private void bindRecentUser(ImageView avatarImage, final Account account) {

        if (account == null) {
            avatarImage.setVisibility(GONE);
            return;
        }

        UserInfo userInfo = mAdapter.getUserInfo(account);
        if (userInfo != null) {
            bindAvatarImage(avatarImage, userInfo.getLargeAvatarOrAvatar());
        } else {
            bindAvatarImage(avatarImage, null);
        }
        avatarImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToAccount(account);
            }
        });
    }

    private void bindAvatarImage(ImageView avatarImage, String avatarUrl) {

        if (TextUtils.isEmpty(avatarUrl)) {
            avatarImage.setImageResource(R.drawable.avatar_icon_white_inactive_64dp);
            avatarImage.setTag(null);
            return;
        }

        for (ImageView anotherAvatarImage : mAvatarImages) {
            String anotherAvatarUrl = (String) anotherAvatarImage.getTag();
            if (TextUtils.equals(anotherAvatarUrl , avatarUrl)) {
                if (anotherAvatarImage != avatarImage) {
                    avatarImage.setImageDrawable(anotherAvatarImage.getDrawable());
                    avatarImage.setTag(avatarUrl);
                }
                return;
            }
        }

        ImageUtils.loadNavigationAvatar(avatarImage, avatarUrl, getContext());
    }

    public void switchToAccount(Account account) {

        Context context = getContext();
        AccountUtils.setActiveAccount(account, context);
        if (account.equals(mRecentOneAccount)) {
            transitionWithRecent(mRecentOneAvatarImage);
        } else if (account.equals(mRecentTwoAccount)) {
            transitionWithRecent(mRecentTwoAvatarImage);
        }
        bind();

        if (mListener != null) {
            mListener.onActiveAccountChanged(account);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void transitionWithRecent(ImageView recentAvatarImage) {

        TransitionSet transitionSet = new TransitionSet();
        int duration = ViewUtils.getLongAnimTime(getContext());
        // Will be set on already added and newly added transitions.
        transitionSet.setDuration(duration);
        // NOTE: TransitionSet.setInterpolator() won't have any effect on platform versions.
        // https://code.google.com/p/android/issues/detail?id=195495
        transitionSet.setInterpolator(new FastOutSlowInInterpolator());

        Fade fadeAvatarOut = new Fade(Fade.OUT);
        mFadeOutAvatarImage.setImageDrawable(mAvatarImage.getDrawable());
        mFadeOutAvatarImage.setTag(mAvatarImage.getTag());
        mFadeOutAvatarImage.setVisibility(VISIBLE);
        fadeAvatarOut.addTarget(mFadeOutAvatarImage);
        transitionSet.addTransition(fadeAvatarOut);
        // Make it finish before new avatar arrives.
        fadeAvatarOut.setDuration(duration / 2);

        Fade fadeIn = new Fade(Fade.IN);
        recentAvatarImage.setVisibility(INVISIBLE);
        fadeIn.addTarget(recentAvatarImage);
        transitionSet.addTransition(fadeIn);

        ChangeTransform changeTransform = new ChangeTransform();
        recentAvatarImage.setScaleX(0.8f);
        recentAvatarImage.setScaleY(0.8f);
        changeTransform.addTarget(recentAvatarImage);

        mAvatarImage.setX(recentAvatarImage.getLeft()
                - (mAvatarImage.getWidth() - recentAvatarImage.getWidth()) / 2);
        mAvatarImage.setY(recentAvatarImage.getTop()
                - (mAvatarImage.getHeight() - recentAvatarImage.getHeight()) / 2);
        mAvatarImage.setScaleX((float) (recentAvatarImage.getWidth()
                - recentAvatarImage.getPaddingLeft() - recentAvatarImage.getPaddingRight())
                / mAvatarImage.getWidth());
        mAvatarImage.setScaleY((float) (recentAvatarImage.getHeight()
                - recentAvatarImage.getPaddingTop() - recentAvatarImage.getPaddingBottom())
                / mAvatarImage.getHeight());
        changeTransform.addTarget(mAvatarImage);
        transitionSet.addTransition(changeTransform);

        CrossfadeText crossfadeText = new CrossfadeText();
        crossfadeText.addTarget(mNameText);
        crossfadeText.addTarget(mDescriptionText);
        transitionSet.addTransition(crossfadeText);

        TransitionManager.beginDelayedTransition(this, transitionSet);

        mFadeOutAvatarImage.setVisibility(INVISIBLE);

        recentAvatarImage.setVisibility(VISIBLE);
        recentAvatarImage.setScaleX(1);
        recentAvatarImage.setScaleY(1);

        mAvatarImage.setTranslationX(0);
        mAvatarImage.setTranslationY(0);
        mAvatarImage.setScaleX(1);
        mAvatarImage.setScaleY(1);
    }

    private void toggleShowingAccountList() {

        if (mListener == null) {
            return;
        }

        mShowingAccountList = !mShowingAccountList;
        mListener.showAccountList(mShowingAccountList);
        mDropDownImage.animate()
                .rotation(mShowingAccountList ? 180 : 0)
                .setDuration(ViewUtils.getShortAnimTime(getContext()))
                .start();
    }

    public interface Adapter {
        User getPartialUser(Account account);
        UserInfo getUserInfo(Account account);
    }

    public interface Listener {
        void openProfile(Account account);
        void showAccountList(boolean show);
        void onActiveAccountChanged(Account newAccount);
    }
}
