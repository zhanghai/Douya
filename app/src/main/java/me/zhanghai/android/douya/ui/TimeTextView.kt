/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.textview.MaterialTextView
import me.zhanghai.android.douya.util.formatHumanFriendly
import org.threeten.bp.Duration
import org.threeten.bp.ZonedDateTime

class TimeTextView : MaterialTextView {
    companion object {
        private val UPDATE_TEXT_INTERVAL_MILLIS = Duration.ofSeconds(30).toMillis()
    }

    private val updateTextRunnable = Runnable { updateText() }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    )

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    var time: ZonedDateTime? = null
        set(value) {
            field = value
            updateText()
        }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        if (time != null) {
            updateText()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        removeCallbacks(updateTextRunnable)
    }

    private fun updateText() {
        removeCallbacks(updateTextRunnable)
        text = time?.formatHumanFriendly(context)
        if (time != null) {
            postDelayed(updateTextRunnable, UPDATE_TEXT_INTERVAL_MILLIS)
        }
    }
}
