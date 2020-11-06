package com.armcha.animatedbottombar.animator

import android.view.View
import android.view.ViewPropertyAnimator
import androidx.interpolator.view.animation.FastOutSlowInInterpolator


class DefaultAnimator : Animator {

    companion object {
        private const val SCALE_FACTOR = 0.75f
        private const val DURATION = 160L
    }

    override fun startAnimation(view: View): ViewPropertyAnimator {
        return view.animate()
                .alpha(0f)
                .scaleX(SCALE_FACTOR)
                .setDuration(DURATION)
                .setInterpolator(FastOutSlowInInterpolator())
    }

    override fun endAnimation(view: View): ViewPropertyAnimator {
        view.scaleX = SCALE_FACTOR
        return view.animate()
                .scaleX(1f)
                .alpha(1f)
                .setListener(null)
                .setDuration(DURATION)
    }
}