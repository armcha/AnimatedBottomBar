package com.armcha.animatedbottombar.animator

import android.view.View

internal class AnimatorProvider(var animator: Animator) {

    fun <V : View> animate(view: V,acton: () -> Unit) {
        view.clearAnimation()
        view.animate().cancel()
        animator.startAnimation(view)
                .withEndAction {
                    acton()
                    animator.endAnimation(view)
                            .start()
                }
                .start()
    }
}