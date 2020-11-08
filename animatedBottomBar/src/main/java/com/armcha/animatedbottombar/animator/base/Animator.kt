package com.armcha.animatedbottombar.animator.base

import android.view.View
import android.view.ViewPropertyAnimator
import androidx.interpolator.view.animation.FastOutSlowInInterpolator


interface Animator {

    fun startAnimation(view: View): ViewPropertyAnimator

    fun endAnimation(view: View): ViewPropertyAnimator
}