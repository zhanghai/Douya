/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;

import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeParseException;

import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.util.LogUtils;
import me.zhanghai.android.douya.util.TimeUtils;
import me.zhanghai.android.douya.util.ViewUtils;

public class JoinTimeLocationAutoGoneTextView extends TimeTextView {

    private String mLocation;

    public JoinTimeLocationAutoGoneTextView(Context context) {
        super(context);
    }

    public JoinTimeLocationAutoGoneTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public JoinTimeLocationAutoGoneTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setDoubanTime(String doubanTime) {
        throw new UnsupportedOperationException("Use setJoinTimeAndLocation() instead.");
    }

    public void setJoinTimeAndLocation(String doubanTime, String location) {
        mLocation = location;
        try {
            setTime(TimeUtils.parseDoubanDateTime(doubanTime));
        } catch (DateTimeParseException e) {
            LogUtils.e("Unable to parse date time: " + doubanTime);
            e.printStackTrace();
            setTimeText(doubanTime);
        }
    }

    @Override
    protected String formatTime(ZonedDateTime time) {
        return TimeUtils.formatDate(time, getContext());
    }

    @Override
    protected void setTimeText(String timeText) {
        String text;
        if (!TextUtils.isEmpty(mLocation)) {
            text = getContext().getString(R.string.profile_join_time_location_format, timeText,
                    mLocation);
        } else {
            text = getContext().getString(R.string.profile_join_time_format, timeText);
        }
        setText(text);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);

        ViewUtils.setVisibleOrGone(this, !TextUtils.isEmpty(text));
    }
}
