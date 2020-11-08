package com.armcha.animatedbottombar.animator

import android.view.View
import android.view.ViewPropertyAnimator
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.armcha.animatedbottombar.animator.base.Animator

class TranslationXAnimator : Animator {

    override fun startAnimation(view: View): ViewPropertyAnimator {
        return view.animate()
                .translationX(-50f)
                .setDuration(180L)
                .setInterpolator(FastOutSlowInInterpolator())
    }

    override fun endAnimation(view: View): ViewPropertyAnimator {
        return view.animate()
                .setDuration(180L)
                .setInterpolator(FastOutSlowInInterpolator())
                .translationX(0f)
    }
}