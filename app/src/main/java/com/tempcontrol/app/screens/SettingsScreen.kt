package com.tempcontrol.app.screens

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import java.util.Locale

object SettingsScreen {

    private const val PREFS_NAME = "tempcontrol_settings"
    private const val THRESHOLD_KEY = "temperature_threshold"
    private const val MONITORING_KEY = "monitoring_active"
    private const val DEFAULT_THRESHOLD = 40f

    fun create(context: Context): LinearLayout {
        val fondo = Color.rgb(244, 247, 250)
        val texto = Color.rgb(35, 48, 65)
        val principal = Color.rgb(0, 150, 136)

        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 28, 24, 24)
            setBackgroundColor(fondo)
        }

        layout.addView(TextView(context).apply {
            text = "⚙️ Ajustes"
            textSize = 28f
            setTextColor(texto)
            gravity = Gravity.CENTER
        })

        layout.addView(TextView(context).apply {
            text = "Configura las alertas y la monitorización"
            textSize = 15f
            setTextColor(texto)
            gravity = Gravity.CENTER
            setPadding(0, 8, 0, 24)
        })

        layout.addView(TextView(context).apply {
            text = "LÍMITE DE ALERTA"
            textSize = 14f
            setTextColor(texto)
        })

        val prefs = context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )

        val currentThreshold = prefs.getFloat(
            THRESHOLD_KEY,
            DEFAULT_THRESHOLD
        )

        layout.addView(TextView(context).apply {
            text = String.format(
                Locale.getDefault(),
                "%.1f °C",
                currentThreshold
            )
            textSize = 32f
            setTextColor(principal)
            gravity = Gravity.CENTER
            setPadding(0, 12, 0, 16)
        })

        layout.addView(TextView(context).apply {
            text = "Selecciona un límite habitual:"
            textSize = 16f
            setTextColor(texto)
            setPadding(0, 0, 0, 8)
        })

        val options = listOf(35f, 38f, 40f, 42f, 45f)

        for (value in options) {
            layout.addView(Button(context).apply {
                text = String.format(
                    Locale.getDefault(),
                    "%.0f °C%s",
                    value,
                    if (value == currentThreshold) " · Actual" else ""
                )

                setOnClickListener {
                    prefs.edit()
                        .putFloat(THRESHOLD_KEY, value)
                        .apply()

                    (context as? Activity)?.setContentView(
                        create(context)
                    )
                }
            }, buttonParams(4))
        }

        layout.addView(Button(context).apply {
            text = "Introducir otro límite"
            setOnClickListener {
                (context as? Activity)?.setContentView(
                    HomeScreen.create(context)
                )
                HomeScreen.openTemperatureSettings(context)
            }
        }, buttonParams(8))

        layout.addView(TextView(context).apply {
            text = "ESTADO DE LA MONITORIZACIÓN"
            textSize = 14f
            setTextColor(texto)
            setPadding(0, 24, 0, 8)
        })

        val running = isMonitoring(context)

        layout.addView(TextView(context).apply {
            text = if (running) {
                "● Monitorización activa"
            } else {
                "○ Monitorización detenida"
            }
            textSize = 18f
            setTextColor(
                if (running) Color.rgb(46, 125, 50)
                else Color.rgb(100, 100, 100)
            )
            gravity = Gravity.CENTER
            setPadding(0, 8, 0, 16)
        })

        layout.addView(Button(context).apply {
            text = "Actualizar estado"
            setOnClickListener {
                (context as? Activity)?.setContentView(
                    create(context)
                )
            }
        }, buttonParams(4))

        layout.addView(Button(context).apply {
            text = "← Volver al inicio"
            setOnClickListener {
                (context as? Activity)?.setContentView(
                    HomeScreen.create(context)
                )
            }
        }, buttonParams(12))

        return layout
    }

    private fun isMonitoring(context: Context): Boolean {
        return context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        ).getBoolean(MONITORING_KEY, false)
    }

    private fun buttonParams(topMargin: Int) =
        LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            this.topMargin = topMargin
        }
}
