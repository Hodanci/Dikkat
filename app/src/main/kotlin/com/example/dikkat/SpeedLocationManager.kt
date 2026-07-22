package com.example.dikkat

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority

class SpeedLocationManager(
    private val context: Context,
    private val fusedLocationClient: FusedLocationProviderClient,
    private val onLocationUpdate: (Location) -> Unit
) {
    
    private var locationCallback: LocationCallback? = null
    private var isTracking = false

    fun startLocationUpdates() {
        if (isTracking) return

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            1000 // 1 saniyede bir güncelle
        ).apply {
            setMinUpdateDistanceMeters(5f)
            setMaxUpdateDelayMillis(2000)
        }.build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    onLocationUpdate(location)
                }
            }
        }

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback!!,
                null
            )
            isTracking = true
        }
    }

    fun stopLocationUpdates() {
        if (locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback!!)
            isTracking = false
        }
    }

    fun isTracking(): Boolean = isTracking
}