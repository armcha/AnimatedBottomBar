package com.armcha.sample

import android.view.View
import android.view.ViewPropertyAnimator
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.armcha.animatedbottombar.animator.base.Animator

class CustomAnimator : Animator {

    override fun startAnimation(view: View): ViewPropertyAnimator {
        return view.animate()
                .setDuration(200L)
                .setInterpolator(FastOutSlowInInterpolator())
                .rotation(40f)
    }

    override fun endAnimation(view: View): ViewPropertyAnimator {
        return view.animate()
                .setDuration(200L)
                .setInterpolator(FastOutSlowInInterpolator())
                .rotation(0f)
    }
}