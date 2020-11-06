package com.armcha.animatedbottombar

import android.view.View

class AnimatorProvider(private val animator: Animator) {

    fun <V : View> animate(view: V,acton: () -> Unit) {
        view.clearAnimation()
        animator.startAnimation(view)
                .withEndAction {
                    acton()
                    animator.endAnimation(view)
                            .start()
                }
                .start()
    }
}