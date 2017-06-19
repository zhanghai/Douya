/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package android.support.v7.app;

public class AppCompatDelegateNightModeHelper {

    private AppCompatDelegateNightModeHelper() {}

    public static int mapNightMode(AppCompatDelegate delegate, int mode) {
        // We don't care about APIs below 14.
        return ((AppCompatDelegateImplV14) delegate).mapNightMode(mode);
    }
}
