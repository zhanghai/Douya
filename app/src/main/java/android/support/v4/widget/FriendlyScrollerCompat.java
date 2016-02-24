/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package android.support.v4.widget;

import android.content.Context;
import android.os.Build;
import android.support.v4.widget.EdgeEffectCompat;
import android.support.v4.widget.ScrollerCompat;
import android.support.v4.widget.ScrollerCompatGingerbread;
import android.support.v4.widget.ScrollerCompatIcs;
import android.view.animation.Interpolator;
import android.widget.Scroller;

public class FriendlyScrollerCompat extends ScrollerCompat {

    private static final String TAG = "FriendlyScrollerCompat";

    FriendlyScrollerCompatImpl mImpl;

    interface FriendlyScrollerCompatImpl {
        int getStartX(Object scroller);
        int getStartY(Object scroller);
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
    }

    public static FriendlyScrollerCompat create(Context context) {
        return create(context, null);
    }

    public static FriendlyScrollerCompat create(Context context, Interpolator interpolator) {
        return new FriendlyScrollerCompat(context, interpolator);
    }

    FriendlyScrollerCompat(Context context, Interpolator interpolator) {
        super(context, interpolator);

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
        return mImpl.getStartX(mScroller);
    }

    /**
     * Returns the start Y offset in the scroll.
     *
     * @return The start Y offset as an absolute distance from the origin.
     */
    public final int getStartY() {
        return mImpl.getStartY(mScroller);
    }
}
