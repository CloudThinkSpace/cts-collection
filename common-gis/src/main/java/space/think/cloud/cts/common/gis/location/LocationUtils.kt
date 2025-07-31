package space.think.cloud.cts.common.gis.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.app.ActivityCompat

class LocationUtils(private val context: Context) {

    private val locationManager: LocationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private var locationListener: LocationListener? = null
    private val mainHandler = Handler(Looper.getMainLooper())
    private var timeoutRunnable: Runnable? = null

    // 定位结果回调接口
    interface LocationCallback {
        fun onLocationReceived(location: Location) // 位置更新
        fun onLocationError(error: LocationError)  // 错误信息
    }

    // 定位状态枚举
    enum class LocationState {
        IDLE,          // 空闲状态
        SINGLE_REQUEST, // 单次定位中
        CONTINUOUS      // 连续定位中
    }

    // 定位错误类型
    enum class LocationError {
        PERMISSION_DENIED,     // 权限被拒绝
        PROVIDER_DISABLED,      // 定位服务未开启
        TIMEOUT,                // 定位超时
        NO_PROVIDER_AVAILABLE   // 无可用定位源
    }

    private var currentState = LocationState.IDLE

    /**
     * 单次定位（带超时处理）
     * @param minTime 最小更新时间（毫秒）
     * @param minDistance 最小移动距离（米）
     * @param timeout 超时时间（毫秒，0表示无超时）
     * @param callback 定位回调
     */
    @SuppressLint("MissingPermission")
    fun getSingleLocation(
        minTime: Long = 1000,
        minDistance: Float = 1f,
        timeout: Long = 15000,
        callback: LocationCallback
    ) {
        if (currentState != LocationState.IDLE) {
            callback.onLocationError(LocationError.NO_PROVIDER_AVAILABLE)
            return
        }

        if (!hasLocationPermission(context)) {
            callback.onLocationError(LocationError.PERMISSION_DENIED)
            return
        }

        if (!isLocationEnabled()) {
            callback.onLocationError(LocationError.PROVIDER_DISABLED)
            return
        }

        currentState = LocationState.SINGLE_REQUEST
        startLocationUpdates(minTime, minDistance, callback)

        // 设置超时处理
        if (timeout > 0) {
            timeoutRunnable = Runnable {
                callback.onLocationError(LocationError.TIMEOUT)
                stopLocationUpdates()
            }
            mainHandler.postDelayed(timeoutRunnable!!, timeout)
        }
    }

    /**
     * 启动连续定位
     * @param minTime 更新间隔（毫秒）
     * @param minDistance 最小移动距离（米）
     * @param callback 定位回调
     */
    @SuppressLint("MissingPermission")
    fun startContinuousLocationUpdates(
        minTime: Long = 5000,
        minDistance: Float = 5f,
        callback: LocationCallback
    ) {
        if (currentState == LocationState.CONTINUOUS) return

        if (!hasLocationPermission(context)) {
            callback.onLocationError(LocationError.PERMISSION_DENIED)
            return
        }

        if (!isLocationEnabled()) {
            callback.onLocationError(LocationError.PROVIDER_DISABLED)
            return
        }

        currentState = LocationState.CONTINUOUS
        startLocationUpdates(minTime, minDistance, callback)
    }

    /**
     * 停止连续定位
     */
    fun stopLocationUpdates() {
        locationListener?.let {
            locationManager.removeUpdates(it)
            locationListener = null
        }

        timeoutRunnable?.let {
            mainHandler.removeCallbacks(it)
            timeoutRunnable = null
        }

        currentState = LocationState.IDLE
    }

    /**
     * 获取最后已知位置
     */
    @SuppressLint("MissingPermission")
    fun getLastKnownLocation(callback: (Location?) -> Unit) {
        if (!hasLocationPermission(context)) return callback(null)

        val providers = locationManager.getProviders(true)
        var bestLocation: Location? = null

        providers.forEach { provider ->
            val location = locationManager.getLastKnownLocation(provider)
            if (location != null && (bestLocation == null ||
                        location.accuracy < bestLocation!!.accuracy)) {
                bestLocation = location
            }
        }

        callback(bestLocation)
    }

    /**
     * 检查定位服务是否开启
     */
    fun isLocationEnabled(): Boolean {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    // 获取定位状态
    fun getLocationState() = currentState

    // 内部方法：启动位置监听
    private fun startLocationUpdates(
        minTime: Long,
        minDistance: Float,
        callback: LocationCallback
    ) {
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                callback.onLocationReceived(location)

                // 单次定位获取后自动停止
                if (currentState == LocationState.SINGLE_REQUEST) {
                    stopLocationUpdates()
                }
            }

            override fun onProviderDisabled(provider: String) {
                callback.onLocationError(LocationError.PROVIDER_DISABLED)
                if (currentState == LocationState.SINGLE_REQUEST) {
                    stopLocationUpdates()
                }
            }

            override fun onProviderEnabled(provider: String) {}
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        }

        try {
            // 优先使用GPS，其次网络
            val providers = listOf(
                LocationManager.GPS_PROVIDER,
                LocationManager.NETWORK_PROVIDER
            ).filter { locationManager.isProviderEnabled(it) }

            if (providers.isEmpty()) {
                callback.onLocationError(LocationError.NO_PROVIDER_AVAILABLE)
                currentState = LocationState.IDLE
                return
            }

            providers.forEach { provider ->
                locationManager.requestLocationUpdates(
                    provider,
                    minTime,
                    minDistance,
                    locationListener!!,
                    Looper.getMainLooper()
                )
            }
        } catch (e: SecurityException) {
            callback.onLocationError(LocationError.PERMISSION_DENIED)
            currentState = LocationState.IDLE
        }
    }

    companion object {
        fun hasLocationPermission(context: Context): Boolean {
            return ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        }
    }
}