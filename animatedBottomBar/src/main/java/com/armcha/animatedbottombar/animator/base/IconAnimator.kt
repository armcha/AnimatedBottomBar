package com.armcha.animatedbottombar.animator.base

import android.view.View
import android.view.ViewPropertyAnimator


interface IconAnimator {

    fun startAnimation(view: View, animator: ViewPropertyAnimator): ViewPropertyAnimator

    fun endAnimation(view: View, animator: ViewPropertyAnimator): ViewPropertyAnimator
}