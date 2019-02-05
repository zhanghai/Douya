/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.navigation.ui;

import android.accounts.Account;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import androidx.core.view.ViewCompat;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.transitionseverywhere.ChangeTransform;
import com.transitionseverywhere.Fade;
import com.transitionseverywhere.Transition;
import com.transitionseverywhere.TransitionManager;
import com.transitionseverywhere.TransitionSet;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.account.util.AccountUtils;
import me.zhanghai.android.douya.network.api.info.apiv2.SimpleUser;
import me.zhanghai.android.douya.network.api.info.apiv2.User;
import me.zhanghai.android.douya.ui.CrossfadeText;
import me.zhanghai.android.douya.util.DrawableUtils;
import me.zhanghai.android.douya.util.ImageUtils;
import me.zhanghai.android.douya.util.ViewUtils;

public class NavigationHeaderLayout extends FrameLayout {

    @BindView(R.id.backdrop)
    ImageView mBackdropImage;
    @BindView(R.id.scrim)
    View mScrimView;
    @BindViews({R.id.avatar, R.id.fade_out_avatar,
            R.id.recent_one_avatar, R.id.fade_out_recent_one_avatar,
            R.id.recent_two_avatar, R.id.fade_out_recent_two_avatar})
    ImageView[] mAvatarImages;
    @BindView(R.id.avatar)
    ImageView mAvatarImage;
    @BindView(R.id.fade_out_avatar)
    ImageView mFadeOutAvatarImage;
    @BindView(R.id.recent_one_avatar)
    ImageView mRecentOneAvatarImage;
    @BindView(R.id.fade_out_recent_one_avatar)
    ImageView mFadeOutRecentOneAvatarImage;
    @BindView(R.id.recent_two_avatar)
    ImageView mRecentTwoAvatarImage;
    @BindView(R.id.fade_out_recent_two_avatar)
    ImageView mFadeOutRecentTwoAvatarImage;
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

    private boolean mAccountTransitionRunning;
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

        ViewUtils.inflateInto(R.layout.navigation_header_layout, this);
        ButterKnife.bind(this);

        mBackdropImage.setImageResource(R.drawable.profile_header_backdrop);
        ViewCompat.setBackground(mScrimView, DrawableUtils.makeScrimDrawable());
        mInfoLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showAccountList(!mShowingAccountList);
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

    public void onAccountListChanged() {
        boolean needReload = !mActiveAccount.equals(AccountUtils.getActiveAccount());
        bind();
        if (mListener != null && needReload) {
            mListener.onAccountTransitionStart();
            mListener.onAccountTransitionEnd();
        }
    }

