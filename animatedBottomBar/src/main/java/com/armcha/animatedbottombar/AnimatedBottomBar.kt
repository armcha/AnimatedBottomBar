package com.armcha.animatedbottombar

import android.animation.LayoutTransition
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.armcha.animatedbottombar.R

class AnimatedBottomBar(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {

    private var animatorProvider: AnimatorProvider? = AnimatorProvider(DefaultAnimator())
    private val fabs = mutableListOf<View>()

    var animator: Animator? = null
        set(value) {
            field = value
            animatorProvider = if (value != null) AnimatorProvider(value) else null
        }

    init {
        val fabRadius = resources.getDimension(R.dimen.float_menu_button_radius).toInt()
        repeat(3) {
            val fab = FloatingActionButton(context)
            fab.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
            fab.setImageResource(R.drawable.bell_outline)
            val fabParams = LayoutParams(fabRadius, fabRadius, Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL)
            fabParams.bottomMargin = fabRadius / 3
            addView(fab, fabParams)
            fabs.add(fab)
            fab.translationZ = 25f
            fab.elevation = 30f
            fab.compatElevation = 15f
            fab.setOnClickListener {
                log {
                    "CLIECK $it"
                }
            }
        }

        val bottomBar = BottomBar(context, attrs)
        bottomBar.animatorProvider = animatorProvider
        val bottomBarHeight = resources.getDimension(R.dimen.bottom_bar_height)
        val params = LayoutParams(LayoutParams.MATCH_PARENT, bottomBarHeight.toInt(), Gravity.BOTTOM)
        addView(bottomBar, params)
        bottomBar.elevation = 11f
        bottomBar.translationZ = 11f
        val items = mutableListOf<BottomItem>()
        items += BottomItem(R.drawable.bell_outline, "TITLE 1")
        items += BottomItem(R.drawable.bell_outline, "TITLE 2")
        items += BottomItem(R.drawable.bell_outline, "TITLE 3")
        items += BottomItem(R.drawable.bell_outline, "TITLE 4")
        bottomBar.addItems(items)

        val bottomBarOval = BottomBarOval(context)
        val ovalWidth = bottomBarHeight * 0.95
        val ovalHeight = bottomBarHeight * 1.5
        val ovalParams = LayoutParams(
                ovalWidth.toInt(),
                ovalHeight.toInt(),
                Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        )
        addView(bottomBarOval, ovalParams)

        val dimView = View(context)
        val dimParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        dimView.alpha = 0f
        dimView.setBackgroundColor(ContextCompat.getColor(context, R.color.dim_color))
        addView(dimView, dimParams)

        bottomBarOval.elevation = 12f
        bottomBarOval.translationZ = 12f

        val overshootInterpolator = OvershootInterpolator(1.5f)
        bottomBarOval.setOnClickListener { view ->
            if (view.translationY < 0) {
                dimView.animate()
                        .alpha(0f)
                        .setDuration(200)
                        .start()
                fabs.forEach {
                    it.animate()
                            .setDuration(300)
                            .translationY(0f)
                            .translationX(0f)
                            .withLayer()
                            .start()
                }

                view.animate()
                        .setDuration(300)
                        .translationY(0f)
                        .setInterpolator(overshootInterpolator)
                        .start()

                bottomBar.animate()
                        .setDuration(300)
                        .translationY(0f)
                        .scaleY(1.0f)
                        .setInterpolator(overshootInterpolator)
                        .scaleX(1.0f)
                        .start()
                bottomBarOval.onClose()
            } else {
                dimView.animate()
                        .alpha(1f)
                        .setDuration(200)
                        .start()
                fabs.forEachIndexed { index, fab ->
                    val animator = fab.animate()
                            .setDuration(300)
                            .setInterpolator(overshootInterpolator)

                    if (index == 0) {
                        animator.translationX(-(bottomBarOval.width.toFloat()).toFloat())
                        animator.translationY(-(bottomBarOval.height - fab.height).toFloat())
                    }
                    if (index == 1) {
                        animator.translationY(-(bottomBarOval.height + fab.height / 2).toFloat())
                    }
                    if (index == 2) {
                        animator.translationX(bottomBarOval.width.toFloat())
                        animator.translationY(-(bottomBarOval.height - fab.height).toFloat())
                    }

                    animator.start()
                }
                val barHeight = bottomBar.height.toFloat()
                view.animate()
                        .setDuration(300)
                        .translationY(-barHeight * 0.45f)
                        .setInterpolator(overshootInterpolator)
                        .start()

                bottomBar.animate()
                        .setDuration(300)
                        .scaleY(0.95f)
                        .scaleX(0.95f)
                        .setInterpolator(overshootInterpolator)
                        .translationY(barHeight - barHeight * 0.3f)
                        .start()
                bottomBarOval.onOpen()
            }
        }
    }
}