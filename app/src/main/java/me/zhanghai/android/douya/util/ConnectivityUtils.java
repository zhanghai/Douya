/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import me.zhanghai.android.douya.DouyaApplication;

public class ConnectivityUtils {

    private ConnectivityUtils() {}

    public static boolean isConnected() {
        return isConnected(getActiveNetworkInfo());
    }

    private static boolean isConnected(NetworkInfo networkInfo) {
        return networkInfo != null && networkInfo.isConnected();
    }

    public static boolean isNonMobileConnected() {
        return isNonMobileConnected(getActiveNetworkInfo());
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

    /**
     * @deprecated Remove @SuppressLint("MissingPermission") and add permission to use.
     */
    @SuppressLint("MissingPermission")
    @Nullable
    private static NetworkInfo getActiveNetworkInfo() {
        return getConnectivityManager().getActiveNetworkInfo();
    }

    private static ConnectivityManager getConnectivityManager() {
        return (ConnectivityManager) DouyaApplication.getInstance().getSystemService(
                Context.CONNECTIVITY_SERVICE);
    }
}
