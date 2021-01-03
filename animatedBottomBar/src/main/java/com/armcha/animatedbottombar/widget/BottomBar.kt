package com.armcha.animatedbottombar.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Space
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import com.armcha.animatedbottombar.BottomItem
import com.armcha.animatedbottombar.R
import com.armcha.animatedbottombar.animator.base.AnimatorProvider
import com.armcha.animatedbottombar.colorFrom
import com.armcha.animatedbottombar.config.BottomBarConfig
import com.armcha.animatedbottombar.tint
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
        setShadowLayer(DEFAULT_SHADOW_RADIUS, 0f, 0f, ContextCompat.getColor(context, R.color.bottom_bar_shadow_color))
    }

    private var itemClickListener: (Int) -> Unit = {}
    var animatorProvider: AnimatorProvider? = null

    var currentSelectedIndex = 0
        private set

    var config = BottomBarConfig(colorFrom(android.R.color.white), colorFrom(R.color.oval_red),
            colorFrom(android.R.color.black),
            DEFAULT_CORNER_RADIUS, false)
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

        paint.color = config.backgroundColor
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

    fun onItemClick(body: (index: Int) -> Unit) {
        itemClickListener = body
    }

    fun addItems(items: List<BottomItem>) {
        removeAllViews()
        bottomViews.clear()
        currentSelectedIndex = 0
        val (centreIndex, itemWeight) = if (items.size <= 2) 1 to 2f else 2 to 1f
        var currentIndex = 0
        repeat(items.size + 1) {
            if (it == centreIndex) {
                val space = Space(context)
                val params = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f)
                addView(space, params)
            } else {
                val view = inflate(context, R.layout.bottom_bar_item, null) as ConstraintLayout
                val params = LayoutParams(0, LayoutParams.MATCH_PARENT, itemWeight)
                val isCurrent = currentIndex == currentSelectedIndex
                val color = if (isCurrent) config.selectedItemTint else config.unSelectedItemTint
                view.iconImageView.tint(color)
                view.title.setTextColor(color)

                val item = items[currentIndex++]
                view.iconImageView.setImageResource(item.icon)
                view.title.text = item.title
                view.title.isVisible = (isCurrent || config.shouldShowTitle) && item.title != null
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
        oldSelected.title.isVisible = config.shouldShowTitle

        animatorProvider?.animate(currentSelected) {
            currentSelected.layoutTransition.setDuration(it.duration)
            val selectedTint = config.selectedItemTint
            currentSelected.iconImageView.tint(selectedTint)
            currentSelected.title.setTextColor(selectedTint)
            currentSelected.title.isVisible = oldSelected.title.text.isNotEmpty()
        }
        animatorProvider?.animate(oldSelected) {
            val unSelectedTint = config.unSelectedItemTint
            oldSelected.iconImageView.tint(unSelectedTint)
            oldSelected.title.setTextColor(unSelectedTint)
        }
    }

    internal fun enable() {
        bottomViews.forEach {
            it.isClickable = true
        }
    }

    internal fun disable() {
        bottomViews.forEach {
            it.isClickable = false
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
            val isCurrentIndex = index == currentSelectedIndex
            val color = if (isCurrentIndex) config.selectedItemTint else config.unSelectedItemTint
            viewGroup.iconImageView.tint(color)
            viewGroup.title.setTextColor(color)
            viewGroup.title.isVisible = isCurrentIndex || config.shouldShowTitle
            paint.color = config.backgroundColor
        }
    }
}