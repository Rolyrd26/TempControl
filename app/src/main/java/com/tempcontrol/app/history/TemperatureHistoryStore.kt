
package com.tempcontrol.app.history

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class TemperatureReading(
    val timestamp: Long,
    val temperatureC: Float
)

object TemperatureHistoryStore {

    private const val PREFS_NAME = "tempcontrol_history"
    private const val KEY_READINGS = "readings"
    private const val SAMPLE_INTERVAL_MS = 60_000L
    private const val MAX_SAMPLES = 1440

    fun recordIfDue(context: Context, temperatureC: Float?) {
        if (temperatureC == null) return

        val prefs = context.applicationContext
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        val readings = readArray(prefs.getString(KEY_READINGS, null))
        val now = System.currentTimeMillis()

        if (readings.length() > 0) {
            val last = readings.optJSONObject(readings.length() - 1)
            val lastTimestamp = last?.optLong("timestamp", 0L) ?: 0L

            if (now - lastTimestamp < SAMPLE_INTERVAL_MS) return
        }

        readings.put(
            JSONObject()
                .put("timestamp", now)
                .put("temperatureC", temperatureC.toDouble())
        )

        val trimmed = JSONArray()
        val start = maxOf(0, readings.length() - MAX_SAMPLES)

        for (i in start until readings.length()) {
            trimmed.put(readings.getJSONObject(i))
        }

        prefs.edit()
            .putString(KEY_READINGS, trimmed.toString())
            .apply()
    }

    fun getReadings(context: Context): List<TemperatureReading> {
        val prefs = context.applicationContext
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        val array = readArray(prefs.getString(KEY_READINGS, null))
        val result = mutableListOf<TemperatureReading>()

        for (i in 0 until array.length()) {
            val item = array.optJSONObject(i) ?: continue

            result.add(
                TemperatureReading(
                    timestamp = item.optLong("timestamp"),
                    temperatureC = item.optDouble("temperatureC").toFloat()
                )
            )
        }

        return result
    }

    private fun readArray(value: String?): JSONArray {
        return try {
            if (value.isNullOrBlank()) JSONArray() else JSONArray(value)
        } catch (_: Exception) {
            JSONArray()
        }
    }
}
