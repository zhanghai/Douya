/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util;

import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

public class ParcelCompat {

    private ParcelCompat() {}

    /**
     * @see android.support.v4.os.ParcelCompat#readBoolean(Parcel)
     */
    public static boolean readBoolean(@NonNull Parcel in) {
        return android.support.v4.os.ParcelCompat.readBoolean(in);
    }

    /**
     * @see android.support.v4.os.ParcelCompat#writeBoolean(Parcel, boolean)
     */
    public static void writeBoolean(@NonNull Parcel out, boolean value) {
        android.support.v4.os.ParcelCompat.writeBoolean(out, value);
    }

    /*
     * @see android.os.Parcel#readCharSequence()
     */
    public static CharSequence readCharSequence(@NonNull Parcel in) {
        return TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(in);
    }

    /*
     * @see android.os.Parcel#writeCharSequence(CharSequence)
     */
    public static void writeCharSequence(@NonNull Parcel out, @Nullable CharSequence value) {
        TextUtils.writeToParcel(value, out, 0);
    }
}
