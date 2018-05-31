/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import me.zhanghai.android.douya.functional.Functional;

public class Book extends SimpleBook {

    @SerializedName("author_intro")
    public String authorIntroduction;

    @SerializedName("book_series")
    public Series bookSeries;

    @SerializedName("buylinks_url")
    public String buyUrl;

    @SerializedName("catalog")
    public String tableOfContents;

    @SerializedName("debut_url")
    public String debutUrl;

    @SerializedName("ebook_type_name")
    public String ebookTypeName;

    public ArrayList<Ebook> ebooks = new ArrayList<>();

    @SerializedName("info_url")
    public String informationUrl;

    @SerializedName("pages")
    public ArrayList<String> pageCounts = new ArrayList<>();

    @SerializedName("price")
    public ArrayList<String> prices = new ArrayList<>();

    @SerializedName("store_uri")
    public String storeUri;

    @SerializedName("subtitle")
    public ArrayList<String> subtitles = new ArrayList<>();

    @SerializedName("translator")
    public ArrayList<String> translators = new ArrayList<>();

    public String getYearMonth(Context context) {
        return CollectableItem.getYearMonth(releaseDates, context);
    }

    public List<String> getPageCountStrings() {
        return Functional.map(pageCounts, pageCount -> pageCount + "页");
    }


    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel source) {
            return new Book(source);
        }
        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    public Book() {}

    protected Book(Parcel in) {
        super(in);

        authorIntroduction = in.readString();
        bookSeries = in.readParcelable(Series.class.getClassLoader());
        buyUrl = in.readString();
        tableOfContents = in.readString();
        debutUrl = in.readString();
        ebookTypeName = in.readString();
        ebooks = in.createTypedArrayList(Ebook.CREATOR);
        informationUrl = in.readString();
        pageCounts = in.createStringArrayList();
        prices = in.createStringArrayList();
        storeUri = in.readString();
        subtitles = in.createStringArrayList();
        translators = in.createStringArrayList();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeString(authorIntroduction);
        dest.writeParcelable(bookSeries, flags);
        dest.writeString(buyUrl);
        dest.writeString(tableOfContents);
        dest.writeString(debutUrl);
        dest.writeString(ebookTypeName);
        dest.writeTypedList(ebooks);
        dest.writeString(informationUrl);
        dest.writeStringList(pageCounts);
        dest.writeStringList(prices);
        dest.writeString(storeUri);
        dest.writeStringList(subtitles);
        dest.writeStringList(translators);
    }


    public static class Series implements Parcelable {

        public String id;

        @SerializedName("publisher_all")
        public ArrayList<String> publishers = new ArrayList<>();

        @SerializedName("publisher_basic")
        public String publisher;

        @SerializedName("sharing_url")
        public String shareUrl;

        public String text;

        public String title;

        @SerializedName("total_number")
        public int count;

        public String type;

        public String uri;

        public String url;


        public static final Creator<Series> CREATOR = new Creator<Series>() {
            @Override
            public Series createFromParcel(Parcel source) {
                return new Series(source);
            }
            @Override
            public Series[] newArray(int size) {
                return new Series[size];
            }
        };

        public Series() {}

        protected Series(Parcel in) {
            id = in.readString();
            publishers = in.createStringArrayList();
            publisher = in.readString();
            shareUrl = in.readString();
            text = in.readString();
            title = in.readString();
            count = in.readInt();
            type = in.readString();
            uri = in.readString();
            url = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(id);
            dest.writeStringList(publishers);
            dest.writeString(publisher);
            dest.writeString(shareUrl);
            dest.writeString(text);
            dest.writeString(title);
            dest.writeInt(count);
            dest.writeString(type);
            dest.writeString(uri);
            dest.writeString(url);
        }
    }
}
