/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import butterknife.BindInt;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.util.ViewUtils;

/**
 * A {@link LinearLayout} that manages showing and hiding the AppBar and its shadow with support.
 */
public class AppBarWrapperLayout extends LinearLayout {

    @BindInt(android.R.integer.config_shortAnimTime)
    int mAnimationDuration;

    private View mAppbarView;
    private View mShadowCompatView;

    private boolean mShowing = true;
    private AnimatorSet mAnimator;

    public AppBarWrapperLayout(Context context) {
        super(context);

        init();
    }

    public AppBarWrapperLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public AppBarWrapperLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AppBarWrapperLayout(Context context, AttributeSet attrs, int defStyleAttr,
                               int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init();
    }

    private void init() {

        setOrientation(VERTICAL);

        ButterKnife.bind(this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        ViewUtils.inflateInto(R.layout.appbar_shadow_compat, this);

        if (getChildCount() != 2) {
            throw new IllegalStateException("One and only one AppBar view should be wrapped " +
                    "inside this layout");
        }
        mAppbarView = getChildAt(0);
        mShadowCompatView = getChildAt(1);
    }

    @Override
    protected Parcelable onSaveInstanceState() {

        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.showing = mShowing;

        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {

        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());

        if (!savedState.showing) {
            hideImmediately();
        }
    }

    public void hide() {

        if (!mShowing) {
            return;
        }
        mShowing = false;

        cancelAnimator();

        mAnimator = new AnimatorSet()
                .setDuration(mAnimationDuration);
        mAnimator.setInterpolator(new FastOutLinearInInterpolator());
        AnimatorSet.Builder builder = mAnimator.play(ObjectAnimator.ofFloat(this, TRANSLATION_Y,
                getTranslationY(), getHideTranslationY()));
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            builder.before(ObjectAnimator.ofFloat(mShadowCompatView, ALPHA,
                    mShadowCompatView.getAlpha(), 0));
        } else {
            builder.before(ObjectAnimator.ofFloat(mAppbarView, TRANSLATION_Z,
                    mAppbarView.getTranslationZ(), -mAppbarView.getElevation()));
        }

        mAnimator.start();
    }

    public void hideImmediately() {

        if (!mShowing) {
            return;
        }

        float hideTranslationY = getHideTranslationY();
        if (hideTranslationY != 0) {
            mShowing = false;
            setTranslationY(hideTranslationY);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                mShadowCompatView.setAlpha(0);
            } else {
                mAppbarView.setTranslationZ(-mAppbarView.getElevation());
            }
        } else {
            ViewUtils.postOnPreDraw(this, new Runnable() {
                @Override
                public void run() {
                    hideImmediately();
                }
            });
        }
    }

    private int getHideTranslationY() {
        return -(getBottom() - mShadowCompatView.getHeight());
    }

    public void show() {

        if (mShowing) {
            return;
        }
        mShowing = true;

        cancelAnimator();

        mAnimator = new AnimatorSet()
                .setDuration(mAnimationDuration);
        mAnimator.setInterpolator(new FastOutSlowInInterpolator());
        AnimatorSet.Builder builder = mAnimator.play(ObjectAnimator.ofFloat(this, TRANSLATION_Y,
                getTranslationY(), 0));
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            builder.with(ObjectAnimator.ofFloat(mShadowCompatView, ALPHA,
                    mShadowCompatView.getAlpha(), 1));
        } else {
            builder.with(ObjectAnimator.ofFloat(mAppbarView, TRANSLATION_Z,
                    mAppbarView.getTranslationZ(), 0));
        }
        mAnimator.start();
    }

    public void showImmediately() {

        if (mShowing) {
            return;
        }
        mShowing = true;

        setTranslationY(0);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            mShadowCompatView.setAlpha(1);
        } else {
            mAppbarView.setTranslationZ(0);
        }
    }

    private void cancelAnimator() {
        if (mAnimator != null) {
            mAnimator.cancel();
            mAnimator = null;
        }
    }

    private static class SavedState extends BaseSavedState {

        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    @Override
                    public SavedState createFromParcel(Parcel source) {
                        return new SavedState(source);
                    }

                    @Override
                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };

        public boolean showing;

        public SavedState(Parcel in) {
            super(in);

            showing = in.readByte() != 0;
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);

            dest.writeByte(showing ? (byte) 1 : (byte) 0);
        }
    }
}
