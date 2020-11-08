package com.armcha.sample

import android.util.Log


inline fun log(message: () -> Any?) {
    Log.e("TAG",message().toString())
}