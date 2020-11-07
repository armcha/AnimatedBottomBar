package com.armcha.animatedbottombar

import androidx.annotation.ColorRes

class BottomBarConfig(@ColorRes val backgroundColor: Int,
                      @ColorRes val selectedItemTint: Int,
                      @ColorRes val unSelectedItemTint: Int,
                      val cornerRadius: Float)