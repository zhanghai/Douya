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
 * {@code CardMessage} in Frodo.
 */
public class DoumailCard implements Parcelable {

    @SerializedName("desc")
    public String description;

    public String icon;

    @SerializedName("short_title")
    public String shortTitle;

    public Style style;

    public String tag;

    public String title;

    public String uri;


    public static final Parcelable.Creator<DoumailCard> CREATOR =
            new Parcelable.Creator<DoumailCard>() {
                @Override
                public DoumailCard createFromParcel(Parcel source) {
                    return new DoumailCard(source);
                }
                @Override
                public DoumailCard[] newArray(int size) {
                    return new DoumailCard[size];
                }
            };

    public DoumailCard() {}

    protected DoumailCard(Parcel in) {
        description = in.readString();
        icon = in.readString();
        shortTitle = in.readString();
        style = in.readParcelable(Style.class.getClassLoader());
        tag = in.readString();
        title = in.readString();
        uri = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(description);
        dest.writeString(icon);
        dest.writeString(shortTitle);
        dest.writeParcelable(style, flags);
        dest.writeString(tag);
        dest.writeString(title);
        dest.writeString(uri);
    }


    public static class Style implements Parcelable {

        public enum IconAlignment {

            LEFT("left"),
            RIGHT("right");

            private String mApiString;

            IconAlignment(String apiString) {
                mApiString = apiString;
            }

            public static IconAlignment ofApiString(String apiString, IconAlignment defaultValue) {
                for (IconAlignment iconAlignment : IconAlignment.values()) {
                    if (TextUtils.equals(iconAlignment.mApiString, apiString)) {
                        return iconAlignment;
                    }
                }
                return defaultValue;
            }

            public static IconAlignment ofApiString(String apiString) {
                return ofApiString(apiString, null);
            }

            public String getApiString() {
                return mApiString;
            }
        }

        public enum IconOutline {

            CIRCLE("circle"),
            SQUARE("square");

            private String mApiString;

            IconOutline(String apiString) {
                mApiString = apiString;
            }

            public static IconOutline ofApiString(String apiString, IconOutline defaultValue) {
                for (IconOutline iconOutline : IconOutline.values()) {
                    if (TextUtils.equals(iconOutline.mApiString, apiString)) {
                        return iconOutline;
                    }
                }
                return defaultValue;
            }

            public static IconOutline ofApiString(String apiString) {
                return ofApiString(apiString, null);
            }

            public String getApiString() {
                return mApiString;
            }
        }

        /**
         * @deprecated Use {@link #getIconAlignment()} instead.
         */
        @SerializedName("icon_align")
        public String iconAlignment;

        public IconAlignment getIconAlignment() {
            //noinspection deprecation
            return IconAlignment.ofApiString(iconAlignment);
        }

        /**
         * @deprecated Use {@link #getIconOutline()} instead.
         */
        @SerializedName("icon_shape")
        public String iconOutline;

        public IconOutline getIconOutline() {
            //noinspection deprecation
            return IconOutline.ofApiString(iconOutline);
        }


        public static final Parcelable.Creator<Style> CREATOR = new Parcelable.Creator<Style>() {
            @Override
            public Style createFromParcel(Parcel source) {
                return new Style(source);
            }
            @Override
            public Style[] newArray(int size) {
                return new Style[size];
            }
        };

        public Style() {}

        protected Style(Parcel in) {
            //noinspection deprecation
            iconAlignment = in.readString();
            //noinspection deprecation
            iconOutline = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            //noinspection deprecation
            dest.writeString(iconAlignment);
            //noinspection deprecation
            dest.writeString(iconOutline);
        }
    }
}
