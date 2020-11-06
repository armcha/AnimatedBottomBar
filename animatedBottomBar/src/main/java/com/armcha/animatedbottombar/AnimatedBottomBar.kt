package com.armcha.animatedbottombar

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.armcha.animatedbottombar.animator.Animator
import com.armcha.animatedbottombar.animator.AnimatorProvider
import com.armcha.animatedbottombar.animator.DefaultAnimator
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AnimatedBottomBar(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {

    private val bottomBarHeight by lazy { resources.getDimension(R.dimen.bottom_bar_height) }
    private val overshootInterpolator = OvershootInterpolator(1.5f)
    private val animatorProvider = AnimatorProvider(DefaultAnimator())
    private val bottomBar by lazy { BottomBar(context) }
    private val dimView by lazy { View(context) }
    private val fabs = mutableListOf<View>()

    init {
        setUpBottomBar()
        setUpOvalButton()
        setUpDimView()
    }

    fun addFabItems(firstItem: FabItem, secondItem: FabItem) {
        addFabItems(listOf(firstItem, secondItem))
    }

    fun addFabItems(firstItem: FabItem, secondItem: FabItem, thirdItem: FabItem) {
        addFabItems(listOf(firstItem, secondItem, thirdItem))
    }

    fun addBottomItems(bottomItems: List<BottomItem>) {
        bottomBar.addItems(bottomItems)
    }

    private fun addFabItems(fabItemList: List<FabItem>) {
        val fabRadius = resources.getDimension(R.dimen.float_menu_button_radius).toInt()
        fabItemList.forEach {
            val fab = FloatingActionButton(context)
            with(fab) {
                backgroundTintList = ColorStateList.valueOf(colorFrom(it.color))
                setImageResource(it.icon)
                imageTintList = ColorStateList.valueOf(colorFrom(it.iconTint))
                translationZ = 25f
                elevation = 30f
                compatElevation = 15f
                customSize = fabRadius
                setOnClickListener {
                    log {
                        "CLIECK $it"
                    }
                }
                val fabParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
                        Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL)
                fabParams.bottomMargin = fabRadius / 3
                addView(this, fabParams)
                fabs.add(this)
            }
        }
    }

    private fun setUpBottomBar() {
        bottomBar.animatorProvider = animatorProvider

        val params = LayoutParams(LayoutParams.MATCH_PARENT, bottomBarHeight.toInt(), Gravity.BOTTOM)
        addView(bottomBar, params)
        bottomBar.elevation = 11f
        bottomBar.translationZ = 11f
    }

    private fun setUpOvalButton() {
        val bottomBarOval = BottomBarOval(context)
        val ovalWidth = bottomBarHeight * 0.95
        val ovalHeight = bottomBarHeight * 1.5
        val ovalParams = LayoutParams(
                ovalWidth.toInt(),
                ovalHeight.toInt(),
                Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL)
        addView(bottomBarOval, ovalParams)
        bottomBarOval.elevation = 12f
        bottomBarOval.translationZ = 12f

        bottomBarOval.setOnClickListener {
            if (bottomBarOval.translationY < 0) {
                dimView.animate()
                        .alpha(0f)
                        .setDuration(200)
                        .start()
                fabs.forEach {
                    it.animate()
                            .setDuration(300)
                            .translationY(0f)
                            .translationX(0f)
                            .start()
                }

                bottomBarOval.animate()
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
                        animator.translationX(-(fab.width.toFloat() * 1.5f))
                        animator.translationY(-(bottomBarOval.height - (fab.height * 0.65)).toFloat())
                    }
                    if (index == 1) {
                        animator.translationY(-(bottomBarOval.height + (fab.height * 0.65)).toFloat())
                    }
                    if (index == 2) {
                        animator.translationX(fab.width.toFloat() * 1.5f)
                        animator.translationY(-(bottomBarOval.height - (fab.height * 0.65)).toFloat())
                    }

                    animator.start()
                }
                val barHeight = bottomBar.height.toFloat()
                bottomBarOval.animate()
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

    private fun setUpDimView() {
        val dimParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        dimView.alpha = 0f
        dimView.isClickable = true
        dimView.setBackgroundColor(colorFrom(R.color.dim_color))
        addView(dimView, dimParams)
    }

    fun setAnimator(animator: Animator) {
        animatorProvider.animator = animator
    }
}