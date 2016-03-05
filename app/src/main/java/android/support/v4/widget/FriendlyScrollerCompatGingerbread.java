/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package android.support.v4.widget;

import android.widget.OverScroller;

public class FriendlyScrollerCompatGingerbread {

    public static int getStartX(Object scroller) {
        return ((OverScroller) scroller).getStartX();
    }

    public static int getStartY(Object scroller) {
        return ((OverScroller) scroller).getStartY();
    }

    public static void forceFinished(Object scroller, boolean finished) {
        ((OverScroller) scroller).forceFinished(finished);
    }
}
