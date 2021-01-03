package io.armcha.animatedbottombar.widget

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
import io.armcha.animatedbottombar.R
import io.armcha.animatedbottombar.animator.base.AnimatorProvider
import io.armcha.animatedbottombar.colorFrom
import io.armcha.animatedbottombar.config.OvalButtonConfig
import io.armcha.animatedbottombar.tint

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
        setShadowLayer(SHADOW_RADIUS, 0f, 0f, ContextCompat.getColor(context, R.color.bottom_bar_shadow_color))
    }
    var animatorProvider: AnimatorProvider? = null

    var config = OvalButtonConfig(colorFrom(R.color.oval_red), colorFrom(android.R.color.white), R.drawable.ic_oval_open,
            R.drawable.ic_oval_close)
        set(value) {
            field = value
            update()
            invalidate()
        }

    init {
        setWillNotDraw(false)
        setLayerType(LAYER_TYPE_SOFTWARE, paint)
        val iconSize = resources.getDimension(R.dimen.oval_button_icon).toInt()
        val iconParams = LayoutParams(iconSize, iconSize)
        addView(iconImageView, iconParams)
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