/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.content.Context;
import android.graphics.Color;
import android.os.Parcel;
import android.text.TextUtils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;

import org.threeten.bp.LocalDate;
import org.threeten.bp.Year;
import org.threeten.bp.YearMonth;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.DateTimeParseException;

import java.util.List;

import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.util.CollectionUtils;
import me.zhanghai.android.douya.util.TimeUtils;

/**
 * {@code LegacySubject} in Frodo, for those that can have a rating.
 */
public abstract class CollectableItem extends BaseItem {

    public enum Type {

        APP("app", R.string.item_app_name, R.string.item_app_action, R.string.item_app_this_item,
                false),
        BOOK("book", R.string.item_book_name, R.string.item_book_action,
                R.string.item_book_this_item, true),
        EVENT("event", R.string.item_event_name, R.string.item_event_action,
                R.string.item_event_this_item, false),
        GAME("game", R.string.item_game_name, R.string.item_game_action,
                R.string.item_game_this_item, true),
        MOVIE("movie", R.string.item_movie_name, R.string.item_movie_action,
                R.string.item_movie_this_item, false),
        MUSIC("music", R.string.item_music_name, R.string.item_music_action,
                R.string.item_music_this_item, true),
        TV("tv", R.string.item_tv_name, R.string.item_tv_action, R.string.item_tv_this_item, true);

        private String mApiString;
        private int mNameRes;
        private int mActionRes;
        private int mThisItemRes;
        private boolean mHasDoingState;

        Type(String apiString, int nameRes, int actionRes, int thisItemRes, boolean hasDoingState) {
            mApiString = apiString;
            mNameRes = nameRes;
            mActionRes = actionRes;
            mThisItemRes = thisItemRes;
            mHasDoingState = hasDoingState;
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

        public int getNameRes() {
            return mNameRes;
        }

        public String getName(Context context) {
            return context.getString(mNameRes);
        }

        public int getActionRes() {
            return mActionRes;
        }

        public String getAction(Context context) {
            return context.getString(mActionRes);
        }

        public int getThisItemRes() {
            return mThisItemRes;
        }

        public String getThisItem(Context context) {
            return context.getString(mThisItemRes);
        }

        public boolean hasDoingState() {
            return mHasDoingState;
        }
    }

    /**
     * @deprecated Use {@link #getBackgroundColor()} instead.
     */
    @SerializedName("body_bg_color")
    public String backgroundColor;

    @SerializedName("comment_count")
    public int commentCount;

    /**
     * @deprecated Use {@link #getThemeColor()} instead.
     */
    @SerializedName("header_bg_color")
    public String themeColor;

    @SerializedName("interest")
    public SimpleItemCollection collection;

    @SerializedName("intro")
    public String introduction;

    @SerializedName("in_blacklist")
    public boolean isInBlackList;

    @SerializedName("is_douban_intro")
    public boolean isIntroductionByDouban;

    /**
     * @deprecated Use {@link #getRatingUnavailableReason(Context)} instead.
     */
    @SerializedName("null_rating_reason")
    public String ratingUnavailableReason;

    @SerializedName("pic")
    public Image cover;

    public SimpleRating rating;

    @SerializedName("review_count")
    public int reviewCount;

    @SerializedName("vendor_count")
    public int vendorCount;

    public int getBackgroundColor() {
        //noinspection deprecation
        return Color.parseColor("#" + backgroundColor);
    }

    public int getThemeColor() {
        //noinspection deprecation
        return Color.parseColor("#" + themeColor);
    }

    public Type getType() {
        return Type.ofApiString(type);
    }

    public String getPrettyIntroduction() {
        if (TextUtils.isEmpty(introduction)) {
            return introduction;
        }
        return introduction.replaceAll("(?<!\n)\n(?!\n)", "\n\n");
    }

    public String getRatingUnavailableReason(Context context) {
        //noinspection deprecation
        return Rating.getRatingUnavailableReason(ratingUnavailableReason, context);
    }

    private static String truncateReleaseDate(String releaseDate) {
        if (TextUtils.isEmpty(releaseDate) || releaseDate.length() < 10) {
            return releaseDate;
        }
        return releaseDate.substring(0, 10);
    }

