/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.ArrowKeyMovementMethod;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class SelectableLinkMovementMethod extends LinkMovementMethod {

    private static SelectableLinkMovementMethod sInstance;

    public static SelectableLinkMovementMethod getInstance() {
        if (sInstance == null) {
            sInstance = new SelectableLinkMovementMethod();
        }
        return sInstance;
    }

    // LinkMovementMethod overrides this only since Kitkat.
    @Override
    public boolean canSelectArbitrarily() {
        return true;
    }

    /**
     * @see ArrowKeyMovementMethod#initialize(TextView, Spannable)
     */
    @Override
    public void initialize(TextView widget, Spannable text) {
        Selection.setSelection(text, 0);
    }

    /**
     * @see ArrowKeyMovementMethod#onTakeFocus(TextView, Spannable, int)
     */
    @Override
    public void onTakeFocus(TextView view, Spannable text, int dir) {
        if ((dir & (View.FOCUS_FORWARD | View.FOCUS_DOWN)) != 0) {
            if (view.getLayout() == null) {
                // This shouldn't be null, but do something sensible if it is.
                Selection.setSelection(text, text.length());
            }
        } else {
            Selection.setSelection(text, text.length());
        }
    }

    @Override
    public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {

        int action = event.getAction();

        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            x -= widget.getTotalPaddingLeft();
            y -= widget.getTotalPaddingTop();

            x += widget.getScrollX();
            y += widget.getScrollY();

            Layout layout = widget.getLayout();
            int line = layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);

            ClickableSpan[] links = buffer.getSpans(off, off, ClickableSpan.class);

            if (links.length != 0) {
                if (action == MotionEvent.ACTION_UP) {
                    links[0].onClick(widget);
                } else if (action == MotionEvent.ACTION_DOWN) {
                    Selection.setSelection(buffer,
                            buffer.getSpanStart(links[0]),
                            buffer.getSpanEnd(links[0]));
                }
                return true;
            }
            // Removed: See https://stackoverflow.com/a/30572151
            // else {
            //    Selection.removeSelection(buffer);
            //}
        }

        return super.onTouchEvent(widget, buffer, event);
    }
}
