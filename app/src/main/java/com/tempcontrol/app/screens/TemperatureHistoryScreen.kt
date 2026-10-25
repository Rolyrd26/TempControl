package com.tempcontrol.app.screens

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.tempcontrol.app.history.TemperatureHistoryStore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TemperatureHistoryScreen {

    fun create(context: Context): LinearLayout {
        val fondo = Color.rgb(244, 247, 250)
        val texto = Color.rgb(35, 48, 65)

        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 24, 24, 24)
            setBackgroundColor(fondo)
        }

        layout.addView(TextView(context).apply {
            text = "Historial de temperatura"
            textSize = 24f
            setTextColor(texto)
            gravity = Gravity.CENTER
            setPadding(0, 8, 0, 20)
        })

        val readings = TemperatureHistoryStore
            .getReadings(context)
            .sortedByDescending { it.timestamp }

        layout.addView(TextView(context).apply {
            text = if (readings.isEmpty()) {
                "Todavía no hay lecturas guardadas.\n" +
                    "Abre el monitor y déjalo activo al menos un minuto."
            } else {
                "Lecturas guardadas: ${readings.size}"
            }
            textSize = 16f
            setTextColor(texto)
            setPadding(0, 0, 0, 16)
        })

        val list = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
        }

        val dateFormat = SimpleDateFormat(
            "dd/MM/yyyy · HH:mm",
            Locale.getDefault()
        )

        for (reading in readings) {
            list.addView(TextView(context).apply {
                text = "${String.format(Locale.getDefault(), "%.1f", reading.temperatureC)} °C\n" +
                    dateFormat.format(Date(reading.timestamp))
                textSize = 18f
                setTextColor(texto)
                setPadding(16, 14, 16, 14)
                setBackgroundColor(Color.WHITE)
            }, LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 8
            })
        }

        val scroll = ScrollView(context).apply {
            addView(list)
        }

        layout.addView(scroll, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            0,
            1f
        ))

        layout.addView(Button(context).apply {
            text = "← Volver al inicio"
            setOnClickListener {
                (context as? Activity)?.setContentView(
                    HomeScreen.create(context)
                )
            }
        })

        return layout
    }
}
