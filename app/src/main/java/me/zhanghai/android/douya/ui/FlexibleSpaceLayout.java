/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.support.v4.view.InputDeviceCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.widget.EdgeEffectCompat;
import android.support.v4.widget.FriendlyScrollerCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;

import butterknife.BindInt;
import butterknife.ButterKnife;

public class FlexibleSpaceLayout extends LinearLayout {

    private static final int INVALID_POINTER_ID = -1;

    @BindInt(android.R.integer.config_mediumAnimTime)
    int mMediumAnimationTime;

    private int mTouchSlop;
    private int mMinimumFlingVelocity;
    private int mMaximumFlingVelocity;

    private FlexibleSpaceHeaderView mHeaderView;
    private FlexibleSpaceContentView mContentView;

    private int mScroll;
    private boolean mHeaderCollapsed;

    private boolean mIsBeingDragged;
    private int mActivePointerId;
    private float mLastMotionY;
    private VelocityTracker mVelocityTracker;

    private FriendlyScrollerCompat mScroller;
    private EdgeEffectCompat mEdgeEffectBottom;

    private float mView_verticalScrollFactor = Float.MIN_VALUE;

    public FlexibleSpaceLayout(Context context) {
        super(context);

        init(getContext(), null, 0, 0);
    }

    public FlexibleSpaceLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(getContext(), attrs, 0, 0);
    }

    public FlexibleSpaceLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(getContext(), attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FlexibleSpaceLayout(Context context, AttributeSet attrs, int defStyleAttr,
                               int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init(getContext(), attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {

        ButterKnife.bind(this);

        setFocusable(true);
        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
        setOrientation(VERTICAL);
        setWillNotDraw(false);

        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        mTouchSlop = viewConfiguration.getScaledTouchSlop();
        mMinimumFlingVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
        mMaximumFlingVelocity = viewConfiguration.getScaledMaximumFlingVelocity();

        mScroller = FriendlyScrollerCompat.create(context);
        mEdgeEffectBottom = new EdgeEffectCompat(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mHeaderView = findHeaderView(this);
        if (mHeaderView == null) {
            throw new IllegalStateException("Cannot find a FlexibleSpaceHeaderView");
        }
        mContentView = findContentView(this);
        if (mContentView == null) {
            throw new IllegalStateException("Cannot find a FlexibleSpaceContentView");
        }
    }

    private FlexibleSpaceHeaderView findHeaderView(View view) {
        if (view instanceof FlexibleSpaceHeaderView) {
            return (FlexibleSpaceHeaderView) view;
        } else if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0, count = viewGroup.getChildCount(); i < count; ++i) {
                FlexibleSpaceHeaderView headerView = findHeaderView(viewGroup.getChildAt(i));
                if (headerView != null) {
                    return headerView;
                }
            }
        }
        return null;
    }

    private FlexibleSpaceContentView findContentView(View view) {
        if (view instanceof FlexibleSpaceContentView) {
            return (FlexibleSpaceContentView) view;
        } else if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0, count = viewGroup.getChildCount(); i < count; ++i) {
                FlexibleSpaceContentView contentView = findContentView(viewGroup.getChildAt(i));
                if (contentView != null) {
                    return contentView;
                }
            }
        }
        return null;
    }

    public int getScroll() {
        return mScroll;
    }

    public void scrollTo(int scroll) {

        if (mScroll == scroll) {
            return;
        }

        mHeaderView.scrollTo(scroll);
        scroll = Math.max(0, scroll - mHeaderView.getScroll());
        mContentView.scrollTo(scroll);

        int headerScroll = mHeaderView.getScroll();
        mScroll = headerScroll + mContentView.getScroll();
        if (headerScroll == 0) {
            mHeaderCollapsed = false;
        } else if (headerScroll == mHeaderView.getScrollExtent()) {
            mHeaderCollapsed = true;
        }
    }

    public void scrollBy(int delta) {
        scrollTo(mScroll + delta);
    }

    private void fling(float velocity) {
        // From AOSP MultiShrinkScroller
        // TODO: Is this true?
        // For reasons I do not understand, scrolling is less janky when maxY=Integer.MAX_VALUE
        // then when maxY is set to an actual value.
        mScroller.fling(0, mScroll, 0, (int) velocity, 0, 0, -Integer.MAX_VALUE, Integer.MAX_VALUE);
        ViewCompat.postInvalidateOnAnimation(this);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {

        switch (MotionEventCompat.getActionMasked(event)) {

            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    return true;
                    // updateActivePointerId(event) and clearVelocityTrackerIfHas() should be called
                    // in onTouchEvent().
                } else {
                    updateActivePointerId(event);
                    clearVelocityTrackerIfHas();
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (mIsBeingDragged) {
                    return true;
                } else if (Math.abs(getMotionEventY(event) - mLastMotionY) > mTouchSlop) {
                    return true;
                }
                break;

            case MotionEventCompat.ACTION_POINTER_DOWN:
                onPointerDown(event);
                break;

            case MotionEventCompat.ACTION_POINTER_UP:
                onPointerUp(event);
                break;
        }

        // updateLastMotion() is called here if the touch event is not to be intercepted, so
        // otherwise it should always be called in onTouchEvent().
        updateLastMotion(event);

        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (MotionEventCompat.getActionMasked(event)) {

            case MotionEvent.ACTION_DOWN:
                updateActivePointerId(event);
                clearVelocityTrackerIfHas();
                if (!mIsBeingDragged) {
                    startDrag();
                } else {
                    restartDrag();
                }
                break;

            case MotionEvent.ACTION_MOVE: {
                float deltaY = getMotionEventY(event) - mLastMotionY;
                if (deltaY == 0) {
                    break;
                }
                if (!mIsBeingDragged && Math.abs(deltaY) > mTouchSlop) {
                    startDrag();
                    if (deltaY > 0) {
                        deltaY -= mTouchSlop;
                    } else {
                        deltaY += mTouchSlop;
                    }
                }
                if (mIsBeingDragged) {
                    onDrag(event, deltaY);
                }
                break;
            }

            case MotionEvent.ACTION_UP:
                endDrag(false);
                break;

            case MotionEvent.ACTION_CANCEL:
                endDrag(true);
                break;

            case MotionEventCompat.ACTION_POINTER_DOWN:
                onPointerDown(event);
                break;

            case MotionEventCompat.ACTION_POINTER_UP:
                onPointerUp(event);
                break;
        }

        updateLastMotion(event);

        return true;
    }

    private void onPointerDown(MotionEvent event) {
        int pointerIndex = MotionEventCompat.getActionIndex(event);
        mActivePointerId = MotionEventCompat.getPointerId(event, pointerIndex);
        mLastMotionY = MotionEventCompat.getY(event, pointerIndex);
    }

    private void onPointerUp(MotionEvent event) {
        int pointerIndex = MotionEventCompat.getActionIndex(event);
        int pointerId = MotionEventCompat.getPointerId(event, pointerIndex);
        if (pointerId == mActivePointerId) {
            int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mActivePointerId = MotionEventCompat.getPointerId(event, newPointerIndex);
            mLastMotionY = MotionEventCompat.getY(event, newPointerIndex);
        }
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {

        if (MotionEventCompat_isFromSource(event, InputDeviceCompat.SOURCE_CLASS_POINTER)) {
            if (event.getActionMasked() == MotionEvent.ACTION_SCROLL) {
                if (!mIsBeingDragged) {
                    float vscroll = event.getAxisValue(MotionEvent.AXIS_VSCROLL);
                    if (vscroll != 0) {
                        float deltaY = vscroll * View_getScrollFactor();
                        int oldScrollY = getScrollY();
                        scrollBy(0, (int) -deltaY);
                        return getScrollY() != oldScrollY;
                    }
                }
            }
        }

        return super.onGenericMotionEvent(event);
    }

    private void startDrag() {
        abortScrollerAnimation();
        requestParentDisallowInterceptTouchEventIfHas(true);
        mIsBeingDragged = true;
    }

    private void restartDrag() {
        abortScrollerAnimation();
    }

    protected void onDrag(MotionEvent event, float delta) {
        int oldScroll = mScroll;
        scrollBy((int) -delta);
        delta += mScroll - oldScroll;
        if (delta < 0) {
            pullEdgeEffectBottom(event, delta);
        }
    }

    protected void pullEdgeEffectBottom(MotionEvent event, float delta) {
        mEdgeEffectBottom.onPull(-delta / getHeight(),
                1f - getMotionEventX(event) / getWidth());
        if (!mEdgeEffectBottom.isFinished()) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private void endDrag(boolean cancelled) {

        if (!mIsBeingDragged) {
            return;
        }
        mIsBeingDragged = false;

        mEdgeEffectBottom.onRelease();

        onDragEnd(cancelled);

        mActivePointerId = INVALID_POINTER_ID;
        recycleVelocityTrackerIfHas();
    }

    protected void onDragEnd(boolean cancelled) {

        boolean startedFling = false;
        if (!cancelled) {
            float velocity = getCurrentVelocity();
            if (Math.abs(velocity) > mMinimumFlingVelocity) {
                fling(-velocity);
                startedFling = true;
            }
        }

        if (!startedFling && mScroll > 0 && mScroll < mHeaderView.getScrollExtent()) {
            snapHeaderView();
        }
    }

    @Override
    public void computeScroll() {

        if (mScroller.computeScrollOffset()) {

            int oldScroll = mScroll;
            int scrollerCurrY = mScroller.getCurrY();
            scrollTo(scrollerCurrY);

            int headerScrollExtent = mHeaderView.getScrollExtent();
            int scrollerFinalY = mScroller.getFinalY();
            if (mScroll > 0 && mScroll < headerScrollExtent
                    && scrollerFinalY > 0 && scrollerFinalY < headerScrollExtent) {

                forceScrollerFinished();
                snapHeaderView();

            } else {

                if (mScroll > oldScroll && scrollerCurrY > mScroll) {
                    // We did scroll down for some y and the target y is beyond our range.
                    mEdgeEffectBottom.onAbsorb((int) mScroller.getCurrVelocity());
                }
                if (scrollerCurrY < 0 || scrollerCurrY > mScroll) {
                    abortScrollerAnimation();
                }
                ViewCompat.postInvalidateOnAnimation(this);
            }
        }
    }

    private void snapHeaderView() {
        ObjectAnimator animator = ObjectAnimator.ofInt(this, SCROLL, mScroll,
                mHeaderCollapsed ? 0 : mHeaderView.getScrollExtent());
        animator.setDuration(mMediumAnimationTime);
        animator.setInterpolator(new FastOutSlowInInterpolator());
        animator.start();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        if (shouldDrawEdgeEffectBottom() && !mEdgeEffectBottom.isFinished()) {
            int count = canvas.save();
            int width = getWidth();
            int height = getHeight();
            canvas.translate(-width, height);
            canvas.rotate(180, width, 0);
            mEdgeEffectBottom.setSize(width, height);
            if (mEdgeEffectBottom.draw(canvas)) {
                ViewCompat.postInvalidateOnAnimation(this);
            }
            canvas.restoreToCount(count);
        }
    }

    protected boolean shouldDrawEdgeEffectBottom() {
        return true;
    }

    private void updateActivePointerId(MotionEvent event) {
        // ACTION_DOWN always refers to pointer index 0.
        mActivePointerId = MotionEventCompat.getPointerId(event, 0);
    }

    private void updateLastMotion(MotionEvent event) {
        mLastMotionY = getMotionEventY(event);
        ensureVelocityTracker().addMovement(event);
    }

    private float getMotionEventX(MotionEvent event) {
        if (mActivePointerId != INVALID_POINTER_ID) {
            int pointerIndex = MotionEventCompat.findPointerIndex(event,
                    mActivePointerId);
            if (pointerIndex != -1) {
                return MotionEventCompat.getX(event, pointerIndex);
            } else {
                // Error!
            }
        }
        return event.getX();
    }

    private float getMotionEventY(MotionEvent event) {
        if (mActivePointerId != INVALID_POINTER_ID) {
            int pointerIndex = MotionEventCompat.findPointerIndex(event,
                    mActivePointerId);
            if (pointerIndex != -1) {
                return MotionEventCompat.getY(event, pointerIndex);
            } else {
                // Error!
            }
        }
        return event.getY();
    }

    private VelocityTracker ensureVelocityTracker() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        return mVelocityTracker;
    }

    private void clearVelocityTrackerIfHas() {
        if (mVelocityTracker != null) {
            mVelocityTracker.clear();
        }
    }

    protected void recycleVelocityTrackerIfHas() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    private float getCurrentVelocity() {
        if (mVelocityTracker == null) {
            return 0;
        }
        mVelocityTracker.computeCurrentVelocity(1000, mMaximumFlingVelocity);
        return mVelocityTracker.getYVelocity(mActivePointerId);
    }

    protected void abortScrollerAnimation() {
        mScroller.abortAnimation();
    }

    private void forceScrollerFinished() {
        mScroller.forceFinished(true);
    }

    private void requestParentDisallowInterceptTouchEventIfHas(boolean disallowIntercept) {
        ViewParent viewParent = getParent();
        if (viewParent != null) {
            viewParent.requestDisallowInterceptTouchEvent(disallowIntercept);
        }
    }

    public static final IntProperty<FlexibleSpaceLayout> SCROLL =
            new IntProperty<FlexibleSpaceLayout>("scroll") {

                @Override
                public Integer get(FlexibleSpaceLayout object) {
                    return object.getScroll();
                }

                @Override
                public void setValue(FlexibleSpaceLayout object, int value) {
                    object.scrollTo(value);
                }
            };

    private float View_getScrollFactor() {
        if (mView_verticalScrollFactor == Float.MIN_VALUE) {
            Context context = getContext();
            TypedValue outValue = new TypedValue();
            if (context.getTheme().resolveAttribute(android.R.attr.listPreferredItemHeight,
                    outValue, true)) {
                mView_verticalScrollFactor = outValue.getDimension(
                        context.getResources().getDisplayMetrics());
            } else {
                throw new IllegalStateException(
                        "Expected theme to define listPreferredItemHeight.");
            }
        }
        return mView_verticalScrollFactor;
    }

    private boolean MotionEventCompat_isFromSource(MotionEvent event, int source) {
        return (MotionEventCompat.getSource(event) & source) == source;
    }
}
