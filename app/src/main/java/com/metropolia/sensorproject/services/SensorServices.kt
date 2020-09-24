package com.metropolia.sensorproject.services

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import io.reactivex.rxjava3.subjects.PublishSubject

object DataStreams {
    private var stepCount = 0
    val stepCountSubject: PublishSubject<Int> = PublishSubject.create()
    val locationSubject: PublishSubject<Location> = PublishSubject.create()

    fun updateStepCount(steps: Int? = null) {
        if (steps == null ) { stepCount ++ } else { stepCount = steps }
        stepCountSubject.onNext(stepCount)
    }

    fun getStepCount(): Int { return stepCount }
}

class SensorService(private val sensorManager: SensorManager) : SensorEventListener {
    private val stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)

    fun registerListener() {
        if (stepDetectorSensor == null) { return }
        stepDetectorSensor.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            if(event.sensor == stepDetectorSensor) {
                DataStreams.updateStepCount()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, value: Int) { }
}

class LocationService(private val context: Context) {
    private val locationClient = LocationServices.getFusedLocationProviderClient(context)
    private val locationCallback = locationCallback()

    fun startGettingLocation() {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION ), 1)
            return
        }
        locationClient.requestLocationUpdates(
            createLocationRequest(),
            locationCallback,
            Looper.getMainLooper()
        )
    }

    fun stopLocationService() {
        locationClient.removeLocationUpdates(locationCallback)
    }

    private fun locationCallback() = object : LocationCallback() {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onLocationResult(result: LocationResult?) {
            result ?: return
            for (location in result.locations) {
                Log.i("XXX", "${location.longitude} ${location.latitude} ${location.speedAccuracyMetersPerSecond}")
                DataStreams.locationSubject.onNext(location)
            }
        }
    }

    private fun createLocationRequest(): LocationRequest? {
        return LocationRequest.create()?.apply {
            interval = 5000
            fastestInterval = 1000
            priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        }
    }
}
