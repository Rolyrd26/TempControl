package com.tempcontrol.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tempcontrol.app.screens.HomeScreen

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(HomeScreen.create(this))
    }
}
