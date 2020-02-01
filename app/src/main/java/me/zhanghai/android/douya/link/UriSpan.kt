/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.link

import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View

class UriSpan(private val uri: String) : ClickableSpan() {
    override fun updateDrawState(textPaint: TextPaint) {
        super.updateDrawState(textPaint)

        textPaint.isUnderlineText = false
    }

    override fun onClick(widget: View) {
        UriHandler.open(uri, widget.context)
    }
}
