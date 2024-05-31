package com.example.front

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

class ShakeDetector(context: Context, private val onShake: () -> Unit) : SensorEventListener {

    private val sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var accelerationCurrent = SensorManager.GRAVITY_EARTH
    private var accelerationLast = SensorManager.GRAVITY_EARTH

    init {
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_UI
        )
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            val x = it.values[0]
            val y = it.values[1]
            val z = it.values[2]

            val acceleration = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
            val delta = acceleration - accelerationLast
            accelerationLast = accelerationCurrent
            accelerationCurrent = acceleration

            if (delta > 12) { // Threshold for shake detection
                onShake()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    fun stop() {
        sensorManager.unregisterListener(this)
    }
}
