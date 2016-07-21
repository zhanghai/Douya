/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import me.zhanghai.android.douya.link.UriHandler;

public class UriSpan extends ClickableSpan {

    private String mUri;

    public UriSpan(String uri) {
        mUri = uri;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);

        ds.setUnderlineText(false);
    }

    @Override
    public void onClick(View widget) {
        UriHandler.open(mUri, widget.getContext());
    }
}
