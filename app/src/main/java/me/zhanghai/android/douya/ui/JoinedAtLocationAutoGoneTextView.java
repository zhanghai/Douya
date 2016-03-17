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

public class JoinedAtLocationAutoGoneTextView extends TimeTextView {

    private String mLocation;

    public JoinedAtLocationAutoGoneTextView(Context context) {
        super(context);
    }

    public JoinedAtLocationAutoGoneTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public JoinedAtLocationAutoGoneTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public JoinedAtLocationAutoGoneTextView(Context context, AttributeSet attrs, int defStyleAttr,
                                            int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void setDoubanTime(String doubanTime) {
        throw new UnsupportedOperationException("Use setJoinedAtAndLocation() instead.");
    }

    public void setJoinedAtAndLocation(String doubanTime, String location) {
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
            text = getContext().getString(R.string.profile_joined_at_and_location_format, timeText,
                    mLocation);
        } else {
            text = getContext().getString(R.string.profile_joined_at_format, timeText);
        }
        setText(text);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);

        ViewUtils.setVisibleOrGone(this, !TextUtils.isEmpty(text));
    }
}
