/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintHelper
import androidx.constraintlayout.widget.ConstraintLayout

class Group : ConstraintHelper {

    private val views = Iterable {
        object : Iterator<View> {
            private var container = parent as ConstraintLayout
            private var index = 0
            override fun hasNext() = index < mCount
            override fun next() = container.getViewById(mIds[index]).also { ++index }
        }
    }

    private var attributesApplied = false

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
        : super(context, attrs, defStyleAttr)

    override fun updatePreLayout(container: ConstraintLayout?) {
        super.updatePreLayout(container)

        if (!attributesApplied) {
            views.forEach {
                it.alpha = alpha
                it.visibility = visibility
            }
            attributesApplied = true
        }
    }

    override fun setAlpha(alpha: Float) {
        super.setAlpha(alpha)

        views.forEach { it.alpha = alpha }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun setTransitionAlpha(alpha: Float) {
        super.setTransitionAlpha(alpha)

        views.forEach { it.transitionAlpha = alpha }
    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)

        views.forEach { it.visibility = visibility }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun setTransitionVisibility(visibility: Int) {
        super.setTransitionVisibility(visibility)

        views.forEach { it.setTransitionVisibility(visibility) }
    }
}
