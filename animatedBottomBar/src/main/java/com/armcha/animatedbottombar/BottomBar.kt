package com.armcha.animatedbottombar

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Space
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding

internal class BottomBar(context: Context, attrs: AttributeSet? = null) : LinearLayout(context, attrs) {

    companion object {

        private const val SHADOW_RADIUS = 40f
        private const val DEFAULT_CORNER_RADIUS = 120f
    }

    private var itemClickListener: (Int) -> Unit = {}
    private val path = Path()
    private val rectF = RectF()
    private val paint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.FILL
        strokeWidth = 1f
        isAntiAlias = true
        setShadowLayer(SHADOW_RADIUS, 0f, 0f, ContextCompat.getColor(context, R.color.gray_600))
    }
//    private var config = BottomBarConfig(
//        R.color.teal_200, R.drawable.bell_outline,
//        android.R.drawable.ic_menu_close_clear_cancel)
//        set(value) {
//            field = value
////            update()
//            invalidate()
//        }


    init {
        setLayerType(LAYER_TYPE_SOFTWARE, paint)
        setWillNotDraw(false)
        orientation = HORIZONTAL
        gravity = Gravity.CENTER
        weightSum = 5f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        rectF.set(
            SHADOW_RADIUS / 2,
            SHADOW_RADIUS,
            width.toFloat() - SHADOW_RADIUS / 2,
            height.toFloat() - paint.strokeWidth / 2
        )
        val cornerRadius = SHADOW_RADIUS * 3
        path.addRoundRect(
            rectF,
            floatArrayOf(cornerRadius, cornerRadius, cornerRadius, cornerRadius, 0f, 0f, 0f, 0f),
            Path.Direction.CW
        )
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
                val view = inflate(context, R.layout.bottom_bar_item, null)
                val params = LayoutParams(0, 220, itemWeight)
                val icon = view.findViewById<ImageView>(R.id.iconImageView)
                val title = view.findViewById<TextView>(R.id.title)
                view.setOnClickListener { itemClickListener(currentIndex) }
                val item = items[currentIndex++]
                icon.setImageResource(item.icon)
                title.isVisible = item.title != null
                title.text = item.title
                if(it==0) {
//                    params.marginStart = 30
                    view.updatePadding(20,20,0,0)
//                    view.setBackgroundColor(Color.RED)
                }
//                else
//                view.setBackgroundColor(Color.BLUE)

                addView(view, params)
            }
        }
    }
}