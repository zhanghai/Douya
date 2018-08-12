/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

/**
 * {@code Chat} in Frodo.
 */
public abstract class BaseDoumailThread implements Parcelable {

    public enum Type {

        DOUMAIL("chat"),
        DISCUSSION("discuss"),
        GROUP_CHAT("group_chat");

        private String mApiString;

        Type(String apiString) {
            mApiString = apiString;
        }

        public static Type ofApiString(String apiString, Type defaultValue) {
            for (Type type : Type.values()) {
                if (TextUtils.equals(type.mApiString, apiString)) {
                    return type;
                }
            }
            return defaultValue;
        }

        public static Type ofApiString(String apiString) {
            return ofApiString(apiString, null);
        }

        public String getApiString() {
            return mApiString;
        }
    }

    @SerializedName("conversation_id")
    public String conversationId;

    @SerializedName("conversation_type")
    public String conversationType;

    @SerializedName("create_time")
    public String createTime;

    @SerializedName("last_message")
    public Doumail lastDoumail;

    public boolean pinned;

    /**
     * @deprecated Use {@link #getType()} instead.
     */
    public String type;

    public Type getType() {
        //noinspection deprecation
        return Type.ofApiString(type);
    }

    @SerializedName("unread_count")
    public int unreadCount;

    public String uri;


    public BaseDoumailThread() {}

    protected BaseDoumailThread(Parcel in) {
        conversationId = in.readString();
        conversationType = in.readString();
        createTime = in.readString();
        lastDoumail = in.readParcelable(Doumail.class.getClassLoader());
        pinned = in.readByte() != 0;
        //noinspection deprecation
        type = in.readString();
        unreadCount = in.readInt();
        uri = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(conversationId);
        dest.writeString(conversationType);
        dest.writeString(createTime);
        dest.writeParcelable(lastDoumail, flags);
        dest.writeByte(pinned ? (byte) 1 : (byte) 0);
        //noinspection deprecation
        dest.writeString(type);
        dest.writeInt(unreadCount);
        dest.writeString(uri);
    }
}
