/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package android.support.v4.widget;

import android.content.Context;
import android.os.Build;
import android.view.animation.Interpolator;
import android.widget.Scroller;

public class FriendlyScrollerCompat {

    ScrollerCompat mScrollerCompat;

    FriendlyScrollerCompatImpl mImpl;

    interface FriendlyScrollerCompatImpl {
        int getStartX(Object scroller);
        int getStartY(Object scroller);
        void forceFinished(Object scroller, boolean finished);
    }

    static class FriendlyScrollerCompatImplBase implements FriendlyScrollerCompatImpl {

        @Override
        public int getStartX(Object scroller) {
            return ((Scroller) scroller).getStartX();
        }

        @Override
        public int getStartY(Object scroller) {
            return ((Scroller) scroller).getStartY();
        }

        @Override
        public void forceFinished(Object scroller, boolean finished) {
            ((Scroller) scroller).forceFinished(finished);
        }
    }

    static class FriendlyScrollerCompatImplGingerbread implements FriendlyScrollerCompatImpl {

        @Override
        public int getStartX(Object scroller) {
            return FriendlyScrollerCompatGingerbread.getStartX(scroller);
        }

        @Override
        public int getStartY(Object scroller) {
            return FriendlyScrollerCompatGingerbread.getStartY(scroller);
        }

        @Override
        public void forceFinished(Object scroller, boolean finished) {
            FriendlyScrollerCompatGingerbread.forceFinished(scroller, finished);
        }
    }

    public static FriendlyScrollerCompat create(Context context) {
        return create(context, null);
    }

    public static FriendlyScrollerCompat create(Context context, Interpolator interpolator) {
        return new FriendlyScrollerCompat(context, interpolator);
    }

    FriendlyScrollerCompat(Context context, Interpolator interpolator) {

        mScrollerCompat = ScrollerCompat.create(context, interpolator);

        if (Build.VERSION.SDK_INT >= 9) { // Gingerbread
            mImpl = new FriendlyScrollerCompatImplGingerbread();
        } else {
            mImpl = new FriendlyScrollerCompatImplBase();
        }
    }

    /**
     * Returns the start X offset in the scroll.
     *
     * @return The start X offset as an absolute distance from the origin.
     */
    public final int getStartX() {
        return mImpl.getStartX(mScrollerCompat.mScroller);
    }

    /**
     * Returns the start Y offset in the scroll.
     *
     * @return The start Y offset as an absolute distance from the origin.
     */
    public final int getStartY() {
        return mImpl.getStartY(mScrollerCompat.mScroller);
    }

    /**
     * Force the finished field to a particular value.
     *
     * @param finished The new finished value.
     */
    public void forceFinished(boolean finished) {
        mImpl.forceFinished(mScrollerCompat.mScroller, finished);
    }

    public boolean isFinished() {
        return mScrollerCompat.isFinished();
    }

    public int getCurrX() {
        return mScrollerCompat.getCurrX();
    }

    public int getCurrY() {
        return mScrollerCompat.getCurrY();
    }

    public int getFinalX() {
        return mScrollerCompat.getFinalX();
    }

    public int getFinalY() {
        return mScrollerCompat.getFinalY();
    }

    public float getCurrVelocity() {
        return mScrollerCompat.getCurrVelocity();
    }

    public boolean computeScrollOffset() {
        return mScrollerCompat.computeScrollOffset();
    }

    public void startScroll(int startX, int startY, int dx, int dy) {
        mScrollerCompat.startScroll(startX, startY, dx, dy);
    }

    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        mScrollerCompat.startScroll(startX, startY, dx, dy, duration);
    }

    public void fling(int startX, int startY, int velocityX, int velocityY, int minX, int maxX, int minY, int maxY) {
        mScrollerCompat.fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY);
    }

    public void fling(int startX, int startY, int velocityX, int velocityY, int minX, int maxX, int minY, int maxY, int overX, int overY) {
        mScrollerCompat.fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY, overX, overY);
    }

    public boolean springBack(int startX, int startY, int minX, int maxX, int minY, int maxY) {
        return mScrollerCompat.springBack(startX, startY, minX, maxX, minY, maxY);
    }

    public void abortAnimation() {
        mScrollerCompat.abortAnimation();
    }

    public void notifyVerticalEdgeReached(int startY, int finalY, int overY) {
        mScrollerCompat.notifyVerticalEdgeReached(startY, finalY, overY);
    }

    public void notifyHorizontalEdgeReached(int startX, int finalX, int overX) {
        mScrollerCompat.notifyHorizontalEdgeReached(startX, finalX, overX);
    }

    public boolean isOverScrolled() {
        return mScrollerCompat.isOverScrolled();
    }
}
