package io.armcha.animatedbottombar.config

import androidx.annotation.ColorInt

data class BottomBarConfig(@ColorInt val backgroundColor: Int,
                           @ColorInt val selectedItemTint: Int,
                           @ColorInt val unSelectedItemTint: Int,
                           val cornerRadius: Float,
                           val shouldShowTitle: Boolean)