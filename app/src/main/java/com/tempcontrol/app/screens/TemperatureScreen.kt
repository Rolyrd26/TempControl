
package com.tempcontrol.app.screens

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.tempcontrol.app.monitor.BatteryTemperatureMonitor

object TemperatureScreen {

    fun create(context: Context): LinearLayout {
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(32, 32, 32, 32)
            setBackgroundColor(Color.rgb(245, 248, 250))
        }

        val title = TextView(context).apply {
            text = "Monitor de temperatura"
            textSize = 24f
            setTextColor(Color.rgb(30, 45, 60))
            gravity = Gravity.CENTER
        }

        val thermometer = TextView(context).apply {
            text = "🌡️"
            textSize = 72f
            gravity = Gravity.CENTER
        }

        val batteryTemperature =
            BatteryTemperatureMonitor.getBatteryTemperature(context)

        val temperature = TextView(context).apply {
            text = batteryTemperature?.let {
                String.format(java.util.Locale.getDefault(), "%.1f °C", it)
            } ?: "-- °C"
            textSize = 42f
            setTextColor(Color.rgb(0, 150, 136))
            gravity = Gravity.CENTER
        }

        val status = TextView(context).apply {
            text = if (batteryTemperature != null) {
                "Temperatura de la batería"
            } else {
                "No se pudo obtener la temperatura"
            }
            textSize = 16f
            setTextColor(Color.DKGRAY)
            gravity = Gravity.CENTER
        }

        layout.addView(title)
        layout.addView(thermometer)
        layout.addView(temperature)
        layout.addView(status)

        return layout
    }
}
