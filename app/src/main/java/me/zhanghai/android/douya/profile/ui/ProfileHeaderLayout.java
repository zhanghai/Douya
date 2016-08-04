/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.profile.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Outline;
import android.os.Build;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindColor;
import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.followship.content.FollowUserManager;
import me.zhanghai.android.douya.network.api.info.apiv2.User;
import me.zhanghai.android.douya.network.api.info.apiv2.UserInfo;
import me.zhanghai.android.douya.ui.FlexibleSpaceHeaderView;
import me.zhanghai.android.douya.ui.JoinedAtLocationAutoGoneTextView;
import me.zhanghai.android.douya.ui.WhiteIndeterminateProgressIconDrawable;
import me.zhanghai.android.douya.util.ImageUtils;
import me.zhanghai.android.douya.util.MathUtils;
import me.zhanghai.android.douya.util.StatusBarColorUtils;
import me.zhanghai.android.douya.util.ViewUtils;

/**
 * Set the initial layout_height to match_parent or wrap_content instead a specific value so that
 * the view measures itself correctly for the first time.
 */
public class ProfileHeaderLayout extends FrameLayout implements FlexibleSpaceHeaderView {

    @BindColor(android.R.color.transparent)
    int mStatusBarColorTransparent;
    private int mStatusBarColorFullscreen;
    @BindDimen(R.dimen.profile_large_avatar_size)
    int mLargeAvatarSize;
    @BindDimen(R.dimen.profile_small_avatar_size)
    int mSmallAvatarSize;
    @BindDimen(R.dimen.profile_small_avatar_margin_left)
    int mSmallAvatarMarginLeft;
    @BindDimen(R.dimen.profile_small_avatar_margin_top)
    int mSmallAvatarMarginTop;