    protected static String getReleaseDate(List<String> releaseDates) {
        return truncateReleaseDate(CollectionUtils.firstOrNull(releaseDates));
    }

    protected static String getYearMonth(String releaseDate, Context context) {
        releaseDate = truncateReleaseDate(releaseDate);
        if (TextUtils.isEmpty(releaseDate)) {
            return null;
        }
        try {
            LocalDate date = TimeUtils.parseDoubanDate(releaseDate);
            return DateTimeFormatter.ofPattern(context.getString(R.string.year_month_pattern))
                    .format(date);
        } catch (DateTimeParseException e) {
            e.printStackTrace();
        }
        try {
            YearMonth yearMonth = TimeUtils.parseDoubanYearMonth(releaseDate);
            // Throws UnsupportedTemporalTypeException: Unsupported field: DayOfWeek
            //return DateTimeFormatter.ofPattern(context.getString(R.string.year_month_pattern))
            //        .format(yearMonth);
            return context.getString(R.string.year_month_format, yearMonth.getYear(),
                    yearMonth.getMonthValue());
        } catch (DateTimeParseException e) {
            e.printStackTrace();
        }
        try {
            Year year = Year.parse(releaseDate);
            // Throws UnsupportedTemporalTypeException: Unsupported field: DayOfWeek
            //return DateTimeFormatter.ofPattern(context.getString(R.string.year_pattern))
            //        .format(year);
            return context.getString(R.string.year_format, year.getValue());
        } catch (DateTimeParseException e) {
            e.printStackTrace();
        }
        return releaseDate;
    }

    protected static String getYearMonth(List<String> releaseDates, Context context) {
        return getYearMonth(CollectionUtils.firstOrNull(releaseDates), context);
    }

    public static class Deserializer implements JsonDeserializer<CollectableItem> {

        @Override
        public CollectableItem deserialize(JsonElement json, java.lang.reflect.Type typeOfT,
                                           JsonDeserializationContext context)
                throws JsonParseException {
            java.lang.reflect.Type type = null;
            Type itemType = Type.ofApiString(json.getAsJsonObject().get("type").getAsString());
            if (itemType != null) {
                switch (itemType) {
//                    case APP:
//                        break;
                    case BOOK:
                        type = SimpleBook.class;
                        break;
//                    case EVENT:
//                        break;
                    case GAME:
                        type = SimpleGame.class;
                        break;
                    case MOVIE:
                    case TV:
                        type = SimpleMovie.class;
                        break;
                    case MUSIC:
                        type = SimpleMusic.class;
                        break;
                }
            }
            if (type == null) {
                type = UnknownCollectableItem.class;
            }
            return context.deserialize(json, type);
        }
    }


    public CollectableItem() {}

    protected CollectableItem(Parcel in) {
        super(in);

        //noinspection deprecation
        backgroundColor = in.readString();
        commentCount = in.readInt();
        //noinspection deprecation
        themeColor = in.readString();
        collection = in.readParcelable(SimpleItemCollection.class.getClassLoader());
        introduction = in.readString();
        isInBlackList = in.readByte() != 0;
        isIntroductionByDouban = in.readByte() != 0;
        cover = in.readParcelable(Image.class.getClassLoader());
        //noinspection deprecation
        ratingUnavailableReason = in.readString();
        rating = in.readParcelable(SimpleRating.class.getClassLoader());
        reviewCount = in.readInt();
        vendorCount = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        //noinspection deprecation
        dest.writeString(backgroundColor);
        dest.writeInt(commentCount);
        //noinspection deprecation
        dest.writeString(themeColor);
        dest.writeParcelable(collection, flags);
        dest.writeString(introduction);
        dest.writeByte(isInBlackList ? (byte) 1 : (byte) 0);
        dest.writeByte(isIntroductionByDouban ? (byte) 1 : (byte) 0);
        dest.writeParcelable(cover, flags);
        //noinspection deprecation
        dest.writeString(ratingUnavailableReason);
        dest.writeParcelable(rating, flags);
        dest.writeInt(reviewCount);
        dest.writeInt(vendorCount);
    }
}
