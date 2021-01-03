package io.armcha.animatedbottombar.animator

import android.view.View
import android.view.ViewPropertyAnimator
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import io.armcha.animatedbottombar.animator.base.IconAnimator

class RotationIconAnimator : IconAnimator {

    companion object {
        private const val DURATION = 150L
    }

    override fun startAnimation(view: View, animator: ViewPropertyAnimator): ViewPropertyAnimator {
        return animator
                .scaleX(0f)
                .setDuration(DURATION)
                .setInterpolator(FastOutSlowInInterpolator())
    }

    override fun endAnimation(view: View, animator: ViewPropertyAnimator): ViewPropertyAnimator {
        return animator
                .scaleX(1f)
                .setListener(null)
                .setDuration(DURATION)
                .setInterpolator(FastOutSlowInInterpolator())
    }
}