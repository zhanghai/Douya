/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class ConnectivityUtils {

    private ConnectivityUtils() {}

    public static boolean isConnected(Context context) {
        return isConnected(getActiveNetworkInfo(context));
    }

    private static boolean isConnected(NetworkInfo networkInfo) {
        return networkInfo != null && networkInfo.isConnected();
    }

    public static boolean isNonMobileConnected(Context context) {
        return isNonMobileConnected(getActiveNetworkInfo(context));
    }

    private static boolean isNonMobileConnected(NetworkInfo networkInfo) {
        return isConnected(networkInfo) && !isMobileNetwork(networkInfo);
    }

    private static boolean isMobileNetwork(@NonNull NetworkInfo networkInfo) {
        return isNetworkTypeMobile(networkInfo.getType());
    }

    /*
     * @see ConnectivityManager#isNetworkTypeMobile(int)
     */
    private static boolean isNetworkTypeMobile(int networkType) {
        switch (networkType) {
            case ConnectivityManager.TYPE_MOBILE:
                //noinspection deprecation
            case ConnectivityManager.TYPE_MOBILE_MMS:
                //noinspection deprecation
            case ConnectivityManager.TYPE_MOBILE_SUPL:
            case ConnectivityManager.TYPE_MOBILE_DUN:
                //noinspection deprecation
            case ConnectivityManager.TYPE_MOBILE_HIPRI:
                //case ConnectivityManager.TYPE_MOBILE_FOTA:
                //case ConnectivityManager.TYPE_MOBILE_IMS:
                //case ConnectivityManager.TYPE_MOBILE_CBS:
                //case ConnectivityManager.TYPE_MOBILE_IA:
                //case ConnectivityManager.TYPE_MOBILE_EMERGENCY:
                return true;
            default:
                return false;
        }
    }

    @Nullable
    private static NetworkInfo getActiveNetworkInfo(Context context) {
        return getConnectivityManager(context).getActiveNetworkInfo();
    }

    private static ConnectivityManager getConnectivityManager(Context context) {
        return (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }
}
