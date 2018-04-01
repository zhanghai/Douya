/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;

/**
 * {@code PrivateChat} in Frodo.
 */
public class DoumailThread extends BaseDoumailThread {

    @SerializedName("image_disabled")
    public boolean isImageDisabled;

    @SerializedName("image_disabled_reason")
    public String imageDisabledReason;

    @SerializedName("target_user")
    public User targetUser;


    public static final Creator<DoumailThread> CREATOR = new Creator<DoumailThread>() {
        @Override
        public DoumailThread createFromParcel(Parcel source) {
            return new DoumailThread(source);
        }
        @Override
        public DoumailThread[] newArray(int size) {
            return new DoumailThread[size];
        }
    };

    public DoumailThread() {}

    protected DoumailThread(Parcel in) {
        super(in);

        isImageDisabled = in.readByte() != 0;
        imageDisabledReason = in.readString();
        targetUser = in.readParcelable(User.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeByte(isImageDisabled ? (byte) 1 : (byte) 0);
        dest.writeString(imageDisabledReason);
        dest.writeParcelable(targetUser, flags);
    }
}
