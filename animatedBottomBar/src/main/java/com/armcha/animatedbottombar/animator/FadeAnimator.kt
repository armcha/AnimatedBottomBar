package com.armcha.animatedbottombar.animator

import android.view.View
import android.view.ViewPropertyAnimator
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.armcha.animatedbottombar.animator.base.Animator

class FadeAnimator : Animator {

    override fun startAnimation(view: View): ViewPropertyAnimator {
        return view.animate()
                .setDuration(200L)
                .setInterpolator(FastOutSlowInInterpolator())
                .alpha(0f)
    }

    override fun endAnimation(view: View): ViewPropertyAnimator {
        return view.animate()
                .setDuration(200L)
                .setInterpolator(FastOutSlowInInterpolator())
                .alpha(1f)
    }
}