
package com.tempcontrol.app.screens

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
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

        layout.addView(TextView(context).apply {
            text = "Monitor de temperatura"
            textSize = 24f
            setTextColor(Color.rgb(30, 45, 60))
            gravity = Gravity.CENTER
        })

        layout.addView(TextView(context).apply {
            text = "🌡️"
            textSize = 72f
            gravity = Gravity.CENTER
            setPadding(0, 24, 0, 12)
        })

        val batteryTemperature =
            BatteryTemperatureMonitor.getBatteryTemperature(context)

        layout.addView(TextView(context).apply {
            text = batteryTemperature?.let {
                String.format(
                    java.util.Locale.getDefault(),
                    "%.1f °C",
                    it
                )
            } ?: "-- °C"
            textSize = 42f
            setTextColor(Color.rgb(0, 150, 136))
            gravity = Gravity.CENTER
        })

        layout.addView(TextView(context).apply {
            text = if (batteryTemperature != null) {
                "Temperatura de la batería"
            } else {
                "No se pudo obtener la temperatura"
            }
            textSize = 16f
            setTextColor(Color.DKGRAY)
            gravity = Gravity.CENTER
            setPadding(0, 12, 0, 24)
        })

        layout.addView(Button(context).apply {
            text = "← Volver al inicio"
            setOnClickListener {
                (context as? Activity)?.setContentView(
                    HomeScreen.create(context)
                )
            }
        }, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ))

        return layout
    }
}