    private void bindActiveUser() {

        mActiveAccount = AccountUtils.getActiveAccount();

        User user = mAdapter.getUser(mActiveAccount);
        if (user != null) {
            bindAvatarImage(mAvatarImage, user.getLargeAvatarOrAvatar());
            mNameText.setText(user.name);
            if (!TextUtils.isEmpty(user.signature)) {
                mDescriptionText.setText(user.signature);
            } else {
                //noinspection deprecation
                mDescriptionText.setText(user.uid);
            }
        } else {
            SimpleUser partialUser = mAdapter.getPartialUser(mActiveAccount);
            bindAvatarImage(mAvatarImage, null);
            mNameText.setText(partialUser.name);
            //noinspection deprecation
            mDescriptionText.setText(partialUser.uid);
        }
        mAvatarImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAccountTransitionRunning) {
                    return;
                }
                if (mListener != null) {
                    mListener.openProfile(mActiveAccount);
                }
            }
        });
        mAvatarImage.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mAccountTransitionRunning) {
                    return false;
                }
                if (mListener != null) {
                    mListener.openProfile(mActiveAccount);
                }
                return true;
            }
        });
        //mBackdropImage.setImageResource();
    }

    private void bindRecentUsers() {
        mRecentOneAccount = AccountUtils.getRecentOneAccount();
        bindRecentUser(mRecentOneAvatarImage, mRecentOneAccount);
        mRecentTwoAccount = AccountUtils.getRecentTwoAccount();
        bindRecentUser(mRecentTwoAvatarImage, mRecentTwoAccount);
    }

    private void bindRecentUser(ImageView avatarImage, final Account account) {

        if (account == null) {
            avatarImage.setVisibility(GONE);
            return;
        }

        avatarImage.setVisibility(VISIBLE);
        User user = mAdapter.getUser(account);
        if (user != null) {
            bindAvatarImage(avatarImage, user.getLargeAvatarOrAvatar());
        } else {
            bindAvatarImage(avatarImage, null);
        }
        avatarImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToAccountWithTransitionIfNotRunning(account);
            }
        });
        avatarImage.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mAccountTransitionRunning) {
                    return false;
                }
                if (mListener != null) {
                    mListener.openProfile(account);
                }
                return true;
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
                setAvatarImageFrom(avatarImage, anotherAvatarImage);
                return;
            }
        }

        ImageUtils.loadNavigationHeaderAvatar(avatarImage, avatarUrl);
    }

    private void setAvatarImageFrom(ImageView toAvatarImage, ImageView fromAvatarImage) {
        if (toAvatarImage == fromAvatarImage) {
            return;
        }
        toAvatarImage.setImageDrawable(fromAvatarImage.getDrawable());
        toAvatarImage.setTag(fromAvatarImage.getTag());
    }

    public void switchToAccountWithTransitionIfNotRunning(Account account) {

        if (mAccountTransitionRunning) {
            return;
        }

        showAccountList(false);

        if (AccountUtils.isActiveAccount(account)) {
            return;
        }

        AccountUtils.setActiveAccount(account);
        if (account.equals(mRecentOneAccount)) {
            beginAvatarTransitionFromRecent(mRecentOneAvatarImage);
        } else if (account.equals(mRecentTwoAccount)) {
            beginAvatarTransitionFromRecent(mRecentTwoAvatarImage);
        } else {
            beginAvatarTransitionFromNonRecent();
        }
        bind();
    }

    private void beginAvatarTransitionFromRecent(ImageView recentAvatarImage) {
        beginAvatarTransition(recentAvatarImage, mAvatarImage, null);
    }

    private void beginAvatarTransitionFromNonRecent() {
        beginAvatarTransition(mAvatarImage, mRecentOneAvatarImage,
                mRecentTwoAccount != null ? mRecentTwoAvatarImage : null);
    }

    private void beginAvatarTransition(ImageView moveAvatarOneImage, ImageView moveAvatarTwoImage,
                                       ImageView moveAvatarThreeImage) {

        ImageView appearAvatarImage = moveAvatarOneImage;
        ImageView disappearAvatarImage = moveAvatarThreeImage != null ? moveAvatarThreeImage
                : moveAvatarTwoImage;
        ImageView fadeOutDisappearAvatarImage =
                disappearAvatarImage == mAvatarImage ? mFadeOutAvatarImage
                        : disappearAvatarImage == mRecentOneAvatarImage ?
                        mFadeOutRecentOneAvatarImage : mFadeOutRecentTwoAvatarImage;

        TransitionSet transitionSet = new TransitionSet();
        int duration = ViewUtils.getLongAnimTime(getContext());
        // Will be set on already added and newly added transitions.
        transitionSet.setDuration(duration);
        // NOTE: TransitionSet.setInterpolator() won't have any effect on platform versions.
        // https://code.google.com/p/android/issues/detail?id=195495
        transitionSet.setInterpolator(new FastOutSlowInInterpolator());

        Fade fadeOutAvatar = new Fade(Fade.OUT);
        setAvatarImageFrom(fadeOutDisappearAvatarImage, disappearAvatarImage);
        fadeOutDisappearAvatarImage.setVisibility(VISIBLE);
        fadeOutAvatar.addTarget(fadeOutDisappearAvatarImage);
        transitionSet.addTransition(fadeOutAvatar);
        // Make it finish before new avatar arrives.
        fadeOutAvatar.setDuration(duration / 2);

        Fade fadeInAvatar = new Fade(Fade.IN);
        appearAvatarImage.setVisibility(INVISIBLE);
        fadeInAvatar.addTarget(appearAvatarImage);
        transitionSet.addTransition(fadeInAvatar);

        ChangeTransform changeAppearAvatarTransform = new ChangeTransform();
        appearAvatarImage.setScaleX(0.8f);
        appearAvatarImage.setScaleY(0.8f);
        changeAppearAvatarTransform.addTarget(appearAvatarImage);
        transitionSet.addTransition(changeAppearAvatarTransform);

        addChangeMoveToAvatarTransformToTransitionSet(moveAvatarOneImage, moveAvatarTwoImage,
                transitionSet);

        if (moveAvatarThreeImage != null) {
            addChangeMoveToAvatarTransformToTransitionSet(moveAvatarTwoImage, moveAvatarThreeImage,
                    transitionSet);
        }

        CrossfadeText crossfadeText = new CrossfadeText();
        crossfadeText.addTarget(mNameText);
        crossfadeText.addTarget(mDescriptionText);
        transitionSet.addTransition(crossfadeText);

        transitionSet.addListener(new Transition.TransitionListenerAdapter() {
            @Override
            public void onTransitionEnd(Transition transition) {

                mAccountTransitionRunning = false;
                mInfoLayout.setEnabled(true);

                if (mListener != null) {
                    mListener.onAccountTransitionEnd();
                }
            }
        });
        mInfoLayout.setEnabled(false);
        TransitionManager.beginDelayedTransition(this, transitionSet);
        mAccountTransitionRunning = true;
        if (mListener != null) {
            mListener.onAccountTransitionStart();
        }

        fadeOutDisappearAvatarImage.setVisibility(INVISIBLE);

        appearAvatarImage.setVisibility(VISIBLE);
        appearAvatarImage.setScaleX(1);
        appearAvatarImage.setScaleY(1);

        resetMoveToAvatarTransform(moveAvatarTwoImage);
        if (moveAvatarThreeImage != null) {
            resetMoveToAvatarTransform(moveAvatarThreeImage);
        }
    }

    private void addChangeMoveToAvatarTransformToTransitionSet(ImageView moveFromAvatarImage,
                                                               ImageView moveToAvatarImage,
                                                               TransitionSet transitionSet) {
        ChangeTransform changeMoveToAvatarTransform = new ChangeTransform();
        moveToAvatarImage.setX(moveFromAvatarImage.getLeft()
                + (moveFromAvatarImage.getWidth() - moveToAvatarImage.getWidth()) / 2);
        moveToAvatarImage.setY(moveFromAvatarImage.getTop()
                + (moveFromAvatarImage.getHeight() - moveToAvatarImage.getHeight()) / 2);
        moveToAvatarImage.setScaleX((float) ViewUtils.getWidthExcludingPadding(moveFromAvatarImage)
                / ViewUtils.getWidthExcludingPadding(moveToAvatarImage));
        moveToAvatarImage.setScaleY((float) ViewUtils.getHeightExcludingPadding(moveFromAvatarImage)
                / ViewUtils.getHeightExcludingPadding(moveToAvatarImage));
        changeMoveToAvatarTransform.addTarget(moveToAvatarImage);
        transitionSet.addTransition(changeMoveToAvatarTransform);
    }

    private void resetMoveToAvatarTransform(ImageView moveToAvatarImage) {
        moveToAvatarImage.setTranslationX(0);
        moveToAvatarImage.setTranslationY(0);
        moveToAvatarImage.setScaleX(1);
        moveToAvatarImage.setScaleY(1);
    }

    public boolean isShowingAccountList() {
        return mShowingAccountList;
    }

    public void setShowingAccountList(boolean showing) {
        showAccountList(showing, false);
    }

    private void showAccountList(boolean show, boolean animate) {

        if (mShowingAccountList == show) {
            return;
        }

        if (mListener == null) {
            return;
        }

        float rotation = show ? 180 : 0;
        if (animate) {
            mDropDownImage.animate()
                    .rotation(rotation)
                    .setDuration(ViewUtils.getShortAnimTime(getContext()))
                    .start();
        } else {
            mDropDownImage.setRotation(rotation);
        }
        mListener.showAccountList(show);
        mShowingAccountList = show;
    }

    private void showAccountList(boolean show) {
        showAccountList(show, true);
    }

    public interface Adapter {
        SimpleUser getPartialUser(Account account);
        User getUser(Account account);
    }

    public interface Listener {
        void openProfile(Account account);
        void showAccountList(boolean show);
        void onAccountTransitionStart();
        void onAccountTransitionEnd();
    }
}
