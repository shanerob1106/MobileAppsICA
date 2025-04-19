package com.example.sendit.helpers

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

// Sensor code for the Barometer
@Composable
fun SenseBarometer(
    sensorDelay: Int = SensorManager.SENSOR_DELAY_NORMAL,
    onSensorChanged: (event: SensorEvent) -> Unit = {},
    onAccuracyChanged: (sensor: Sensor, accuracy: Int) -> Unit = { _, _ -> },
    onPressureChanged: (pressure: Float) -> Unit = {}
) {
    val context = LocalContext.current
    val sensorManager =
        remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    val barometerSensor = remember { sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE) }

    val sensorListener = remember {
        object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor.type == Sensor.TYPE_PRESSURE) {
                    onSensorChanged(event)
                    onPressureChanged(event.values[0])
                }
            }

            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
                onAccuracyChanged(sensor, accuracy)
            }
        }
    }

    DisposableEffect(Unit) {
        barometerSensor?.let {
            sensorManager.registerListener(sensorListener, it, sensorDelay)
        }
        onDispose {
            sensorManager.unregisterListener(sensorListener)
        }
    }
}

// Sensor code for the Accelerometer
@Composable
fun SenseAccelerometer(
    sensorDelay: Int = SensorManager.SENSOR_DELAY_NORMAL,
    onSensorChanged: (event: SensorEvent) -> Unit = {},
    onAccuracyChanged: (sensor: Sensor, accuracy: Int) -> Unit = { _, _ -> },
    onSensorDataChanged: (x: Float, y: Float, z: Float) -> Unit = { _, _, _ -> }
) {
    val context = LocalContext.current
    val sensorManager =
        remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    val accelerometer = remember { sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) }
    val sensorListener = remember {
        object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                    onSensorChanged(event)
                    onSensorDataChanged(event.values[0], event.values[1], event.values[2])
                }
            }

            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
                onAccuracyChanged(sensor, accuracy)
            }
        }
    }

    DisposableEffect(Unit) {
        sensorManager.registerListener(sensorListener, accelerometer, sensorDelay)
        onDispose {
            sensorManager.unregisterListener(sensorListener)
        }
    }
}