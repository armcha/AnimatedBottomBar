package com.armcha.animatedbottombar.widget

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewPropertyAnimator
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.armcha.animatedbottombar.BottomItem
import com.armcha.animatedbottombar.FabItem
import com.armcha.animatedbottombar.R
import com.armcha.animatedbottombar.animator.ScaleAndFadeIconAnimator
import com.armcha.animatedbottombar.animator.base.IconAnimator
import com.armcha.animatedbottombar.animator.base.AnimatorProvider
import com.armcha.animatedbottombar.colorFrom
import com.armcha.animatedbottombar.config.BottomBarConfig
import com.armcha.animatedbottombar.config.DimViewConfig
import com.armcha.animatedbottombar.config.OvalButtonConfig
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AnimatedBottomBar(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {

    private val bottomBarHeight by lazy { resources.getDimension(R.dimen.bottom_bar_height) }
    private val overshootInterpolator = OvershootInterpolator(1.5f)
    private val bottomItemAnimatorProvider = AnimatorProvider(ScaleAndFadeIconAnimator())
    private val ovalButtonAnimatorProvider = AnimatorProvider(ScaleAndFadeIconAnimator())
    private val ovalButton by lazy { OvalButton(context) }
    private val bottomBar by lazy { BottomBar(context) }
    private val dimView by lazy { View(context) }
    private val fabs = mutableListOf<View>()
    private var fabClickListener: (Int) -> Unit = {}
    private var dimConfig = DimViewConfig(colorFrom(R.color.bottom_bar_dim_color),
            cancelableOnTouchOutside = true)
        set(value) {
            field = value
            updateDimView()
        }
    private var isOpened = false

    var subMenuAnimationDuration = 500L

    init {
        setUpBottomBar()
        setUpOvalButton()
        setUpDimView()
    }

    fun configOval(ovalButtonConfig: OvalButtonConfig) {
        ovalButton.config = ovalButtonConfig
    }

    fun addBottomItems(firstItem: BottomItem, secondItem: BottomItem) {
        bottomBar.addItems(listOf(firstItem, secondItem))
    }

    fun addBottomItems(firstItem: BottomItem, secondItem: BottomItem,
                       thirdItem: BottomItem, fourthItem: BottomItem) {
        bottomBar.addItems(listOf(firstItem, secondItem, thirdItem, fourthItem))
    }

    fun configBottomBar(bottomBarConfig: BottomBarConfig) {
        bottomBar.config = bottomBarConfig
    }

    fun configDimView(dimViewConfig: DimViewConfig) {
        dimConfig = dimViewConfig
    }

    fun setBottomItemIconAnimator(iconAnimator: IconAnimator) {
        bottomItemAnimatorProvider.iconAnimator = iconAnimator
    }

    fun setOvalButtonIconAnimator(iconAnimator: IconAnimator) {
        ovalButtonAnimatorProvider.iconAnimator = iconAnimator
    }

    fun onFabClick(body: (index: Int) -> Unit) {
        fabClickListener = body
    }

    fun onBottomItemClick(body: (index: Int) -> Unit) {
        bottomBar.onItemClick(body)
    }

    fun selectIndex(index: Int) {
        bottomBar.selectIndex(index)
    }

    fun addFabItems(fabItemList: List<FabItem>) {
        if (fabItemList.size < 2 || fabItemList.size > 3)
            throw ArrayIndexOutOfBoundsException("Item size should be between [2,3]")

        val fabRadius = resources.getDimension(R.dimen.float_menu_button_radius).toInt()
        fabItemList.forEach {
            val fab = FloatingActionButton(context)
            with(fab) {
                backgroundTintList = ColorStateList.valueOf(it.color)
                setImageResource(it.icon)
                imageTintList = ColorStateList.valueOf(it.iconTint)
                translationZ = 25f
                elevation = 30f
                compatElevation = 15f
                customSize = fabRadius
                val fabParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
                        Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL)
                fabParams.bottomMargin = fabRadius / 3
                addView(this, fabParams)
                fabs.add(this)
                configClickListeners()
                isInvisible = true
            }
        }
    }

    fun open() {
        dimView.animate().cancel()
        dimView.animate()
                .alpha(1f)
                .setDuration((subMenuAnimationDuration * 0.5).toLong())
                .withStartAction { dimView.isVisible = true }
                .start()

        fabs.forEachIndexed { index, fab ->
            fab.isInvisible = false
            fab.animate().cancel()
            val animator = fab.animate()
                    .setDuration(subMenuAnimationDuration)
                    .setInterpolator(overshootInterpolator)

            val heightFactor = fab.height * 0.65
            val (translationY, translationX) = when (index) {
                0 -> -ovalButton.height + heightFactor to fab.width * -1.5f
                1 -> if (fabs.size == 2) {
                    -ovalButton.height + heightFactor to fab.width * 1.5f
                } else {
                    -ovalButton.height - heightFactor to 0
                }
                else -> -ovalButton.height + heightFactor to fab.width * 1.5f
            }
            animator.translationY(translationY.toFloat())
            animator.setUpdateListener {
                val value = it.animatedValue
                if (value is Float && value > 0.5) {
                    animator.translationX(translationX.toFloat())
                            .setUpdateListener(null)
                            .start()
                }
            }
            animator.start()
        }
        val barHeight = bottomBar.height.toFloat()
        ovalButton.animate().cancel()
        ovalButton.animate()
                .setDuration((subMenuAnimationDuration * 0.75).toLong())
                .translationY(-barHeight * 0.45f)
                .setInterpolator(overshootInterpolator)
                .start()

        bottomBar.animate().cancel()
        bottomBar.animate()
                .setStartDelay(0)
                .setDuration((subMenuAnimationDuration * 0.75).toLong())
                .scaleY(0.95f)
                .scaleX(0.95f)
                .setInterpolator(overshootInterpolator)
                .translationY(barHeight - barHeight * 0.3f)
                .start()
        ovalButton.onOpen()
        bottomBar.disable()
        isOpened = true
    }

    fun close() {
        resetViewState(dimView)
                .alpha(0f)
                .setDuration((subMenuAnimationDuration * 0.5).toLong())
                .withEndAction {
                    dimView.isVisible = false
                }
                .start()
        fabs.forEachIndexed { index, fab ->
            val animator = fab.animate()
                    .translationX(0f)
                    .setDuration((subMenuAnimationDuration * 1.5).toLong())
                    .withEndAction {
                        fab.isInvisible = true
                    }

            if (fabs.size == 3 && index == 1) {
                animator.translationY(0f)
            } else {
                animator.setInterpolator(LinearInterpolator())
                        .setDuration((subMenuAnimationDuration / 4))
                        .setUpdateListener {
                            val value = it.animatedValue
                            if (value is Float && value > .6) {
                                animator.translationY(0f)
                                        .setUpdateListener(null)
                                        .start()
                            }
                        }
                resetViewState(ovalButton)
                        .setStartDelay(subMenuAnimationDuration / 4)
                        .start()
            }
            animator.start()
        }

        resetViewState(bottomBar)
                .setStartDelay(subMenuAnimationDuration / 4)
                .start()

        ovalButton.onClose()
        bottomBar.enable()
        isOpened = false
    }

    private fun configClickListeners() {
        fabs.forEachIndexed { index, viewGroup ->
            viewGroup.setOnClickListener {
                fabClickListener(index)
            }
        }
    }

    private fun setUpBottomBar() {
        bottomBar.animatorProvider = bottomItemAnimatorProvider

        val params = LayoutParams(LayoutParams.MATCH_PARENT, bottomBarHeight.toInt(), Gravity.BOTTOM)
        addView(bottomBar, params)
        bottomBar.elevation = 11f
        bottomBar.translationZ = 11f
    }

    private fun setUpOvalButton() {
        val ovalWidth = bottomBarHeight * 0.95
        val ovalHeight = bottomBarHeight * 1.5
        val ovalParams = LayoutParams(ovalWidth.toInt(), ovalHeight.toInt(),
                Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL)
        addView(ovalButton, ovalParams)
        ovalButton.animatorProvider = ovalButtonAnimatorProvider
        ovalButton.elevation = 12f
        ovalButton.translationZ = 12f
        ovalButton.setOnClickListener {
            if (isOpened) close() else open()
        }
    }

    private fun setUpDimView() {
        val dimParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        dimView.alpha = 0f
        dimView.isClickable = true
        dimView.elevation = 12f
        dimView.isVisible = false
        dimView.setBackgroundColor(dimConfig.dimColor)
        addView(dimView, dimParams)
    }

    private fun updateDimView() {
        dimView.setBackgroundColor(dimConfig.dimColor)
        if (dimConfig.cancelableOnTouchOutside) {
            dimView.setOnClickListener { close() }
        } else {
            dimView.setOnClickListener(null)
        }
    }

    private fun resetViewState(view: View): ViewPropertyAnimator {
        return view.animate()
                .setDuration((subMenuAnimationDuration * 0.75).toLong())
                .scaleY(1.0f)
                .scaleX(1.0f)
                .translationY(0f)
                .translationX(0f)
    }
}