/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Rating implements Parcelable {

    @SerializedName("following")
    public FollowingsRating followingsRating;

    @SerializedName("stats")
    public ArrayList<Float> distribution = new ArrayList<>();

    @SerializedName("type_ranks")
    public ArrayList<GenreRanking> genreRankings = new ArrayList<>();

    /**
     * Frodo API doesn't have this field, so this needs to be set manually.
     */
    @Nullable
    public SimpleRating rating;

    /**
     * Frodo API doesn't have this field, so this needs to be set manually.
     */
    @Nullable
    public String ratingUnavailableReason;


    public static final Parcelable.Creator<Rating> CREATOR = new Parcelable.Creator<Rating>() {
        @Override
        public Rating createFromParcel(Parcel source) {
            return new Rating(source);
        }
        @Override
        public Rating[] newArray(int size) {
            return new Rating[size];
        }
    };

    public Rating() {}

    protected Rating(Parcel in) {
        followingsRating = in.readParcelable(FollowingsRating.class.getClassLoader());
        in.readList(distribution, Float.class.getClassLoader());
        genreRankings = in.createTypedArrayList(GenreRanking.CREATOR);
        rating = in.readParcelable(SimpleRating.class.getClassLoader());
        ratingUnavailableReason = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(followingsRating, flags);
        dest.writeList(distribution);
        dest.writeTypedList(genreRankings);
        dest.writeParcelable(rating, flags);
        dest.writeString(ratingUnavailableReason);
    }


    public static class FollowingsRating extends SimpleRating {

        public ArrayList<User> users = new ArrayList<>();


        public static final Creator<FollowingsRating> CREATOR = new Creator<FollowingsRating>() {
            @Override
            public FollowingsRating createFromParcel(Parcel source) {
                return new FollowingsRating(source);
            }
            @Override
            public FollowingsRating[] newArray(int size) {
                return new FollowingsRating[size];
            }
        };

        public FollowingsRating() {}

        protected FollowingsRating(Parcel in) {
            super(in);

            users = in.createTypedArrayList(User.CREATOR);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);

            dest.writeTypedList(users);
        }
    }

    public static class GenreRanking implements Parcelable {

        @SerializedName("rank")
        public float ranking;

        @SerializedName("type")
        public String gnere;


        public static final Parcelable.Creator<GenreRanking> CREATOR =
                new Parcelable.Creator<GenreRanking>() {
                    @Override
                    public GenreRanking createFromParcel(Parcel source) {
                        return new GenreRanking(source);
                    }
                    @Override
                    public GenreRanking[] newArray(int size) {
                        return new GenreRanking[size];
                    }
                };

        public GenreRanking() {}

        protected GenreRanking(Parcel in) {
            ranking = in.readFloat();
            gnere = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeFloat(ranking);
            dest.writeString(gnere);
        }
    }
}
