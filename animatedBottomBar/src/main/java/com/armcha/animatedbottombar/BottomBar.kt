package com.armcha.animatedbottombar

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Space
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import com.armcha.animatedbottombar.animator.AnimatorProvider
import kotlinx.android.synthetic.main.bottom_bar_item.view.*

internal class BottomBar(context: Context, attrs: AttributeSet? = null) : LinearLayout(context, attrs) {

    companion object {

        private const val DEFAULT_SHADOW_RADIUS = 40f
        private const val DEFAULT_CORNER_RADIUS = 120f
    }

    private val bottomViews = mutableListOf<ViewGroup>()
    private val path = Path()
    private val rectF = RectF()
    private val paint = Paint().apply {
        style = Paint.Style.FILL
        strokeWidth = 1f
        isAntiAlias = true
        setShadowLayer(DEFAULT_SHADOW_RADIUS, 0f, 0f, ContextCompat.getColor(context, R.color.gray_600))
    }

    private var itemClickListener: (Int) -> Unit = {}
    var animatorProvider: AnimatorProvider? = null

    var currentSelectedIndex = 0
        private set

    var config = BottomBarConfig(R.color.white, R.color.purple_500, R.color.gray_600,
            DEFAULT_CORNER_RADIUS)
        set(value) {
            field = value
            update()
            invalidate()
        }

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, paint)
        setWillNotDraw(false)
        orientation = HORIZONTAL
        gravity = Gravity.CENTER
        weightSum = 5f

        paint.color = colorFrom(config.backgroundColor)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        rectF.set(0f, DEFAULT_SHADOW_RADIUS, width.toFloat(), height.toFloat())
        val cornerRadius = config.cornerRadius
        path.reset()
        path.addRoundRect(rectF,
                floatArrayOf(cornerRadius, cornerRadius, cornerRadius, cornerRadius, 0f, 0f, 0f, 0f),
                Path.Direction.CW)
        path.close()

        canvas.drawPath(path, paint)
    }

    fun onItemClick(body: (Int) -> Unit) {
        itemClickListener = body
    }

    fun addItems(items: List<BottomItem>) {
        val (centreIndex, itemWeight) = if (items.size <= 2) 1 to 2f else 2 to 1f
        var currentIndex = 0
        repeat(items.size + 1) {
            if (it == centreIndex) {
                val space = Space(context)
                val params = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f)
                addView(space, params)
            } else {
                val view = inflate(context, R.layout.bottom_bar_item, null) as ConstraintLayout
                view.layoutTransition.setDuration(100)
                val params = LayoutParams(0, LayoutParams.MATCH_PARENT, itemWeight)
                val isCurrent = currentIndex == currentSelectedIndex
                val color = if (isCurrent) config.selectedTint else config.unSelectedTint
                view.iconImageView.tint(color)
                view.title.setTextColor(colorFrom(color))

                val item = items[currentIndex++]
                view.iconImageView.setImageResource(item.icon)
                view.title.text = item.title
                view.title.isVisible = isCurrent && item.title != null
                if (items.size > 2) {
                    when (it) {
                        0 -> view.updatePadding(view.paddingTop, view.paddingTop, 0, 0)
                        4 -> view.updatePadding(0, view.paddingTop, view.paddingTop, 0)
                    }
                }
                addView(view, params)
                bottomViews += view
                configClickListeners()
            }
        }
    }

    fun selectIndex(index: Int) {
        if (index < 0 || index > bottomViews.size) {
            throw ArrayIndexOutOfBoundsException("Index should be between 0 and ${bottomViews.size} ")
        }
        if (currentSelectedIndex == index) {
            return
        }
        itemClickListener(index)
        val oldSelectedIndex = currentSelectedIndex
        currentSelectedIndex = index
        val oldSelected = bottomViews[oldSelectedIndex]
        val currentSelected = bottomViews[currentSelectedIndex]
        currentSelected.title.isVisible = oldSelected.title.text.isNotEmpty()
        oldSelected.title.isVisible = false

        animatorProvider?.animate(currentSelected) {
            val selectedTint = config.selectedTint
            currentSelected.iconImageView.tint(selectedTint)
            currentSelected.title.setTextColor(ContextCompat.getColor(context, selectedTint))
        }
        animatorProvider?.animate(oldSelected) {
            val unSelectedTint = config.unSelectedTint
            oldSelected.iconImageView.tint(unSelectedTint)
            oldSelected.title.setTextColor(colorFrom(unSelectedTint))
        }
    }

    private fun configClickListeners() {
        bottomViews.forEachIndexed { index, viewGroup ->
            viewGroup.setOnClickListener {
                selectIndex(index)
            }
        }
    }

    private fun update() {
        bottomViews.forEachIndexed { index, viewGroup ->
            val color = if (index == currentSelectedIndex)
                config.selectedTint else config.unSelectedTint
            viewGroup.iconImageView.tint(color)
            viewGroup.title.setTextColor(colorFrom(color))
            paint.color = colorFrom(config.backgroundColor)
        }
    }
}