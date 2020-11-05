package com.armcha.animatedbottombar

import android.util.Log


inline fun log(message: () -> Any?) {
    Log.e("TAG",message().toString())
}