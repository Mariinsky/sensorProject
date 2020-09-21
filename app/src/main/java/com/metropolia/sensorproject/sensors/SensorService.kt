package com.metropolia.sensorproject.sensors

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*

val REQUEST_LOCATION_CODE = 100

class SensorService(context: Context) {

    private val locationClient = LocationServices.getFusedLocationProviderClient(context)
    private val locationCallback = locationCallback()

    private fun startGettingLocation(context: Context) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION ), REQUEST_LOCATION_CODE)
            return
        }
        locationClient.requestLocationUpdates(
            createLocationRequest(),
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun stopLocationService() {
        locationClient.removeLocationUpdates(locationCallback)
    }

    private fun locationCallback() = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult?) {
            result ?: return
            for (location in result.locations) {
                Log.i("XXX", "${location.longitude} ${location.latitude}")
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