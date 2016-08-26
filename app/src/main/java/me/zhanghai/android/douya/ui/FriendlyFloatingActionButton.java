/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.ViewGroup;

import butterknife.BindInt;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.util.ViewUtils;

public class FriendlyFloatingActionButton extends FloatingActionButton {

    @BindInt(android.R.integer.config_shortAnimTime)
    int mAnimationDuration;

    private boolean mShowing = true;
    private Animator mAnimator;

    public FriendlyFloatingActionButton(Context context) {
        super(context);

        init();
    }

    public FriendlyFloatingActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public FriendlyFloatingActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        ButterKnife.bind(this);
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

        mAnimator = ObjectAnimator.ofFloat(this, TRANSLATION_Y, getTranslationY(),
                getHideTranslationY())
                .setDuration(mAnimationDuration);
        mAnimator.setInterpolator(new FastOutLinearInInterpolator());
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
        return ((ViewGroup) getParent()).getHeight() - getTop();
    }

    public void show() {

        if (mShowing) {
            return;
        }
        mShowing = true;

        cancelAnimator();

        mAnimator = ObjectAnimator.ofFloat(this, TRANSLATION_Y, getTranslationY(), 0)
                .setDuration(mAnimationDuration);
        mAnimator.setInterpolator(new FastOutSlowInInterpolator());
        mAnimator.start();
    }

    public void showImmediately() {

        if (mShowing) {
            return;
        }
        mShowing = true;

        setTranslationY(0);
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
