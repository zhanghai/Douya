/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util;

import android.content.Context;

import org.threeten.bp.Duration;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.chrono.IsoChronology;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.DateTimeFormatterBuilder;
import org.threeten.bp.format.DateTimeParseException;

import me.zhanghai.android.douya.R;

public class TimeUtils {

    private static final int SECONDS_PER_MINUTE = 60;
    private static final int MINUTES_PER_HOUR = 60;
    private static final int SECONDS_PER_HOUR = SECONDS_PER_MINUTE * MINUTES_PER_HOUR;

    private static final DateTimeFormatter DOUBAN_DATE_TIME_FORMATTER =
            new DateTimeFormatterBuilder()
                    .append(DateTimeFormatter.ISO_LOCAL_DATE)
                    .appendLiteral(' ')
                    .append(DateTimeFormatter.ISO_LOCAL_TIME)
                    .toFormatter()
                    .withChronology(IsoChronology.INSTANCE);

    private static final ZoneId DOUBAN_ZONED_ID = ZoneId.of("Asia/Shanghai");

    private static final Duration JUST_NOW_DURATION = Duration.ofMinutes(1);
    private static final Duration MINUTE_PATTERN_DURATION = Duration.ofHours(1);
    private static final Duration HOUR_PATTERN_DURATION = Duration.ofHours(2);

    /**
     * @throws DateTimeParseException
     */
    public static ZonedDateTime parseDoubanDateTime(String doubanDateTime) {
        return ZonedDateTime.of(LocalDateTime.parse(doubanDateTime, DOUBAN_DATE_TIME_FORMATTER),
                DOUBAN_ZONED_ID);
    }

    public static String formatDateTime(ZonedDateTime dateTime, Context context) {
        ZonedDateTime now = ZonedDateTime.now().withZoneSameInstant(dateTime.getZone());
        LocalDate date = dateTime.toLocalDate();
        LocalDate nowDate = now.toLocalDate();
        if (date.equals(nowDate)) {
            Duration duration = Duration.between(dateTime, now);
            if (duration.compareTo(Duration.ZERO) > 0) {
                if (duration.compareTo(JUST_NOW_DURATION) < 0) {
                    return context.getString(R.string.just_now);
                } else if (duration.compareTo(MINUTE_PATTERN_DURATION) < 0) {
                    return context.getString(R.string.minute_format,
                            Math.round((float) duration.getSeconds() / SECONDS_PER_MINUTE));
                } else if (duration.compareTo(HOUR_PATTERN_DURATION) < 0) {
                    return context.getString(R.string.hour_format,
                            Math.round((float) duration.getSeconds() / SECONDS_PER_HOUR));
                }
            }
            return DateTimeFormatter
                    .ofPattern(context.getString(R.string.today_hour_minute_pattern))
                    .format(dateTime);
        }
        if (date.plusDays(1).equals(nowDate)) {
            return DateTimeFormatter
                    .ofPattern(context.getString(R.string.yesterday_hour_minute_pattern))
                    .format(dateTime);
        } else if (date.getYear() == nowDate.getYear()) {
            return DateTimeFormatter
                    .ofPattern(context.getString(R.string.month_day_hour_minute_pattern))
                    .format(dateTime);
        } else {
            return DateTimeFormatter
                    .ofPattern(context.getString(R.string.date_hour_minute_pattern))
                    .format(dateTime);
        }
    }

    /**
     * Use {@link me.zhanghai.android.douya.ui.TimeTextView} instead if the text is to be set on a
     * {@code TextView}.
     */
    public static String formatDoubanDateTime(String doubanDateTime, Context context) {
        try {
            return formatDateTime(parseDoubanDateTime(doubanDateTime), context);
        } catch (DateTimeParseException e) {
            LogUtils.e("Unable to parse date time: " + doubanDateTime);
            e.printStackTrace();
            return doubanDateTime;
        }
    }

    public static String formatDate(LocalDate date, ZoneId zone, Context context) {
        ZonedDateTime now = ZonedDateTime.now().withZoneSameInstant(zone);
        LocalDate nowDate = now.toLocalDate();
        if (date.equals(nowDate)) {
            return context.getString(R.string.today);
        }
        if (date.plusDays(1).equals(nowDate)) {
            return context.getString(R.string.yesterday);
        } else if (date.getYear() == nowDate.getYear()) {
            return DateTimeFormatter
                    .ofPattern(context.getString(R.string.month_day_pattern))
                    .format(date);
        } else {
            return DateTimeFormatter
                    .ofPattern(context.getString(R.string.date_pattern))
                    .format(date);
        }
    }

    public static String formatDate(ZonedDateTime dateTime, Context context) {
        return formatDate(dateTime.toLocalDate(), dateTime.getZone(), context);
    }
}
