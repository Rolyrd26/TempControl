
package com.tempcontrol.app.screens

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.text.InputType
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.tempcontrol.app.monitor.TemperatureMonitoringService
import java.util.Locale

object HomeScreen {

    private const val PREFS_NAME = "tempcontrol_settings"
    private const val THRESHOLD_KEY = "temperature_threshold"
    private const val DEFAULT_THRESHOLD = 40f

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
            }, buttonParams(24))

            addView(Button(context).apply {
                text = "⚙️ Configurar alerta de temperatura"
                textSize = 16f
                setOnClickListener {
                    showTemperatureSettings(context)
                }
            }, buttonParams(12))

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
            }, buttonParams(12))

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
            }, buttonParams(8))

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
            }, buttonParams(16))

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

    private fun buttonParams(topMargin: Int) =
        LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            this.topMargin = topMargin
        }

    private fun showTemperatureSettings(context: Context) {
        val prefs = context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )

        val currentThreshold = prefs.getFloat(
            THRESHOLD_KEY,
            DEFAULT_THRESHOLD
        )

        val input = EditText(context).apply {
            inputType =
                InputType.TYPE_CLASS_NUMBER or
                    InputType.TYPE_NUMBER_FLAG_DECIMAL
            setSingleLine(true)
            hint = "Ejemplo: 40"
            setText(
                String.format(Locale.US, "%.1f", currentThreshold)
            )
            selectAll()
        }

        val container = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 12, 48, 0)
            addView(
                input,
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            )
            addView(TextView(context).apply {
                text = "Recibirás una notificación cuando la batería " +
                    "alcance este límite mientras la monitorización esté activa."
                textSize = 14f
                setPadding(0, 12, 0, 0)
            })
        }

        AlertDialog.Builder(context)
            .setTitle("Alerta de sobrecalentamiento")
            .setView(container)
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Guardar", null)
            .create()
            .apply {
                setOnShowListener {
                    getButton(AlertDialog.BUTTON_POSITIVE)
                        .setOnClickListener {
                            val value = input.text.toString()
                                .trim()
                                .replace(',', '.')
                                .toFloatOrNull()

                            if (value == null || value < 30f || value > 60f) {
                                input.error =
                                    "Introduce un valor entre 30 y 60 °C"
                                return@setOnClickListener
                            }

                            prefs.edit()
                                .putFloat(THRESHOLD_KEY, value)
                                .apply()

                            Toast.makeText(
                                context,
                                "Límite guardado: ${value} °C",
                                Toast.LENGTH_LONG
                            ).show()

                            dismiss()
                        }
                }
                show()
            }
    }
}
