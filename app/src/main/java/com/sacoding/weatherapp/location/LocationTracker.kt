package com.sacoding.weatherapp.location

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.sacoding.weatherapp.util.TrackingUtility
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.internal.resumeCancellableWith
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
@ExperimentalCoroutinesApi
class LocationTracker @Inject constructor(
    private val application: Application,
    private val fusedLocationClient: FusedLocationProviderClient
) {


    suspend fun getCurrentLocation():Location? {

        val hasAccessFineLocationPermission = ContextCompat.checkSelfPermission(
            application,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val hasAccessCoarseLocationPermission = ContextCompat.checkSelfPermission(
            application,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val locationManager =
            application.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)


        if(!hasAccessCoarseLocationPermission || !hasAccessFineLocationPermission|| !isGpsEnabled){
            return null
        }
        return suspendCancellableCoroutine  { cont->
            fusedLocationClient.lastLocation.apply {
                if(isComplete){
                    if(isSuccessful) {
                        cont.resume(result)
                    }
                    else {
                        cont.resume(null)
                    }
                    return@suspendCancellableCoroutine
                }
                addOnSuccessListener {
                    cont.resume(it)
                }
                addOnFailureListener {
                    cont.resume(null)
                }
                addOnCanceledListener {
                    cont.cancel()
                }
            }
        }
    }
}

