/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package android.support.v4.app;

/**
 * Expose the {@link Fragment#mWho} interface used by loader manager.
 */
public class FriendlyFragment {

    private FriendlyFragment() {}

    public static String getWho(Fragment fragment) {
        return fragment.mWho;
    }

    public static Fragment findByWho(FragmentManager fragmentManager, String who) {
        FragmentManagerImpl fragmentManagerImpl = (FragmentManagerImpl) fragmentManager;
        return fragmentManagerImpl.findFragmentByWho(who);
    }
}
