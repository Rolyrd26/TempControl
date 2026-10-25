package com.tempcontrol.app.monitor

import android.content.Context
import android.content.Intent
import android.content.IntentFilter

object BatteryTemperatureMonitor {

    fun getBatteryTemperature(context: Context): Float? {
        val batteryStatus = context.registerReceiver(
            null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )

        val temperature = batteryStatus?.getIntExtra(
            "temperature",
            -1
        ) ?: -1

        return if (temperature >= 0) {
            temperature / 10f
        } else {
            null
        }
    }
}
