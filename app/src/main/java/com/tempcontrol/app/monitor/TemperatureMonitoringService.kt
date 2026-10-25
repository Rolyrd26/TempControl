package com.tempcontrol.app.monitor

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
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
        private const val NOTIFICATION_ID = 1001
        private const val INTERVAL_MS = 60_000L
    }

    private val handler = Handler(Looper.getMainLooper())

    private val monitorTask = object : Runnable {
        override fun run() {
            val temperature =
                BatteryTemperatureMonitor.getBatteryTemperature(this@TemperatureMonitoringService)

            TemperatureHistoryStore.recordIfDue(
                this@TemperatureMonitoringService,
                temperature
            )

            handler.postDelayed(this, INTERVAL_MS)
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
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

        startForeground(
            NOTIFICATION_ID,
            createNotification()
        )

        handler.removeCallbacks(monitorTask)

        // Guardar una lectura al iniciar y continuar cada minuto.
        monitorTask.run()

        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Monitor de temperatura",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description =
                    "Indica que TempControl está vigilando la temperatura"
            }

            val manager =
                getSystemService(NotificationManager::class.java)

            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val builder =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Notification.Builder(this, CHANNEL_ID)
            } else {
                @Suppress("DEPRECATION")
                Notification.Builder(this)
            }

        return builder
            .setSmallIcon(android.R.drawable.ic_menu_info_details)
            .setContentTitle("TempControl está activo")
            .setContentText(
                "Registrando la temperatura de la batería cada minuto"
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
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
