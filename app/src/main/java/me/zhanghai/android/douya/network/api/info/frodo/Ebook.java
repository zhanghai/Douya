/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * {@code OnlineResource} in Frodo.
 */
public class Ebook implements Parcelable {

    @SerializedName("free")
    public boolean isFree;

    @SerializedName("reader_uri")
    public String readerUri;

    @SerializedName("reader_url")
    public String readerUrl;

    public Source source;

    @SerializedName("source_uri")
    public String sourceUri;

    public String url;


    public static final Creator<Ebook> CREATOR = new Creator<Ebook>() {
        @Override
        public Ebook createFromParcel(Parcel source) {
            return new Ebook(source);
        }
        @Override
        public Ebook[] newArray(int size) {
            return new Ebook[size];
        }
    };

    public Ebook() {}

    protected Ebook(Parcel in) {
        isFree = in.readByte() != 0;
        readerUri = in.readString();
        readerUrl = in.readString();
        source = in.readParcelable(Source.class.getClassLoader());
        sourceUri = in.readString();
        url = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(isFree ? (byte) 1 : (byte) 0);
        dest.writeString(readerUri);
        dest.writeString(readerUrl);
        dest.writeParcelable(source, flags);
        dest.writeString(sourceUri);
        dest.writeString(url);
    }


    /**
     * {@code OnlineResourcePlatform} in Frodo.
     */
    public static class Source implements Parcelable {

        public String literal;

        public String name;

        @SerializedName("pic")
        public String cover;

        @SerializedName("val")
        public int value;


        public static final Creator<Source> CREATOR = new Creator<Source>() {
            @Override
            public Source createFromParcel(Parcel source) {
                return new Source(source);
            }
            @Override
            public Source[] newArray(int size) {
                return new Source[size];
            }
        };

        public Source() {}

        protected Source(Parcel in) {
            literal = in.readString();
            name = in.readString();
            cover = in.readString();
            value = in.readInt();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(literal);
            dest.writeString(name);
            dest.writeString(cover);
            dest.writeInt(value);
        }
    }
}
