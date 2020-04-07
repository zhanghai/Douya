/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.Gravity
import androidx.core.view.doOnPreDraw
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.stateful.ExtendableSavedState
import me.zhanghai.android.douya.util.slideToVisibilityUnsafe

class QuickReturnFloatingActionButton : FloatingActionButton {
    var showing = true
        set(value) {
            if (field == value) {
                return
            }
            field = value
            slideToVisibilityUnsafe(Gravity.BOTTOM, value)
        }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    )

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState() ?: Bundle()
        return ExtendableSavedState(superState).apply {
            extendableStates.put(
                STATE_KEY,
                Bundle().apply {
                    putBoolean(STATE_SHOWING, showing)
                }
            )
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state !is ExtendableSavedState) {
            super.onRestoreInstanceState(state)
            return
        }
        super.onRestoreInstanceState(state.superState)
        // Wait until view is laid out.
        doOnPreDraw {
            showing = state.extendableStates[STATE_KEY]!!.getBoolean(STATE_SHOWING)
        }
    }

    companion object {
        private val STATE_KEY = QuickReturnFloatingActionButton::class.java.name
        private const val STATE_SHOWING = "SHOWING"
    }
}
