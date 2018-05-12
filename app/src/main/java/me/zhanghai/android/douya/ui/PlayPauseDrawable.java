/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.view.animation.FastOutSlowInInterpolator;

import me.zhanghai.android.douya.util.MathUtils;
import me.zhanghai.android.douya.util.ViewUtils;

public class PlayPauseDrawable extends BasePaintDrawable {

    public enum State {

        Play(new int[] {
                8, 5, 8, 12, 19, 12, 19, 12,
                8, 12, 8, 19, 19, 12, 19, 12
        }, new int[] {
                12, 5, 5, 16, 12, 16, 12, 5,
                12, 5, 12, 16, 19, 16, 12, 5
        }),
        Pause(new int[] {
                6, 5, 6, 19, 10, 19, 10, 5,
                14, 5, 14, 19, 18, 19, 18, 5
        }, new int[] {
                5, 6, 5, 10, 19, 10, 19, 6,
                5, 14, 5, 18, 19, 18, 19, 14
        });

        private int[] mStartPoints;
        private int[] mEndPoints;

        State(int[] startPoints, int[] endPoints) {
            mStartPoints = startPoints;
            mEndPoints = endPoints;
        }

        public int[] getStartPoints() {
            return mStartPoints;
        }

        public int[] getEndPoints() {
            return mEndPoints;
        }
    }

    private static final int INTRINSIC_SIZE_DP = 24;
    private final int mIntrinsicSize;

    private static final FloatProperty<PlayPauseDrawable> FRACTION =
            new FloatProperty<PlayPauseDrawable>("fraction") {
                @Override
                public void setValue(PlayPauseDrawable object, float value) {
                    object.mFraction = value;
                }
                @Override
                public Float get(PlayPauseDrawable object) {
                    return object.mFraction;
                }
            };

    private State mPreviousState;
    private State mCurrentState = State.Play;
    private float mFraction = 1;
    private State mNextState;

    private Animator mAnimator;

    private Path mPath = new Path();

    public PlayPauseDrawable(Context context) {
        mIntrinsicSize = ViewUtils.dpToPxSize(INTRINSIC_SIZE_DP, context);
        mAnimator = ObjectAnimator.ofFloat(this, FRACTION, 0, 1)
                .setDuration(ViewUtils.getShortAnimTime(context));
        mAnimator.setInterpolator(new FastOutSlowInInterpolator());
    }

    @Override
    public int getIntrinsicWidth() {
        return mIntrinsicSize;
    }

    @Override
    public int getIntrinsicHeight() {
        return mIntrinsicSize;
    }

    public State getCurrentState() {
        return mCurrentState;
    }

    public void jumpToState(State state) {
        stop();
        mPreviousState = null;
        mCurrentState = state;
        mFraction = 1;
        mNextState = null;
        invalidateSelf();
    }

    public void setNextState(State state) {
        if (mCurrentState == state) {
            mNextState = null;
            return;
        }
        if (!isVisible()) {
            jumpToState(state);
            return;
        }
        mNextState = state;
        tryMoveToNextState();
    }

    private void tryMoveToNextState() {
        if (isRunning()) {
            return;
        }
        mPreviousState = mCurrentState;
        mCurrentState = mNextState;
        mFraction = 0;
        mNextState = null;
        start();
    }

    @Override
    public boolean setVisible(boolean visible, boolean restart) {
        if ((isVisible() != visible) || restart) {
            stop();
            if (mNextState != null) {
                jumpToState(mNextState);
            }
            invalidateSelf();
        }
        return super.setVisible(visible, restart);
    }

    private void start() {
        if (mAnimator.isStarted()) {
            return;
        }
        mAnimator.start();
        invalidateSelf();
    }

    private void stop() {
        if (!mAnimator.isStarted()) {
            return;
        }
        mAnimator.end();
    }

    private boolean isRunning() {
        return mAnimator.isRunning();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        if (isRunning()) {
            invalidateSelf();
        }
    }

    @Override
    protected void onPreparePaint(Paint paint) {
        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas, int width, int height, Paint paint) {
        canvas.scale((float) width / 24, (float) height / 24);
        if (mFraction == 0) {
            drawState(canvas, paint, mPreviousState);
        } else if (mFraction == 1) {
            drawState(canvas, paint, mCurrentState);
        } else {
            drawBetweenStates(canvas, paint, mPreviousState, mCurrentState, mFraction);
        }
    }

    private void drawState(Canvas canvas, Paint paint, State state) {
        int[] points = state.getStartPoints();
        mPath.rewind();
        for (int i = 0, count = points.length, subCount = count / 2; i < count; i += 2) {
            float x = points[i];
            float y = points[i + 1];
            if (i % subCount == 0) {
                if (i > 0) {
                    mPath.close();
                }
                mPath.moveTo(x, y);
            } else {
                mPath.lineTo(x, y);
            }
        }
        mPath.close();
        canvas.drawPath(mPath, paint);
    }

    private void drawBetweenStates(Canvas canvas, Paint paint, State fromState, State toState,
                                   float fraction) {
        canvas.rotate(MathUtils.lerp(0, 90, fraction), 12, 12);
        int[] startPoints = fromState.getStartPoints();
        int[] endPoints = toState.getEndPoints();
        mPath.rewind();
        for (int i = 0, count = startPoints.length, subCount = count / 2; i < count; i += 2) {
            int startX = startPoints[i];
            int startY = startPoints[i + 1];
            int endX = endPoints[i];
            int endY = endPoints[i + 1];
            float x = MathUtils.lerp(startX, endX, fraction);
            float y = MathUtils.lerp(startY, endY, fraction);
            if (i % subCount == 0) {
                if (i > 0) {
                    mPath.close();
                }
                mPath.moveTo(x, y);
            } else {
                mPath.lineTo(x, y);
            }
        }
        mPath.close();
        canvas.drawPath(mPath, paint);
    }
}
