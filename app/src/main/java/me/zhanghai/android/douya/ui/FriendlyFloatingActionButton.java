/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.stateful.ExtendableSavedState;

import androidx.interpolator.view.animation.FastOutLinearInInterpolator;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.ViewGroup;

import butterknife.BindInt;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.util.BundleBuilder;
import me.zhanghai.android.douya.util.ViewUtils;

public class FriendlyFloatingActionButton extends FloatingActionButton {

    private static final String EXTENDABLE_STATE_KEY = FriendlyFloatingActionButton.class.getName();
    private static final String STATE_KEY_SHOWING = "SHOWING";

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
        ExtendableSavedState state = (ExtendableSavedState) super.onSaveInstanceState();
        state.extendableStates.put(EXTENDABLE_STATE_KEY, new BundleBuilder()
                .putBoolean(STATE_KEY_SHOWING, mShowing)
                .build());
        return state;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);

        ExtendableSavedState extendableSavedState = (ExtendableSavedState) state;
        Bundle extendableState = extendableSavedState.extendableStates.get(EXTENDABLE_STATE_KEY);
        boolean showing = extendableState.getBoolean(STATE_KEY_SHOWING);
        if (!showing) {
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
}
