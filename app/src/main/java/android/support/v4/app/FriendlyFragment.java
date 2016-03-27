/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package android.support.v4.app;

import me.zhanghai.android.douya.app.TargetedRetainedFragment;

/**
 * Hack used by {@link TargetedRetainedFragment} so that it won't trigger child fragment manager
 * creation for fragment that has already been detached. This avoids creation of useless child
 * fragment manager in the mean time.
 */
public class FriendlyFragment {

    private FriendlyFragment() {}

    public static boolean hasChildFragmentManager(Fragment fragment) {
        return fragment.mChildFragmentManager != null;
    }
}
