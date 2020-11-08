package com.armcha.animatedbottombar

import android.content.res.ColorStateList
import android.view.View
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

fun ImageView.tint(@ColorInt color: Int) {
    imageTintList = ColorStateList.valueOf(color)
}

infix fun View.colorFrom(@ColorRes color: Int) = ContextCompat.getColor(context, color)