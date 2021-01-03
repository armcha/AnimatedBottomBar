package io.armcha.sample

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import io.armcha.animatedbottombar.item.BottomItem
import com.github.dhaval2404.colorpicker.ColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import io.armcha.animatedbottombar.animator.FadeIconAnimator
import io.armcha.animatedbottombar.animator.RotationIconAnimator
import io.armcha.animatedbottombar.animator.ScaleAndFadeIconAnimator
import io.armcha.animatedbottombar.animator.TranslationXIconAnimator
import io.armcha.animatedbottombar.config.BottomBarConfig
import io.armcha.animatedbottombar.config.DimViewConfig
import io.armcha.animatedbottombar.config.OvalButtonConfig
import io.armcha.animatedbottombar.item.FabItem
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*

class MainActivity : AppCompatActivity() {

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
                selectedItemTint = color(R.color.red),
                unSelectedItemTint = color(R.color.black),
                cornerRadius = 120f, shouldShowTitle = false)
        animatedBottomBar.configBottomBar(bottomBarConfig)

        ovalButtonConfig = OvalButtonConfig(color(R.color.oval_red), color(R.color.white), R.drawable.ic_oval_open,
                R.drawable.ic_oval_close)
        animatedBottomBar.configOval(ovalButtonConfig)

        val dimViewConfig = DimViewConfig(color(R.color.dim_semi_transparent),
                cancelableOnTouchOutside = true)
        animatedBottomBar.configDimView(dimViewConfig)

        animatedBottomBar.addFabItems(
                listOf(FabItem(color(R.color.white), color(R.color.black), R.drawable.calendar),
                        FabItem(color(R.color.white), color(R.color.black), R.drawable.cube),
                        FabItem(color(R.color.white), color(R.color.black), R.drawable.book_open_variant)))

        animatedBottomBar.addBottomItems(BottomItem(R.drawable.book_open_variant, "Library"),
                BottomItem(R.drawable.at, "Email"),
                BottomItem(R.drawable.bell_outline, "Notification"),
                BottomItem(R.drawable.account_circle, "Account"))

        animatedBottomBar.onFabClick { index ->
//            Toast.makeText(this, "Clicked at index $index", Toast.LENGTH_SHORT).show()
        }

        animatedBottomBar.onBottomItemClick { index ->
//            Toast.makeText(this, "Clicked at index $index", Toast.LENGTH_SHORT).show()
        }

        animatedBottomBar.subMenuAnimationDuration = 500
    }

    private fun setUpViews() {
        fun getTitle() = if (!bottomBarConfig.shouldShowTitle) "Show title" else "Hide title"

        shouldShowTitleButton.text = getTitle()
        shouldShowTitleButton.setOnClickListener {
            bottomBarConfig = bottomBarConfig.copy(shouldShowTitle = !bottomBarConfig.shouldShowTitle)
            animatedBottomBar.configBottomBar(bottomBarConfig)
            shouldShowTitleButton.text = getTitle()
        }

        menuItemChangeButton.setOnClickListener {
            if (isFourItems) {
                isFourItems = false
                animatedBottomBar.addBottomItems(BottomItem(R.drawable.book_open_variant, "Library"),
                        BottomItem(R.drawable.at, "Email"))
            } else {
                isFourItems = true
                animatedBottomBar.addBottomItems(BottomItem(R.drawable.book_open_variant, "Library"),
                        BottomItem(R.drawable.at, "Email"),
                        BottomItem(R.drawable.bell_outline, "Notification"),
                        BottomItem(R.drawable.account_circle, "Account"))
            }
        }

        val animators = listOf(
                RotationIconAnimator(), CustomIconAnimator(), FadeIconAnimator(),
                ScaleAndFadeIconAnimator(), TranslationXIconAnimator())

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1,
                animators.map { it::class.java.simpleName })
        val autoCompleteTextView = animationsDropDown.autoCompleteTextView
        autoCompleteTextView.setAdapter(adapter)
        autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            val animator = animators[position]
            if (bottomItemsAnimation.isChecked) {
                animatedBottomBar.setBottomItemIconAnimator(animator)
            } else {
                animatedBottomBar.setOvalButtonIconAnimator(animator)
            }
        }

        cornerRadiusSlider.value = bottomBarConfig.cornerRadius
        cornerRadiusSlider.setLabelFormatter {
            "Corner radius ${it.toInt()}"
        }
        cornerRadiusSlider.addOnChangeListener { _, value, _ ->
            bottomBarConfig = bottomBarConfig.copy(cornerRadius = value)
            animatedBottomBar.configBottomBar(bottomBarConfig)
        }

        changeBackgroundColorButton.setOnClickListener {
            showColorPicker {
                bottomBarConfig = bottomBarConfig.copy(backgroundColor = it)
                animatedBottomBar.configBottomBar(bottomBarConfig)
            }
        }

        changeOvalColorButton.setOnClickListener {
            showColorPicker {
                ovalButtonConfig = ovalButtonConfig.copy(backgroundColor = it)
                animatedBottomBar.configOval(ovalButtonConfig)
            }
        }
    }

    private fun showColorPicker(onColorPicked: (color: Int) -> Unit) {
        ColorPickerDialog
                .Builder(this)
                .setColorShape(ColorShape.CIRCLE)
                .setDefaultColor(color(R.color.white))
                .setColorListener { color, _ ->
                    onColorPicked(color)
                }
                .show()
    }

    private fun color(color: Int) = ContextCompat.getColor(this, color)
}