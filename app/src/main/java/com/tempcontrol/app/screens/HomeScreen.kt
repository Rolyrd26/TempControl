
package com.tempcontrol.app.screens

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.tempcontrol.app.monitor.TemperatureMonitoringService

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
                text = "🌡️ Monitor de temperatura"
                textSize = 16f
                setTextColor(Color.WHITE)
                setBackgroundColor(principal)

                setOnClickListener {
                    (context as? Activity)?.setContentView(
                        TemperatureScreen.create(context)
                    )
                }
            }, LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 24
            })

            addView(Button(context).apply {
                text = "▶ Iniciar monitorización"
                textSize = 16f

                setOnClickListener {
                    val activity = context as? Activity

                    if (
                        Build.VERSION.SDK_INT >= 33 &&
                        activity != null &&
                        activity.checkSelfPermission(
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        activity.requestPermissions(
                            arrayOf(
                                Manifest.permission.POST_NOTIFICATIONS
                            ),
                            2001
                        )
                        return@setOnClickListener
                    }

                    val intent = Intent(
                        context,
                        TemperatureMonitoringService::class.java
                    ).apply {
                        action =
                            TemperatureMonitoringService.ACTION_START
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(intent)
                    } else {
                        context.startService(intent)
                    }
                }
            }, LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 16
            })

            addView(Button(context).apply {
                text = "■ Detener monitorización"
                textSize = 16f

                setOnClickListener {
                    context.stopService(
                        Intent(
                            context,
                            TemperatureMonitoringService::class.java
                        )
                    )
                }
            }, LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 8
            })

            addView(Button(context).apply {
                text = "📊 Historial de temperatura"
                textSize = 16f
                setTextColor(Color.WHITE)
                setBackgroundColor(principal)

                setOnClickListener {
                    (context as? Activity)?.setContentView(
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
                text = "La monitorización en segundo plano requiere " +
                    "mantener activo el servicio de Android."
                textSize = 14f
                setTextColor(texto)
                gravity = Gravity.CENTER
                setPadding(0, 16, 0, 0)
            })
        }
    }
}
