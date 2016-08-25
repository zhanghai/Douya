/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.transitionseverywhere.Transition;
import com.transitionseverywhere.TransitionValues;

public class CrossfadeText extends Transition {

    private static final String PROPNAME_TEXT = "android:crossfadeText:text";

    public CrossfadeText() {}

    public CrossfadeText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void captureStartValues(TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    @Override
    public void captureEndValues(TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    private void captureValues(TransitionValues transitionValues) {
        if (!(transitionValues.view instanceof TextView)) {
            throw new IllegalArgumentException("Target should be a TextView instead of "
                    + transitionValues.view.getClass().getName());
        }
        TextView view = (TextView) transitionValues.view;
        transitionValues.values.put(PROPNAME_TEXT, view.getText());
    }

    @Override
    public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues,
                                   TransitionValues endValues) {
        TextView view = (TextView) startValues.view;
        CharSequence startText = (CharSequence) startValues.values.get(PROPNAME_TEXT);
        CharSequence endText = (CharSequence) endValues.values.get(PROPNAME_TEXT);
        view.setText(startText);
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, View.ALPHA, 1, 0, 1);
        AnimatorListener listener = new AnimatorListener(view, endText);
        animator.addListener(listener);
        animator.addUpdateListener(listener);
        return animator;
    }

    private class AnimatorListener extends AnimatorListenerAdapter
            implements ValueAnimator.AnimatorUpdateListener {

        private TextView mTextView;
        private CharSequence mEndText;

        private boolean mTextUpdated;

        public AnimatorListener(TextView textView, CharSequence endText) {
            mTextView = textView;
            mEndText = endText;
        }

        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            if (valueAnimator.getAnimatedFraction() >= 0.5) {
                ensureTextUpdated();
            }
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            ensureTextUpdated();
        }

        private void ensureTextUpdated() {
            if (!mTextUpdated) {
                mTextView.setText(mEndText);
                mTextUpdated = true;
            }
        }
    }
}
