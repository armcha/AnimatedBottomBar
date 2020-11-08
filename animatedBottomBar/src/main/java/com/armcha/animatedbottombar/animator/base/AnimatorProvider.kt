package com.armcha.animatedbottombar.animator.base

import android.view.View
import android.view.ViewPropertyAnimator

internal class AnimatorProvider(var animator: Animator) {

    fun <V : View> animate(view: V, acton: (ViewPropertyAnimator) -> Unit) {
       val startAnimation = animator.startAnimation(view)
        startAnimation.withEndAction {
                    acton(startAnimation)
                    animator.endAnimation(view).start()
                }
        startAnimation.start()
    }
}