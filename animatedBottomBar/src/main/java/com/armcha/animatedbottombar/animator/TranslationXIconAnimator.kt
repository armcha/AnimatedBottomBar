package com.armcha.animatedbottombar.animator

import android.view.View
import android.view.ViewPropertyAnimator
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.armcha.animatedbottombar.animator.base.IconAnimator

class TranslationXIconAnimator : IconAnimator {

    override fun startAnimation(view: View, animator: ViewPropertyAnimator): ViewPropertyAnimator {
        return animator
                .translationX(-50f)
                .setDuration(180L)
                .setInterpolator(FastOutSlowInInterpolator())
    }

    override fun endAnimation(view: View, animator: ViewPropertyAnimator): ViewPropertyAnimator {
        return animator
                .setDuration(180L)
                .setInterpolator(FastOutSlowInInterpolator())
                .translationX(0f)
    }
}