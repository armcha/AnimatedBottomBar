package com.armcha.animatedbottombar.animator.base

import android.view.View
import android.view.ViewPropertyAnimator

internal class AnimatorProvider(var iconAnimator: IconAnimator) {

    fun <V : View> animate(view: V, acton: (ViewPropertyAnimator) -> Unit) {
        val startAnimation = iconAnimator.startAnimation(view, view.animate())
        startAnimation.withEndAction {
            acton(startAnimation)
            iconAnimator.endAnimation(view, view.animate()).start()
        }
        startAnimation.start()
    }
}