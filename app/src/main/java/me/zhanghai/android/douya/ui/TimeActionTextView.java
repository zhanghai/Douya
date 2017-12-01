/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.content.Context;
import android.util.AttributeSet;

import org.threeten.bp.format.DateTimeParseException;

import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.util.LogUtils;
import me.zhanghai.android.douya.util.TimeUtils;

public class TimeActionTextView extends TimeTextView {

    private boolean mCompact;
    private String mAction;

    public TimeActionTextView(Context context) {
        super(context);
    }

    public TimeActionTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TimeActionTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setDoubanTime(String doubanTime) {
        throw new UnsupportedOperationException("Use setDoubanTimeAndAction() instead.");
    }

    public void setDoubanTimeAndAction(String doubanTime, String action) {
        mAction = action;
        try {
            setTime(TimeUtils.parseDoubanDateTime(doubanTime));
        } catch (DateTimeParseException e) {
            LogUtils.e("Unable to parse date time: " + doubanTime);
            e.printStackTrace();
            setTimeText(doubanTime);
        }
    }

    public void setDoubanTimeAndAction(String doubanTime, int actionRes) {
        setDoubanTimeAndAction(doubanTime, getContext().getString(actionRes));
    }

    public boolean isCompact() {
        return mCompact;
    }

    public void setCompact(boolean compact) {
        if (mCompact == compact) {
            return;
        }
        mCompact = compact;
        updateTimeText();
    }

    @Override
    protected void setTimeText(String timeText) {
        setText(getContext().getString(mCompact ? R.string.time_action_format_compat
                : R.string.time_action_format, timeText, mAction));
    }
}
