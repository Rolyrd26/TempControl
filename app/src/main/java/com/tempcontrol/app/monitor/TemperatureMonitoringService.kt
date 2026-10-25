
package com.tempcontrol.app.monitor

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import com.tempcontrol.app.history.TemperatureHistoryStore

class TemperatureMonitoringService : Service() {

    companion object {
        const val ACTION_START =
            "com.tempcontrol.app.monitor.START"
        const val ACTION_STOP =
            "com.tempcontrol.app.monitor.STOP"

        private const val CHANNEL_ID = "tempcontrol_monitor"
        private const val ALERT_CHANNEL_ID = "tempcontrol_alerts"
        private const val NOTIFICATION_ID = 1001
        private const val ALERT_NOTIFICATION_ID = 1002
        private const val INTERVAL_MS = 60_000L
        private const val PREFS_NAME = "tempcontrol_settings"
        private const val THRESHOLD_KEY = "temperature_threshold"
        private const val MONITORING_KEY = "monitoring_active"
        private const val DEFAULT_THRESHOLD = 40f
    }

    private val handler = Handler(Looper.getMainLooper())
    private var alertActive = false

    private val monitorTask = object : Runnable {
        override fun run() {
            val temperature =
                BatteryTemperatureMonitor.getBatteryTemperature(this@TemperatureMonitoringService)

            TemperatureHistoryStore.recordIfDue(
                this@TemperatureMonitoringService,
                temperature
            )

            if (temperature != null) {
                checkTemperatureAlert(temperature)
            }

            handler.postDelayed(this, INTERVAL_MS)
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        if (intent?.action == ACTION_STOP) {
            stopMonitoring()
            return START_NOT_STICKY
        }

                        startForeground(NOTIFICATION_ID, createNotification())

        getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(MONITORING_KEY, true)
            .apply()
        handler.removeCallbacks(monitorTask)
        monitorTask.run()

        return START_STICKY
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)

            manager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID,
                    "Monitor de temperatura",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "Monitorización de TempControl"
                }
            )

            manager.createNotificationChannel(
                NotificationChannel(
                    ALERT_CHANNEL_ID,
                    "Alertas de sobrecalentamiento",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Avisos cuando la batería supera el límite configurado"
                }
            )
        }
    }

    private fun checkTemperatureAlert(temperature: Float) {
        val prefs = getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )
        val threshold = prefs.getFloat(
            THRESHOLD_KEY,
            DEFAULT_THRESHOLD
        )

        val manager = getSystemService(NotificationManager::class.java)

        if (temperature >= threshold && !alertActive) {
            alertActive = true

            val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Notification.Builder(this, ALERT_CHANNEL_ID)
            } else {
                @Suppress("DEPRECATION")
                Notification.Builder(this)
            }

            manager.notify(
                ALERT_NOTIFICATION_ID,
                builder
                    .setSmallIcon(android.R.drawable.ic_dialog_alert)
                    .setContentTitle("⚠️ TempControl: batería caliente")
                    .setContentText(
                        "La batería está a %.1f °C. Límite: %.1f °C".format(
                            temperature,
                            threshold
                        )
                    )
                    .setAutoCancel(true)
                    .build()
            )
        } else if (temperature < threshold) {
            alertActive = false
        }
    }

    private fun createNotification(): Notification {
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, CHANNEL_ID)
        } else {
            @Suppress("DEPRECATION")
            Notification.Builder(this)
        }

        return builder
            .setSmallIcon(android.R.drawable.ic_menu_info_details)
            .setContentTitle("TempControl está activo")
            .setContentText(
                "Vigilando la temperatura de la batería cada minuto"
            )
            .setOngoing(true)
            .build()
    }

    private fun stopMonitoring() {
        handler.removeCallbacks(monitorTask)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        handler.removeCallbacks(monitorTask)

        getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(MONITORING_KEY, false)
            .apply()

        super.onDestroy()
    }
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}