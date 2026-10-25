package com.tempcontrol.app.screens

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView

object HomeScreen {

    fun create(context: Context): LinearLayout {
        val fondo = Color.rgb(244, 247, 250)
        val texto = Color.rgb(35, 48, 65)
        val principal = Color.rgb(0, 168, 150)

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

            addView(TextView(context).apply {
                text = "Monitor de temperatura"
                textSize = 20f
                setTextColor(principal)
                gravity = Gravity.CENTER
                setPadding(0, 36, 0, 12)
            })

            addView(TextView(context).apply {
                text = "Pronto podrás consultar la temperatura, " +
                    "revisar el historial y recibir alertas."
                textSize = 15f
                setTextColor(texto)
                gravity = Gravity.CENTER
            })
        }
    }
}
