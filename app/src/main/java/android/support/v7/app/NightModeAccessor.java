/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package android.support.v7.app;

import android.content.res.Resources;

public class NightModeAccessor {

    private NightModeAccessor() {}

    public static void flushResources(Resources resources) {
        ResourcesFlusher.flush(resources);
    }

    public static int mapNightMode(AppCompatDelegate delegate, int mode) {
        return ((AppCompatDelegateImpl) delegate).mapNightMode(mode);
    }
}
