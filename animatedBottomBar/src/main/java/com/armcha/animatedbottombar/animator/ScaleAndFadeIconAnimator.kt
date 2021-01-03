package com.armcha.animatedbottombar.animator

import android.view.View
import android.view.ViewPropertyAnimator
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.armcha.animatedbottombar.animator.base.IconAnimator


class ScaleAndFadeIconAnimator : IconAnimator {

    companion object {
        private const val SCALE_FACTOR = 0.6f
        private const val DURATION = 150L
    }

    override fun startAnimation(view: View, animator: ViewPropertyAnimator): ViewPropertyAnimator {
        return animator
                .alpha(0f)
                .scaleX(SCALE_FACTOR)
                .setDuration(DURATION)
                .setInterpolator(FastOutSlowInInterpolator())
    }

    override fun endAnimation(view: View, animator: ViewPropertyAnimator): ViewPropertyAnimator {
        view.scaleX = SCALE_FACTOR

        return animator
                .scaleX(1f)
                .alpha(1f)
                .setListener(null)
                .setDuration(DURATION)
    }
}