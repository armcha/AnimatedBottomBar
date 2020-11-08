package com.armcha.animatedbottombar.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import com.armcha.animatedbottombar.R
import com.armcha.animatedbottombar.animator.base.AnimatorProvider
import com.armcha.animatedbottombar.colorFrom
import com.armcha.animatedbottombar.config.OvalButtonConfig
import com.armcha.animatedbottombar.tint

internal class OvalButton(context: Context) : FrameLayout(context) {

    companion object {

        private const val SHADOW_RADIUS = 25f
    }

    private val path = Path()
    private val ovalRectF = RectF()
    private val iconImageView by lazy { ImageView(context) }
    private val paint = Paint().apply {
        style = Paint.Style.FILL
        strokeWidth = 1f
        isAntiAlias = true
        setShadowLayer(SHADOW_RADIUS, 0f, 0f, ContextCompat.getColor(context, R.color.gray_600))
    }
    var animatorProvider: AnimatorProvider? = null

    var config = OvalButtonConfig(colorFrom(R.color.purple_500), colorFrom(R.color.white), R.drawable.bell_outline,
            android.R.drawable.ic_menu_close_clear_cancel)
        set(value) {
            field = value
            update()
            invalidate()
        }

    init {
        setWillNotDraw(false)
        setLayerType(LAYER_TYPE_SOFTWARE, paint)
        addView(iconImageView)
        iconImageView.updateLayoutParams<LayoutParams> {
            gravity = Gravity.CENTER
        }
        update()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val parentWidth = MeasureSpec.getSize(widthMeasureSpec) / 3
        val parentHeight = MeasureSpec.getSize(heightMeasureSpec) / 3
        measureChildren(MeasureSpec.makeMeasureSpec(parentWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(parentHeight, MeasureSpec.EXACTLY))
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val offset = SHADOW_RADIUS
        ovalRectF.set(offset, offset, width.toFloat() - offset, height - offset)
        val cornerRadius = height / 2f
        val radii = List(8) { cornerRadius }.toFloatArray()
        path.reset()
        path.addRoundRect(ovalRectF, radii, Path.Direction.CW)
        canvas.drawPath(path, paint)
    }

    fun onOpen() {
        animatorProvider?.animate(iconImageView, acton = {
            iconImageView.setImageResource(config.closeIcon)
        })
    }

    fun onClose() {
        animatorProvider?.animate(iconImageView, acton = {
            iconImageView.setImageResource(config.icon)
        })
    }

    private fun update() {
        iconImageView.setImageResource(config.icon)
        iconImageView.tint(config.iconTint)
        paint.color = config.backgroundColor
    }
}