    @BindView(R.id.dismiss)
    View mDismissView;
    @BindView(R.id.appBar)
    LinearLayout mAppBarLayout;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_username)
    TextView mToolbarUsernameText;
    @BindView(R.id.username)
    TextView mUsernameText;
    @BindView(R.id.signature)
    TextView mSignatureText;
    @BindView(R.id.joined_at_location)
    JoinedAtLocationAutoGoneTextView mJoinedAtLocationText;
    @BindView(R.id.follow)
    Button mFollowButton;
    @BindView(R.id.avatar_container)
    FrameLayout mAvatarContainerLayout;
    @BindView(R.id.avatar)
    CircleImageView mAvatarImage;

    private int mMaxHeight;
    private int mScroll;

    private Listener mListener;

    public ProfileHeaderLayout(Context context) {
        super(context);

        init();
    }

    public ProfileHeaderLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public ProfileHeaderLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ProfileHeaderLayout(Context context, AttributeSet attrs, int defStyleAttr,
                               int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void init() {
        // HACK: We need to delegate the outline so that elevation can work.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setOutlineProvider(new ViewOutlineProvider() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void getOutline(View view, Outline outline) {
                    // We cannot use mAppBarLayout.getOutlineProvider.getOutline() because the
                    // bounds of it is not kept in sync when this method is called.
                    // HACK: Workaround the fact that we must provided an outline before we are
                    // measured.
                    int height = getHeight();
                    int top = height > 0 ? height - computeVisibleAppBarHeight() : 0;
                    outline.setRect(0, top, getWidth(), height);
                }
            });
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        ButterKnife.bind(this);
        Context context = getContext();
        mStatusBarColorFullscreen = ViewUtils.getColorFromAttrRes(R.attr.colorPrimaryDark, 0,
                context);

        StatusBarColorUtils.set(mStatusBarColorTransparent, (Activity) context);
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        if (MeasureSpec.getMode(heightMeasureSpec) != MeasureSpec.EXACTLY) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(mMaxHeight, MeasureSpec.EXACTLY);
        }
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        int dismissViewHeight = height - computeVisibleAppBarHeight();
        mDismissView.getLayoutParams().height = dismissViewHeight;

        MarginLayoutParams appBarLayoutLayoutParams =
                (MarginLayoutParams) mAppBarLayout.getLayoutParams();
        appBarLayoutLayoutParams.topMargin = dismissViewHeight;
        // So that the layout remains stable.
        appBarLayoutLayoutParams.height = getAppBarMaxHeight();

        int largeAvatarSizeHalf = mLargeAvatarSize / 2;
        int avatarMarginTop = dismissViewHeight - largeAvatarSizeHalf;
        float avatarHorizontalFraction = avatarMarginTop < mSmallAvatarMarginTop ?
                MathUtils.unlerp(mSmallAvatarMarginTop, -largeAvatarSizeHalf, avatarMarginTop)
                : 0;
        avatarMarginTop = Math.max(mSmallAvatarMarginTop, avatarMarginTop);
        int avatarMarginLeft = MathUtils.lerp(width / 2 - largeAvatarSizeHalf,
                mSmallAvatarMarginLeft, avatarHorizontalFraction);
        MarginLayoutParams avatarContainerLayoutParams =
                (MarginLayoutParams) mAvatarContainerLayout.getLayoutParams();
        avatarContainerLayoutParams.leftMargin = avatarMarginLeft;
        avatarContainerLayoutParams.topMargin = avatarMarginTop;
        float avatarScale = MathUtils.lerp(1, (float) mSmallAvatarSize / mLargeAvatarSize,
                avatarHorizontalFraction);
        mAvatarContainerLayout.setPivotX(0);
        mAvatarContainerLayout.setPivotY(0);
        mAvatarContainerLayout.setScaleX(avatarScale);
        mAvatarContainerLayout.setScaleY(avatarScale);

        for (int i = 0, count = mAppBarLayout.getChildCount(); i < count; ++i) {
            View child = mAppBarLayout.getChildAt(i);
            if (child != mToolbar) {
                child.setAlpha(Math.max(0, 1 - getFraction() * 2));
            }
        }
        mToolbarUsernameText.setAlpha(Math.max(0, getFraction() * 2 - 1));

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private int getAppBarMaxHeight() {
        return mMaxHeight / 2;
    }

    private int computeVisibleAppBarHeight() {
        return MathUtils.lerp(getAppBarMaxHeight(), getMinHeight(), getFraction());
    }

    private float getFraction() {
        int scrollExtent = getScrollExtent();
        return scrollExtent > 0 ? (float) mScroll / scrollExtent : 0;
    }

    @Override
    public int getScroll() {
        return mScroll;
    }

    @Override
    public int getScrollExtent() {
        return mMaxHeight - getMinHeight();
    }

    @Override
    public void scrollTo(int scroll) {

        int scrollExtent = getScrollExtent();
        scroll = MathUtils.clamp(scroll, 0, scrollExtent);
        if (mScroll == scroll) {
            return;
        }

        ViewUtils.setHeight(this, mMaxHeight - scroll);
        int oldScroll = mScroll;
        mScroll = scroll;

        if (oldScroll < scrollExtent && mScroll == scrollExtent) {
            StatusBarColorUtils.animateTo(mStatusBarColorFullscreen, (Activity) getContext());
        } else if (oldScroll == scrollExtent && mScroll < oldScroll) {
            StatusBarColorUtils.animateTo(mStatusBarColorTransparent, (Activity) getContext());
        }
    }

    @Override
    public void scrollBy(int delta) {
        scrollTo(mScroll + delta);
    }

    private int getMinHeight() {
        // So that we don't need to wait until measure.
        return mToolbar.getLayoutParams().height;
    }

    // Should be called by ProfileLayout.onMeasure() before its super call.
    public void setMaxHeight(int maxHeight) {

        if (mMaxHeight == maxHeight) {
            return;
        }

        mMaxHeight = maxHeight;
        ViewUtils.setHeight(mAppBarLayout, mMaxHeight);
    }

    public void bindUser(User user) {
        ImageUtils.loadProfileAvatar(mAvatarImage, user.getLargeAvatarOrAvatar(), getContext());
        mToolbarUsernameText.setText(user.name);
        mUsernameText.setText(user.name);
        mSignatureText.setText(null);
        mJoinedAtLocationText.setText(null);
        TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(mFollowButton, 0, 0, 0, 0);
        mFollowButton.setVisibility(GONE);
    }

    public void bindUserInfo(final UserInfo userInfo) {
        Context context = getContext();
        if (!ViewUtils.isVisible(mAvatarImage)) {
            // HACK: Don't load avatar again if already loaded by bindUser().
            ImageUtils.loadProfileAvatar(mAvatarImage, userInfo.getLargeAvatarOrAvatar(), context);
        }
        mToolbarUsernameText.setText(userInfo.name);
        mUsernameText.setText(userInfo.name);
        mSignatureText.setText(userInfo.signature);
        mJoinedAtLocationText.setJoinedAtAndLocation(userInfo.createdAt, userInfo.locationName);
        if (userInfo.isOneself(context)) {
            TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(mFollowButton,
                    R.drawable.edit_icon_white_24dp, 0, 0, 0);
            mFollowButton.setText(R.string.profile_edit);
            mFollowButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        mListener.onEditProfile(userInfo);
                    }
                }
            });
        } else {
            FollowUserManager followUserManager = FollowUserManager.getInstance();
            String userIdOrUid = userInfo.getIdOrUid();
            if (followUserManager.isWriting(userIdOrUid)) {
                TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(mFollowButton,
                        new WhiteIndeterminateProgressIconDrawable(context), null, null, null);
                mFollowButton.setText(followUserManager.isWritingFollow(userIdOrUid) ?
                        R.string.user_following : R.string.user_unfollowing);
            } else {
                int followDrawableId;
                int followStringId;
                if (userInfo.isFollowed) {
                    if (userInfo.isFollower) {
                        followDrawableId = R.drawable.mutual_icon_white_24dp;
                        followStringId = R.string.profile_following_mutual;
                    } else {
                        followDrawableId = R.drawable.ok_icon_white_24dp;
                        followStringId = R.string.profile_following;
                    }
                } else {
                    followDrawableId = R.drawable.add_icon_white_24dp;
                    followStringId = R.string.profile_follow;
                }
                TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(mFollowButton,
                        followDrawableId, 0, 0, 0);
                mFollowButton.setText(followStringId);
            }
            mFollowButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        mListener.onFollowUser(userInfo, !userInfo.isFollowed);
                    }
                }
            });
        }
        mFollowButton.setVisibility(VISIBLE);
    }

    public interface Listener {
        void onEditProfile(UserInfo userInfo);
        void onFollowUser(UserInfo userInfo, boolean follow);
    }
}
