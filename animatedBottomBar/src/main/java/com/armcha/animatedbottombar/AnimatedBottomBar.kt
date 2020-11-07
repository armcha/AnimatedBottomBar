package com.armcha.animatedbottombar

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewPropertyAnimator
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import com.armcha.animatedbottombar.animator.Animator
import com.armcha.animatedbottombar.animator.AnimatorProvider
import com.armcha.animatedbottombar.animator.DefaultAnimator
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AnimatedBottomBar(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {

    private val bottomBarHeight by lazy { resources.getDimension(R.dimen.bottom_bar_height) }
    private val overshootInterpolator = OvershootInterpolator(1.5f)
    private val animatorProvider = AnimatorProvider(DefaultAnimator())
    private val ovalButton by lazy { OvalButton(context) }
    private val bottomBar by lazy { BottomBar(context) }

    private val dimView by lazy { View(context) }
    private val fabs = mutableListOf<View>()
    private var fabClickListener: (Int) -> Unit = {}

    var isOpened = false

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
                backgroundTintList = ColorStateList.valueOf(colorFrom(it.color))
                setImageResource(it.icon)
                imageTintList = ColorStateList.valueOf(colorFrom(it.iconTint))
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
        bottomBar.animatorProvider = animatorProvider

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
        dimView.setBackgroundColor(colorFrom(R.color.dim_color))
        addView(dimView, dimParams)
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

    fun setAnimator(animator: Animator) {
        animatorProvider.animator = animator
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
                .start()

        fabs.forEachIndexed { index, fab ->
            val animator = fab.animate()
                    .setDuration(300)
                    .setInterpolator(overshootInterpolator)

            val translationY = when (index) {
                0 -> {
                    animator.translationX(fab.width * -1.5f)
                    -ovalButton.height + (fab.height * 0.65)
                }
                1 -> {
                    -ovalButton.height - (fab.height * 0.65)
                }
                else -> {
                    animator.translationX(fab.width * 1.5f)
                    -ovalButton.height + (fab.height * 0.65)
                }
            }
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
        isOpened = true
    }

    fun close() {
        resetViewState(dimView)
                .alpha(0f)
                .setDuration(200)
                .start()
        fabs.forEach {
            resetViewState(it).start()
        }

        resetViewState(ovalButton)
                .setInterpolator(overshootInterpolator)
                .start()

        resetViewState(bottomBar).start()

        ovalButton.onClose()
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