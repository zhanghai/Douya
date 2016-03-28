/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package android.support.v4.app;

import android.content.Intent;

/**
 * Expose the {@link FragmentManagerImpl#findFragmentByWho(String)} interface used by
 * {@link FragmentActivity#onActivityResult(int, int, Intent)}.
 */
public class FriendlyFragment {

    private FriendlyFragment() {}

    public static String getWho(Fragment fragment) {
        return fragment.mWho;
    }

    @Deprecated
    public static Fragment findByWho(FragmentManager fragmentManager, String who) {
        FragmentManagerImpl fragmentManagerImpl = (FragmentManagerImpl) fragmentManager;
        return fragmentManagerImpl.findFragmentByWho(who);
    }

    public static Fragment findByWho(FragmentActivity activity, String who) {
        //noinspection deprecation
        return findByWho(activity.getSupportFragmentManager(), who);
    }
}
