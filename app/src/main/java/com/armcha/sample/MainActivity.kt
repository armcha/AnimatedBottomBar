package com.armcha.sample

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import com.armcha.animatedbottombar.*
import com.armcha.animatedbottombar.animator.FadeAnimator
import com.armcha.animatedbottombar.animator.RotationAnimator
import com.armcha.animatedbottombar.animator.ScaleAndFadeAnimator
import com.armcha.animatedbottombar.animator.TranslationXAnimator
import com.armcha.animatedbottombar.config.BottomBarConfig
import com.armcha.animatedbottombar.config.OvalButtonConfig
import com.github.dhaval2404.colorpicker.ColorPickerDialog
import com.github.dhaval2404.colorpicker.MaterialColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import com.github.dhaval2404.colorpicker.model.ColorSwatch
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*

class MainActivity : AppCompatActivity() {

    private val fourItems = listOf(
            BottomItem(R.drawable.at, "TITLE 1"),
            BottomItem(R.drawable.at, "TITLE 2"),
            BottomItem(R.drawable.at, "TITLE 3"),
            BottomItem(R.drawable.at, "TITLE 4"))

    private val twoItems = fourItems.subList(0, 2)
    private lateinit var bottomBarConfig: BottomBarConfig
    private lateinit var ovalButtonConfig: OvalButtonConfig
    private var isFourItems = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setUpBottomBar()

        setUpViews()
    }

    private fun setUpBottomBar() {
        bottomBarConfig = BottomBarConfig(
                backgroundColor = color(R.color.white),
                selectedItemTint = color(R.color.purple_500),
                unSelectedItemTint = color(R.color.black),
                cornerRadius = 50f, shouldShowTitle = false)
        animatedBottomBar.configBottomBar(bottomBarConfig)

        animatedBottomBar.addFabItems(
                FabItem(color(R.color.white), color(R.color.black), R.drawable.at),
                FabItem(color(R.color.white), color(R.color.black), R.drawable.at),
                FabItem(color(R.color.white), color(R.color.black), R.drawable.at))

        animatedBottomBar.addBottomItems(fourItems)

        ovalButtonConfig = OvalButtonConfig(color(R.color.white), color(R.color.black), R.drawable.at, R.drawable.bell_outline)
        animatedBottomBar.configOval(ovalButtonConfig)

        animatedBottomBar.onFabClick { index ->

        }

        animatedBottomBar.onBottomItemClick { index ->

        }

        animatedBottomBar.subMenuAnimationDuration = 500
    }

    private fun setUpViews() {
        shouldShowTitleButton.text = "Should show title = ${bottomBarConfig.shouldShowTitle}"
        shouldShowTitleButton.setOnClickListener {
            bottomBarConfig = bottomBarConfig.copy(shouldShowTitle = !bottomBarConfig.shouldShowTitle)
            animatedBottomBar.configBottomBar(bottomBarConfig)
            shouldShowTitleButton.text = "Should show title = ${bottomBarConfig.shouldShowTitle}"
        }

        menuItemChangeButton.setOnClickListener {
            if (isFourItems) {
                isFourItems = false
                animatedBottomBar.addBottomItems(twoItems)
            } else {
                isFourItems = true
                animatedBottomBar.addBottomItems(fourItems)
            }
        }

        val animators = listOf(
                RotationAnimator(), CustomAnimator(), FadeAnimator(),
                ScaleAndFadeAnimator(), TranslationXAnimator())

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1,
                animators.map { it::class.java.simpleName })
        val autoCompleteTextView = animationsDropDown.autoCompleteTextView
        autoCompleteTextView.setAdapter(adapter)
        autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            val animator = animators[position]
            if (bottomItemsAnimation.isChecked) {
                animatedBottomBar.setBottomItemAnimator(animator)
            } else {
                animatedBottomBar.setOvalButtonAnimator(animator)
            }
        }

        cornerRadiusSlider.value = bottomBarConfig.cornerRadius
        cornerRadiusSlider.addOnChangeListener { _, value, _ ->
            bottomBarConfig = bottomBarConfig.copy(cornerRadius = value)
            animatedBottomBar.configBottomBar(bottomBarConfig)
        }

        changeBackgroundColorButton.setOnClickListener {
            showColorPicker{
                bottomBarConfig = bottomBarConfig.copy(backgroundColor = it)
                animatedBottomBar.configBottomBar(bottomBarConfig)
            }
        }

        changeOvalColorButton.setOnClickListener {
            showColorPicker{
                ovalButtonConfig = ovalButtonConfig.copy(backgroundColor = it)
                animatedBottomBar.configOval(ovalButtonConfig)
            }
        }
    }

    private fun showColorPicker(onColorPicked: (color: Int) -> Unit) {
        ColorPickerDialog
                .Builder(this)
                .setColorShape(ColorShape.CIRCLE)
                .setDefaultColor(Color.WHITE)
                .setColorListener { color, _ ->
                    onColorPicked(color)
                }
                .show()
    }

    private fun color(color: Int) = ContextCompat.getColor(this, color)
}