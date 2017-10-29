/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

public class Honor implements Parcelable {

    public enum Type {

        TOP_250("top250");

        private String apiString;

        Type(String apiString) {
            this.apiString = apiString;
        }

        public static Type ofApiString(String apiString, Type defaultValue) {
            for (Type type : Type.values()) {
                if (TextUtils.equals(type.apiString, apiString)) {
                    return type;
                }
            }
            return defaultValue;
        }

        public static Type ofApiString(String apiString) {
            return ofApiString(apiString, null);
        }
    }

    /**
     * @deprecated Use {@link #getType()} instead.
     */
    @SerializedName("kind")
    public String type;

    public int rank;

    public String title;

    public String uri;

    public Type getType() {
        //noinspection deprecation
        return Type.ofApiString(type);
    }


    public static final Parcelable.Creator<Honor> CREATOR = new Parcelable.Creator<Honor>() {
        @Override
        public Honor createFromParcel(Parcel source) {
            return new Honor(source);
        }
        @Override
        public Honor[] newArray(int size) {
            return new Honor[size];
        }
    };

    public Honor() {}

    protected Honor(Parcel in) {
        type = in.readString();
        rank = in.readInt();
        title = in.readString();
        uri = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeInt(rank);
        dest.writeString(title);
        dest.writeString(uri);
    }
}
