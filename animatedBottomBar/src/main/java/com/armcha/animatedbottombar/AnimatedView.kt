package com.armcha.animatedbottombar

import android.view.View
import androidx.interpolator.view.animation.FastOutSlowInInterpolator


interface AnimatedView {

    fun <V : View> animate(view: V, duration: Long = 200,
                           endAction: () -> Unit = {},
                           acton: V.() -> Unit) {
        val scaleFactor = 0.75f
        with(view) {
            clearAnimation()
            animate()
                    .alpha(0f)
                    .scaleX(scaleFactor)
                    .setDuration(duration)
                    .setInterpolator(FastOutSlowInInterpolator())
                    .withEndAction {
                        acton(view)
                        scaleX = scaleFactor
                        animate()
                                .scaleX(1f)
                                .alpha(1f)
                                .setListener(null)
                                .setDuration(duration)
                                .withEndAction { endAction() }
                                .start()
                    }
                    .start()
        }
    }
}