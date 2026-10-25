
package com.tempcontrol.app.screens

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.tempcontrol.app.monitor.BatteryTemperatureMonitor
import com.tempcontrol.app.history.TemperatureHistoryStore
import java.util.Locale

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

        val temperatureText = TextView(context).apply {
            text = "-- °C"
            textSize = 42f
            setTextColor(Color.rgb(0, 150, 136))
            gravity = Gravity.CENTER
        }
        layout.addView(temperatureText)

        val statusText = TextView(context).apply {
            text = "Leyendo temperatura..."
            textSize = 16f
            setTextColor(Color.DKGRAY)
            gravity = Gravity.CENTER
            setPadding(0, 12, 0, 24)
        }
        layout.addView(statusText)

        val handler = Handler(Looper.getMainLooper())

        val updateTemperature = object : Runnable {
            override fun run() {
                val batteryTemperature =
                    BatteryTemperatureMonitor.getBatteryTemperature(context)
TemperatureHistoryStore.recordIfDue(context, batteryTemperature)

                temperatureText.text = batteryTemperature?.let {
                    String.format(Locale.getDefault(), "%.1f °C", it)
                } ?: "-- °C"

                
                statusText.text = when {
                    batteryTemperature == null -> {
                        statusText.setTextColor(Color.DKGRAY)
                        "Temperatura no disponible"
                    }

                    batteryTemperature < 35f -> {
                        statusText.setTextColor(Color.rgb(46, 125, 50))
                        "NORMAL · Temperatura adecuada"
                    }

                    batteryTemperature < 40f -> {
                        statusText.setTextColor(Color.rgb(239, 145, 0))
                        "CALIENTE · Vigila la temperatura"
                    }

                    batteryTemperature < 45f -> {
                        statusText.setTextColor(Color.rgb(230, 81, 0))
                        "MUY CALIENTE · Reduce el uso"
                    }

                    else -> {
                        statusText.setTextColor(Color.rgb(198, 40, 40))
                        "ALERTA · Deja de usarlo y permite que se enfríe"
                    }
                }


                handler.postDelayed(this, 2000L)
            }
        }

        handler.post(updateTemperature)

        layout.addView(Button(context).apply {
            text = "← Volver al inicio"
            setOnClickListener {
                handler.removeCallbacks(updateTemperature)
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

