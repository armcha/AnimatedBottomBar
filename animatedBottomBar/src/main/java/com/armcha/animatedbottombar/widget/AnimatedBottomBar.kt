package com.armcha.animatedbottombar.widget

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewPropertyAnimator
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.armcha.animatedbottombar.BottomItem
import com.armcha.animatedbottombar.FabItem
import com.armcha.animatedbottombar.R
import com.armcha.animatedbottombar.animator.base.Animator
import com.armcha.animatedbottombar.animator.base.AnimatorProvider
import com.armcha.animatedbottombar.animator.ScaleAndFadeAnimator
import com.armcha.animatedbottombar.colorFrom
import com.armcha.animatedbottombar.config.BottomBarConfig
import com.armcha.animatedbottombar.config.OvalButtonConfig
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AnimatedBottomBar(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {

    private val bottomBarHeight by lazy { resources.getDimension(R.dimen.bottom_bar_height) }
    private val overshootInterpolator = OvershootInterpolator(1.5f)
    private val bottomItemAnimatorProvider = AnimatorProvider(ScaleAndFadeAnimator())
    private val ovalButtonAnimatorProvider = AnimatorProvider(ScaleAndFadeAnimator())
    private val ovalButton by lazy { OvalButton(context) }
    private val bottomBar by lazy { BottomBar(context) }

    private val dimView by lazy { View(context) }
    private val fabs = mutableListOf<View>()
    private var fabClickListener: (Int) -> Unit = {}

    var isOpened = false
    var subMenuAnimationDuration = 300L

    init {
        setUpBottomBar()
        setUpOvalButton()
        setUpDimView()
    }

    private fun addFabItems(fabItemList: List<FabItem>) {
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
            }
        }
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
        with(dimView) {
            alpha = 0f
            isClickable = true
            elevation = 12f
            isVisible = false
            setBackgroundColor(colorFrom(R.color.dim_color))
            addView(this, dimParams)
        }
    }

    fun addFabItems(firstItem: FabItem, secondItem: FabItem) {
        addFabItems(listOf(firstItem, secondItem))
    }

    fun addFabItems(firstItem: FabItem, secondItem: FabItem, thirdItem: FabItem) {
        addFabItems(listOf(firstItem, secondItem, thirdItem))
    }

    fun configOval(ovalButtonConfig: OvalButtonConfig) {
        ovalButton.config = ovalButtonConfig
    }

    fun addBottomItems(bottomItems: List<BottomItem>) {
        bottomBar.addItems(bottomItems)
    }

    fun configBottomBar(bottomBarConfig: BottomBarConfig) {
        bottomBar.config = bottomBarConfig
    }

    fun setBottomItemAnimator(animator: Animator) {
        bottomItemAnimatorProvider.animator = animator
    }

    fun setOvalButtonAnimator(animator: Animator) {
        ovalButtonAnimatorProvider.animator = animator
    }

    fun onFabClick(body: (index: Int) -> Unit) {
        fabClickListener = body
    }

    fun onBottomItemClick(body: (index: Int) -> Unit) {
        bottomBar.onItemClick(body)
    }

    fun open() {
        dimView.animate()
                .alpha(1f)
                .setDuration(200)
                .withStartAction {
                    dimView.isVisible = true
                }
                .start()

        fabs.forEachIndexed { index, fab ->
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
            animator.translationX(translationX.toFloat())
            animator.translationY(translationY.toFloat())
            animator.start()
        }
        val barHeight = bottomBar.height.toFloat()
        ovalButton.animate()
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
        ovalButton.onOpen()
        bottomBar.disable()
        isOpened = true
    }

    fun close() {
        resetViewState(dimView)
                .alpha(0f)
                .setDuration(200)
                .withEndAction {
                    dimView.isVisible = false
                }
                .start()
        fabs.forEach {
            resetViewState(it)
                    .setDuration(subMenuAnimationDuration)
                    .start()
        }

        resetViewState(ovalButton)
                .setInterpolator(overshootInterpolator)
                .start()

        resetViewState(bottomBar).start()

        ovalButton.onClose()
        bottomBar.enable()
        isOpened = false
    }

    private fun resetViewState(view: View): ViewPropertyAnimator {
        return view.animate()
                .setDuration(300)
                .scaleY(1.0f)
                .scaleX(1.0f)
                .translationY(0f)
                .translationX(0f)
    }
}