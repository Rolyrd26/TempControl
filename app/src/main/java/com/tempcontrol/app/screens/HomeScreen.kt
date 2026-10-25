
package com.tempcontrol.app.screens

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

object HomeScreen {

    fun create(context: Context): LinearLayout {
        val fondo = Color.rgb(244, 247, 250)
        val texto = Color.rgb(35, 48, 65)
        val principal = Color.rgb(0, 150, 136)

        return LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(28, 32, 28, 32)
            setBackgroundColor(fondo)

            addView(TextView(context).apply {
                text = "🌡️"
                textSize = 76f
                gravity = Gravity.CENTER
            })

            addView(TextView(context).apply {
                text = "TempControl"
                textSize = 30f
                setTextColor(texto)
                gravity = Gravity.CENTER
            })

            addView(TextView(context).apply {
                text = "Controla la temperatura de tu teléfono"
                textSize = 16f
                setTextColor(texto)
                gravity = Gravity.CENTER
                setPadding(0, 12, 0, 0)
            })

            addView(Button(context).apply {
                text = "🌡️  Monitor de temperatura"
                textSize = 16f
                setTextColor(Color.WHITE)
                setBackgroundColor(principal)

                setOnClickListener {
                    val activity = context as? android.app.Activity
                    activity?.setContentView(
                        TemperatureScreen.create(context)
                    )
                }
            }, LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 36
            })

            
            addView(Button(context).apply {
                text = "📊  Historial de temperatura"
                textSize = 16f
                setTextColor(Color.WHITE)
                setBackgroundColor(principal)

                setOnClickListener {
                    val activity = context as? android.app.Activity
                    activity?.setContentView(
                        TemperatureHistoryScreen.create(context)
                    )
                }
            }, LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 16
            })

            addView(TextView(context).apply {
                text = "Consulta las lecturas guardadas mientras " +
                    "el monitor está activo."
                textSize = 14f
                setTextColor(texto)
                gravity = Gravity.CENTER
                setPadding(0, 16, 0, 0)
            })

        }
    }
}
