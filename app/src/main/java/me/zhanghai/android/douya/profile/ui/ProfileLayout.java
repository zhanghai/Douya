/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.profile.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowInsets;

import butterknife.BindColor;
import butterknife.BindInt;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.profile.util.ProfileUtils;
import me.zhanghai.android.douya.ui.FlexibleSpaceLayout;
import me.zhanghai.android.douya.ui.IntProperty;
import me.zhanghai.android.douya.util.AppUtils;
import me.zhanghai.android.douya.util.ColorUtils;
import me.zhanghai.android.douya.util.StatusBarColorUtils;
import me.zhanghai.android.douya.util.ViewUtils;

public class ProfileLayout extends FlexibleSpaceLayout {

    @BindInt(android.R.integer.config_shortAnimTime)
    int mShortAnimationTime;
    @BindColor(R.color.system_window_scrim)
    int mStatusBarColor;
    @BindColor(R.color.dark_70_percent)
    int mBackgroundColor;

    private ViewGroup mOffsetContainer;
    private ProfileHeaderLayout mProfileHeaderLayout;

    private boolean mUseWideLayout;
    private boolean mExiting;

    private ColorDrawable mWindowBackground;

    private Listener mListener;

    public ProfileLayout(Context context) {
        super(context);

        init();
    }

    public ProfileLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public ProfileLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    public ProfileLayout(Context context, AttributeSet attrs, int defStyleAttr,
                         int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init();
    }

    private void init() {

        ButterKnife.bind(this);

        Context context = getContext();
        mUseWideLayout = ProfileUtils.shouldUseWideLayout(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setSystemUiVisibility(SYSTEM_UI_FLAG_LAYOUT_STABLE | SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        mWindowBackground = new ColorDrawable(mBackgroundColor);
        AppUtils.getActivityFromContext(context).getWindow()
                .setBackgroundDrawable(mWindowBackground);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        // HACK: Coupled with specific XML hierarchy.
        mOffsetContainer = (ViewGroup) getChildAt(0);
        mProfileHeaderLayout = (ProfileHeaderLayout) mOffsetContainer.getChildAt(0);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
    @Override
    public WindowInsets dispatchApplyWindowInsets(WindowInsets insets) {
        setPadding(insets.getSystemWindowInsetLeft(), 0, insets.getSystemWindowInsetRight(), 0);
        mProfileHeaderLayout.setInsetTop(insets.getSystemWindowInsetTop());
        return insets.consumeSystemWindowInsets();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int height = MeasureSpec.getSize(heightMeasureSpec);
        int headerMaxHeight = mUseWideLayout ? height : height * 2 / 3;
        mProfileHeaderLayout.setMaxHeight(headerMaxHeight);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public int getOffset() {
        // View.offsetTopAndBottom causes transient invalid layout position when animating in.
        return (int) mOffsetContainer.getTranslationY();
    }

    public void offsetTo(int offset) {

        if (offset < 0) {
            offset = 0;
        }
        if (getOffset() == offset) {
            return;
        }

        mOffsetContainer.setTranslationY(offset);
        updateStatusBarAndWindowBackground(offset);
    }

    public void offsetBy(int delta) {
        offsetTo(getOffset() + delta);
    }

    private void updateStatusBarAndWindowBackground(int offset) {
        float fraction = Math.max(0, 1 - (float) offset / getHeight());
        int alpha = (int) (fraction * 0xFF);
        int statusBarColor = ColorUtils.blendAlphaComponent(mStatusBarColor, alpha);
        StatusBarColorUtils.set(statusBarColor, AppUtils.getActivityFromContext(getContext()));
        mWindowBackground.setAlpha(alpha);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (mExiting) {
            return false;
        }
        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mExiting) {
            return false;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        if (mExiting) {
            return false;
        }
        return super.onGenericMotionEvent(event);
    }

    @Override
    protected void onDrag(MotionEvent event, float delta) {
        if (delta > 0) {
            int oldScroll = getScroll();
            scrollBy((int) -delta);
            delta += getScroll() - oldScroll;
            offsetBy((int) delta);
        } else {
            int oldOffset = getOffset();
            offsetBy((int) delta);
            delta -= getOffset() - oldOffset;
            int oldScroll = getScroll();
            scrollBy((int) -delta);
            delta += getScroll() - oldScroll;
            if (delta < 0) {
                pullEdgeEffectBottom(event, delta);
            }
        }
    }

    @Override
    protected void onDragEnd(boolean cancelled) {
        if (getOffset() > 0) {
            exit();
        } else {
            super.onDragEnd(cancelled);
        }
    }

    @Override
    protected boolean shouldDrawEdgeEffectBottom() {
        return !mUseWideLayout;
    }

    public Listener getListener() {
        return mListener;
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public void enter() {
        ViewUtils.postOnPreDraw(this, new Runnable() {
            @Override
            public void run() {
                animateEnter();
            }
        });
    }

    private void animateEnter() {
        ObjectAnimator animator = ObjectAnimator.ofInt(this, OFFSET, getHeight(), 0);
        animator.setDuration(mShortAnimationTime);
        animator.setInterpolator(new LinearOutSlowInInterpolator());
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (mListener != null) {
                    mListener.onEnterAnimationEnd();
                }
            }
        });
        animator.start();
    }

    public void exit() {

        if (mExiting) {
            return;
        }

        mExiting = true;
        abortScrollerAnimation();
        recycleVelocityTrackerIfHas();

        animateExit();
    }

    private void animateExit() {

        int offset = getOffset();
        int height = getHeight();
        if (offset >= height) {
            mListener.onExitAnimationEnd();
            return;
        }

        ObjectAnimator animator = ObjectAnimator.ofInt(this, OFFSET, offset, height);
        animator.setDuration(mShortAnimationTime);
        animator.setInterpolator(new FastOutLinearInInterpolator());
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (mListener != null) {
                    mListener.onExitAnimationEnd();
                }
            }
        });
        animator.start();
    }

    public static final IntProperty<ProfileLayout> OFFSET =
            new IntProperty<ProfileLayout>("offset") {

                @Override
                public Integer get(ProfileLayout object) {
                    return object.getOffset();
                }

                @Override
                public void setValue(ProfileLayout object, int value) {
                    object.offsetTo(value);
                }
            };

    public interface Listener {
        void onEnterAnimationEnd();
        void onExitAnimationEnd();
    }
}
