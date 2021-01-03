package io.armcha.animatedbottombar.config

import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes

data class OvalButtonConfig(@ColorInt val backgroundColor: Int,
                            @ColorInt val iconTint: Int,
                            @DrawableRes val icon: Int,
                            @DrawableRes val closeIcon: Int)