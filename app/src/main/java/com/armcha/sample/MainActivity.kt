package com.armcha.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.armcha.animatedbottombar.BottomItem
import com.armcha.animatedbottombar.FabItem
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        animatedBottomBar.addFabItems(
                FabItem(R.color.purple_500, R.color.black, R.drawable.at),
                FabItem(R.color.purple_200, R.color.white, R.drawable.at),
                FabItem(R.color.purple_200, R.color.white, R.drawable.at))

        val items = mutableListOf<BottomItem>()
        items += BottomItem(R.drawable.at, "TITLE 1")
        items += BottomItem(R.drawable.at, "TITLE 2")
        items += BottomItem(R.drawable.at, "TITLE 3")
        items += BottomItem(R.drawable.at, "TITLE 4")
        animatedBottomBar.addBottomItems(items)
    }
}