/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import butterknife.BindColor;
import butterknife.BindInt;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.util.ViewUtils;

public class ProfileLayout extends FlexibleSpaceLayout {

    @BindInt(android.R.integer.config_shortAnimTime)
    int mShortAnimationTime;
    @BindColor(R.color.dark_70_percent)
    int mBackgroundColor;

    private ColorDrawable mBackgroundDrawable;

    private View mChild;

    private Listener mListener;

    private boolean mExiting;

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setSystemUiVisibility(SYSTEM_UI_FLAG_LAYOUT_STABLE | SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            setFitsSystemWindows(true);
        }

        mBackgroundDrawable = new ColorDrawable(mBackgroundColor);
        me.zhanghai.android.douya.util.ViewCompat.setBackground(this, mBackgroundDrawable);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        if (getChildCount() != 1) {
            throw new IllegalStateException("Must have one child.");
        }
        mChild = getChildAt(0);
    }

    public int getOffset() {
        return mChild.getTop() - getPaddingTop();
    }

    public void offsetTo(int offset) {

        int oldOffset = getOffset();
        if (oldOffset == offset || offset < 0) {
            return;
        }

        ViewCompat.offsetTopAndBottom(mChild, offset - oldOffset);
        updateBackground(offset);
    }

    public void offsetBy(int delta) {
        offsetTo(getOffset() + delta);
    }

    private void updateBackground(int offset) {
        float fraction = Math.max(0, 1 - (float) offset
                / (getHeight() - getPaddingTop() - getPaddingBottom()));
        mBackgroundDrawable.setAlpha((int) (fraction * 0xFF));
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

        mExiting = true;
        abortScrollerAnimation();
        recycleVelocityTrackerIfHas();

        animateExit();
    }

    private void animateExit() {
        ObjectAnimator animator = ObjectAnimator.ofInt(this, OFFSET, getOffset(), getHeight());
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
