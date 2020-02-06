/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.BlendMode
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.InsetDrawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.RippleDrawable
import android.util.AttributeSet
import com.google.android.material.button.MaterialButton
import com.google.android.material.ripple.RippleUtils
import com.google.android.material.shape.MaterialShapeDrawable
import me.zhanghai.android.douya.util.DrawableWrapper

@SuppressLint("RestrictedApi")
class IconBackgroundButton : MaterialButton {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    )

    init {
        val iconBackground = MaterialShapeDrawable(shapeAppearanceModel).apply {
            tintList = supportBackgroundTintList
            supportBackgroundTintMode?.let { setTintMode(it) }
        }
        val insets = getInsets()
        val icon = InsetDrawable(icon, insets.left, insets.top, insets.right, insets.bottom)
        val content = IgnoreTintAndPaddingDrawableWrapper(
            LayerDrawable(arrayOf(iconBackground, icon))
        )
        val mask = MaterialShapeDrawable(shapeAppearanceModel).apply { setTint(Color.WHITE) }
        val rippleDrawable = RippleDrawable(
            RippleUtils.sanitizeRippleDrawableColor(rippleColor), content, mask
        )

        background = null
        supportBackgroundTintList = null
        supportBackgroundTintMode = null
        this.icon = null
        iconTint = null
        iconTintMode = null
        setPadding(
            paddingLeft - insets.left, paddingRight - insets.right, paddingTop - insets.top,
            paddingBottom - insets.bottom
        )

        this.icon = rippleDrawable

        updateIconPadding()
    }

    private fun getInsets(): Rect {
        val backgroundDrawable = background as RippleDrawable
        val insetDrawable = backgroundDrawable.getDrawable(0) as InsetDrawable
        val insets = Rect()
        insetDrawable.getPadding(insets)
        return insets
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        super.setText(text, type)

        updateIconPadding()
    }

    private fun updateIconPadding() {
        iconPadding = if (!text.isNullOrEmpty()) paddingEnd else 0
    }
}

private class IgnoreTintAndPaddingDrawableWrapper : DrawableWrapper {
    companion object {
        private val CREATOR: (
            ConstantState,
            Resources?,
            Resources.Theme?
        ) -> DrawableWrapper = ::IgnoreTintAndPaddingDrawableWrapper
    }

    override val creator
        get() = CREATOR

    constructor(drawable: Drawable) : super(drawable)

    protected constructor(
        state: ConstantState,
        resources: Resources?,
        theme: Resources.Theme?
    ) : super(state, resources, theme)

    override fun setTint(tintColor: Int) {}

    override fun setTintList(tint: ColorStateList?) {}

    override fun setTintMode(tintMode: PorterDuff.Mode?) {}

    override fun setTintBlendMode(blendMode: BlendMode?) {}

    override fun getPadding(padding: Rect): Boolean {
        padding.set(0, 0, 0, 0)
        return false
    }
}
