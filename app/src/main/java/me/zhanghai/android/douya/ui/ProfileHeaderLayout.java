/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Outline;
import android.os.Build;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import butterknife.Bind;
import butterknife.BindDimen;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.util.MathUtils;
import me.zhanghai.android.douya.util.ViewUtils;

/**
 * Set the initial layout_height to match_parent or wrap_content instead a specific value so that
 * the view measures itself correctly for the first time.
 */
public class ProfileHeaderLayout extends RelativeLayout implements FlexibleSpaceHeaderView {

    @BindDimen(R.dimen.profile_large_avatar_size)
    int mLargeAvatarSize;
    @BindDimen(R.dimen.profile_small_avatar_size)
    int mSmallAvatarSize;
    @BindDimen(R.dimen.profile_small_avatar_margin_left)
    int mSmallAvatarMarginLeft;
    @BindDimen(R.dimen.profile_small_avatar_margin_top)
    int mSmallAvatarMarginTop;

    @Bind(R.id.appBar)
    LinearLayout mAppBarLayout;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.avatar_container)
    FrameLayout mAvatarContainerLayout;
    @Bind(R.id.avatar)
    CircleImageView mAvatarImage;

    private Listener mListener;

    private int mScroll;

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
                    int top = height > 0 ? height - computeAppBarHeight() : 0;
                    outline.setRect(0, top, getWidth(), height);
                }
            });
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        ButterKnife.bind(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        if (MeasureSpec.getMode(heightMeasureSpec) != MeasureSpec.EXACTLY) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(getMaxHeight(), MeasureSpec.EXACTLY);
        }

        int appBarHeight = computeAppBarHeight();
        mAppBarLayout.getLayoutParams().height = appBarHeight;

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int avatarContainerSizeHalf = mAvatarContainerLayout.getLayoutParams().width / 2;
        int avatarMarginTop = height - appBarHeight - avatarContainerSizeHalf;
        float avatarHorizontalFraction = avatarMarginTop < mSmallAvatarMarginTop ?
                MathUtils.unlerp(mSmallAvatarMarginTop, -avatarContainerSizeHalf, avatarMarginTop)
                : 0;
        avatarMarginTop = Math.max(mSmallAvatarMarginTop, avatarMarginTop);
        int avatarMarginLeft = MathUtils.lerp(width / 2 - avatarContainerSizeHalf,
                mSmallAvatarMarginLeft, avatarHorizontalFraction);
        ViewUtils.setMargin(mAvatarContainerLayout, avatarMarginLeft, avatarMarginTop, 0, 0);
        float avatarScale = MathUtils.lerp(1, (float) mSmallAvatarSize / mLargeAvatarSize,
                avatarHorizontalFraction);
        mAvatarImage.setPivotX(0);
        mAvatarImage.setPivotY(0);
        mAvatarImage.setScaleX(avatarScale);
        mAvatarImage.setScaleY(avatarScale);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private float getFraction() {
        int scrollExtent = getScrollExtent();
        return scrollExtent > 0 ? (float) mScroll / scrollExtent : 0;
    }

    private int computeAppBarHeight() {
        return MathUtils.lerp(getMaxHeight() / 2, getMinHeight(), getFraction());
    }

    public Listener getListener() {
        return mListener;
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    @Override
    public int getScroll() {
        return mScroll;
    }

    @Override
    public int getScrollExtent() {
        return getMaxHeight() - getMinHeight();
    }

    @Override
    public void scrollTo(int scroll) {

        int scrollExtent = getScrollExtent();
        scroll = MathUtils.clamp(scroll, 0, scrollExtent);
        if (mScroll == scroll) {
            return;
        }

        ViewUtils.setHeight(this, getMaxHeight() - scroll);
        int oldScroll = mScroll;
        mScroll = scroll;

        if (mListener != null) {
            if (oldScroll < scrollExtent && mScroll == scrollExtent) {
                mListener.onHeaderReachedTop();
            } else if (oldScroll == scrollExtent && mScroll < oldScroll) {
                mListener.onHeaderLeftTop();
            }
        }
    }

    @Override
    public void scrollBy(int delta) {
        scrollTo(mScroll + delta);
    }

    private int getMinHeight() {
        return mToolbar.getHeight();
    }

    private int getMaxHeight() {
        ViewParent viewParent = getParent();
        if (viewParent instanceof View) {
            return ((View) viewParent).getHeight() * 2 / 3;
        }
        return 0;
    }

    public interface Listener {
        void onHeaderReachedTop();
        void onHeaderLeftTop();
    }
}
