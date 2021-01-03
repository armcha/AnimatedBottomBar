package io.armcha.sample

import android.view.View
import android.view.ViewPropertyAnimator
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import io.armcha.animatedbottombar.animator.base.IconAnimator

class CustomIconAnimator : IconAnimator {

    override fun startAnimation(view: View, animator: ViewPropertyAnimator): ViewPropertyAnimator {
        return animator
                .setDuration(200L)
                .setInterpolator(FastOutSlowInInterpolator())
                .rotation(40f)
    }

    override fun endAnimation(view: View, animator: ViewPropertyAnimator): ViewPropertyAnimator {
        return animator
                .setDuration(200L)
                .setInterpolator(FastOutSlowInInterpolator())
                .rotation(0f)
    }
}