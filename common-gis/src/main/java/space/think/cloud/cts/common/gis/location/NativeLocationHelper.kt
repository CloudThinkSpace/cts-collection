package space.think.cloud.cts.common.gis.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import androidx.core.app.ActivityCompat


class NativeLocationHelper(context: Context, private var callback: OnLocationChangedCallback?) {
    private val context: Context = context.applicationContext
    private var locationManager: LocationManager?
    private var locationListener: LocationListener? = null

    interface OnLocationChangedCallback {
        fun onLocationChanged(location: Location)
        fun onStatusChanged(provider: String?, status: Int, extras: Bundle?)
        fun onProviderEnabled(provider: String?)
        fun onProviderDisabled(provider: String?)
    }

    init {
        this.locationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
    }

    fun startLocationUpdates() {
        if ((ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) &&
            (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED)
        ) {
            return
        }

        // 检查GPS和网络定位是否可用
        val isGPSEnabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                if (callback != null) {
                    callback!!.onLocationChanged(location)
                }
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                if (callback != null) {
                    callback!!.onStatusChanged(provider, status, extras)
                }
            }

            override fun onProviderEnabled(provider: String) {
                if (callback != null) {
                    callback!!.onProviderEnabled(provider)
                }
            }

            override fun onProviderDisabled(provider: String) {
                if (callback != null) {
                    callback!!.onProviderDisabled(provider)
                }
            }
        }

        // 优先使用GPS定位，如果不可用则使用网络定位
        if (isGPSEnabled) {
            locationManager!!.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MIN_TIME_BETWEEN_UPDATES,
                MIN_DISTANCE_CHANGE_FOR_UPDATES,
                locationListener!!,
                Looper.getMainLooper()
            )
        } else if (isNetworkEnabled) {
            locationManager!!.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                MIN_TIME_BETWEEN_UPDATES,
                MIN_DISTANCE_CHANGE_FOR_UPDATES,
                locationListener!!,
                Looper.getMainLooper()
            )
        }
    }

    fun stopLocationUpdates() {
        if (locationManager != null && locationListener != null) {
            locationManager!!.removeUpdates(locationListener!!)
        }
    }

    val lastKnownLocation: Location?
        get() {
            if ((ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                        != PackageManager.PERMISSION_GRANTED) &&
                (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
                        != PackageManager.PERMISSION_GRANTED)
            ) {
                return null
            }

            var location: Location? = null
            val isGPSEnabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled =
                locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            if (isGPSEnabled) {
                location = locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            }
            if (location == null && isNetworkEnabled) {
                location = locationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            }

            return location
        }

    fun destroy() {
        stopLocationUpdates()
        locationManager = null
        locationListener = null
        callback = null
    }

    companion object {
        private const val MIN_TIME_BETWEEN_UPDATES: Long = 5000 // 10秒
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES = 10f // 10米
    }
}