package com.armcha.animatedbottombar.animator

import android.view.View
import android.view.ViewPropertyAnimator
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.armcha.animatedbottombar.animator.base.IconAnimator

class FadeIconAnimator : IconAnimator {

    override fun startAnimation(view: View, animator: ViewPropertyAnimator): ViewPropertyAnimator {
        return animator
                .setDuration(200L)
                .setInterpolator(FastOutSlowInInterpolator())
                .alpha(0f)
    }

    override fun endAnimation(view: View, animator: ViewPropertyAnimator): ViewPropertyAnimator {
        return animator
                .setDuration(200L)
                .setInterpolator(FastOutSlowInInterpolator())
                .alpha(1f)
    }
}