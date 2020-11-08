package com.armcha.animatedbottombar.animator

import android.view.View
import android.view.ViewPropertyAnimator
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.armcha.animatedbottombar.animator.base.Animator


class RotationAnimator : Animator {

    companion object {
        private const val DURATION = 150L
    }

    override fun startAnimation(view: View): ViewPropertyAnimator {
        return view.animate()
                .scaleX(0f)
                .setDuration(DURATION)
                .setInterpolator(FastOutSlowInInterpolator())
    }

    override fun endAnimation(view: View): ViewPropertyAnimator {
        return view.animate()
                .scaleX(1f)
                .setListener(null)
                .setDuration(DURATION)
                .setInterpolator(FastOutSlowInInterpolator())
    }
}