package space.think.cloud.cts.common.gis.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat
import java.util.concurrent.Executors

class EnhancedNativeLocationHelper(private val context: Context) {

    // 定位管理器
    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    // 线程池用于异步操作
    private val executor = Executors.newSingleThreadExecutor()

    // 超时处理
    private val handler = Handler(Looper.getMainLooper())

    // 定位监听器
    private var locationListener: LocationListener? = null
    private var passiveLocationListener: LocationListener? = null

    // 配置参数
    data class LocationConfig(
        val minTime: Long = 10000,          // 最小时间间隔(毫秒)
        val minDistance: Float = 10f,       // 最小距离变化(米)
        val timeout: Long = 30000,          // 超时时间(毫秒)
        val desiredAccuracy: Float = 50f,   // 期望精度(米)
        val useGPS: Boolean = true,         // 是否使用GPS
        val useNetwork: Boolean = true,     // 是否使用网络定位
        val usePassive: Boolean = false,     // 是否使用被动定位
        val continuousUpdates: Boolean = true // 新增参数，默认关闭
    )

    // 定位状态
    sealed class LocationState {
        object Waiting : LocationState()
        object Searching : LocationState()
        data class Located(val location: Location) : LocationState()
        data class Error(val message: String) : LocationState()
        object Timeout : LocationState()
        object PermissionDenied : LocationState()
    }

    // 检查定位权限
    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    // 检查定位服务是否启用
    fun isLocationEnabled(): Boolean {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    // 获取所有可用的定位源
    fun getAvailableProviders(): List<String> {
        return locationManager.allProviders.filter { provider ->
            locationManager.isProviderEnabled(provider)
        }
    }

    // 获取最后一次已知位置（优化版）
    fun getLastKnownLocation(providerPriority: List<String> = listOf(
        LocationManager.GPS_PROVIDER,
        LocationManager.NETWORK_PROVIDER,
        LocationManager.PASSIVE_PROVIDER
    )): Location? {
        if (!hasLocationPermission()) return null

        return providerPriority.firstNotNullOfOrNull { provider ->
            try {
                locationManager.getLastKnownLocation(provider)
            } catch (e: SecurityException) {
                null
            } catch (e: Exception) {
                null
            }
        }
    }

    // 开始实时位置更新（完整版）
    @SuppressLint("MissingPermission")
    fun startLocationUpdates(
        config: LocationConfig = LocationConfig(),
        onStateChange: (LocationState) -> Unit
    ) {
        if (!hasLocationPermission()) {
            onStateChange(LocationState.PermissionDenied)
            return
        }

        if (!isLocationEnabled()) {
            onStateChange(LocationState.Error("Location services are disabled"))
            return
        }

        // 清除之前的监听
        stopLocationUpdates()

        onStateChange(LocationState.Searching)

        // 主定位监听器
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                if (!config.continuousUpdates && location.accuracy <= config.desiredAccuracy) {
                    // 达到期望精度，停止更新
                    stopLocationUpdates()
                }
                onStateChange(LocationState.Located(location))
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                when (status) {
                    LocationProvider.AVAILABLE -> {}
                    LocationProvider.TEMPORARILY_UNAVAILABLE ->
                        onStateChange(LocationState.Error("Temporarily unavailable"))
                    LocationProvider.OUT_OF_SERVICE ->
                        onStateChange(LocationState.Error("Service out of range"))
                }
            }

            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }.also { listener ->
            try {
                // 根据配置启用不同定位源
                if (config.useGPS && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        config.minTime,
                        config.minDistance,
                        listener,
                        Looper.getMainLooper()
                    )
                }

                if (config.useNetwork && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        config.minTime,
                        config.minDistance,
                        listener,
                        Looper.getMainLooper()
                    )
                }
            } catch (e: SecurityException) {
                onStateChange(LocationState.PermissionDenied)
            } catch (e: Exception) {
                onStateChange(LocationState.Error(e.localizedMessage ?: "Unknown error"))
            }
        }

        // 被动定位监听（可选）
        if (config.usePassive) {
            passiveLocationListener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    // 被动定位通常精度较低，只作为参考
                    onStateChange(LocationState.Located(location))
                }

                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                override fun onProviderEnabled(provider: String) {}
                override fun onProviderDisabled(provider: String) {}
            }.also { listener ->
                try {
                    locationManager.requestLocationUpdates(
                        LocationManager.PASSIVE_PROVIDER,
                        0, 0f, listener, Looper.getMainLooper()
                    )
                } catch (e: Exception) {
                    // 被动定位失败不影响主流程
                }
            }
        }
    }

    // 停止所有位置更新
    fun stopLocationUpdates() {

        locationListener?.let {
            locationManager.removeUpdates(it)
            locationListener = null
        }

        passiveLocationListener?.let {
            locationManager.removeUpdates(it)
            passiveLocationListener = null
        }
    }

    // 单次定位请求（简化版）
    @SuppressLint("MissingPermission")
    fun requestSingleLocation(
        config: LocationConfig = LocationConfig(),
        onResult: (LocationResult) -> Unit
    ) {
        executor.execute {
            if (!hasLocationPermission()) {
                onResult(LocationResult.Failure("Location permission not granted"))
                return@execute
            }

            val providers = mutableListOf<String>().apply {
                if (config.useGPS && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    add(LocationManager.GPS_PROVIDER)
                }
                if (config.useNetwork && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    add(LocationManager.NETWORK_PROVIDER)
                }
            }

            if (providers.isEmpty()) {
                onResult(LocationResult.Failure("No available location providers"))
                return@execute
            }

            var bestLocation: Location? = null

            providers.forEach { provider ->
                try {
                    val location = locationManager.getLastKnownLocation(provider)
                    location?.let {
                        if (bestLocation == null ||
                            (it.accuracy < bestLocation!!.accuracy &&
                                    it.time > bestLocation!!.time - 1000 * 60 * 5)) { // 5分钟内有效
                            bestLocation = it
                        }
                    }
                } catch (e: Exception) {
                    // 忽略单个provider的错误
                }
            }

            bestLocation?.let {
                if (it.accuracy <= config.desiredAccuracy) {
                    onResult(LocationResult.Success(it))
                } else {
                    // 精度不够，尝试获取更精确的位置
                    var receivedUpdate = false

                    val tempListener = object : LocationListener {
                        override fun onLocationChanged(location: Location) {
                            if (!receivedUpdate && location.accuracy <= config.desiredAccuracy) {
                                receivedUpdate = true
                                locationManager.removeUpdates(this)
                                onResult(LocationResult.Success(location))
                            }
                        }

                        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                        override fun onProviderEnabled(provider: String) {}
                        override fun onProviderDisabled(provider: String) {}
                    }

                    try {
                        locationManager.requestSingleUpdate(
                            providers.first(),
                            tempListener,
                            Looper.getMainLooper()
                        )

                        // 设置超时
                        handler.postDelayed({
                            if (!receivedUpdate) {
                                locationManager.removeUpdates(tempListener)
                                onResult(LocationResult.Success(bestLocation!!))
                            }
                        }, config.timeout)
                    } catch (e: Exception) {
                        onResult(LocationResult.Success(bestLocation!!))
                    }
                }
            } ?: run {
                onResult(LocationResult.Failure("Unable to retrieve location"))
            }
        }
    }

    sealed class LocationResult {
        data class Success(val location: Location) : LocationResult()
        data class Failure(val message: String) : LocationResult()
    }

    // 获取地址信息（反向地理编码）
    fun getAddressFromLocation(
        latitude: Double,
        longitude: Double,
        maxResults: Int = 1,
        onResult: (GeocoderResult) -> Unit
    ) {
        executor.execute {
            if (!Geocoder.isPresent()) {
                onResult(GeocoderResult.Failure("Geocoder not available"))
                return@execute
            }

            try {
                val geocoder = Geocoder(context)
                val addresses = geocoder.getFromLocation(latitude, longitude, maxResults)

                if (addresses.isNullOrEmpty()) {
                    onResult(GeocoderResult.Failure("No address found"))
                } else {
                    onResult(GeocoderResult.Success(addresses))
                }
            } catch (e: Exception) {
                onResult(GeocoderResult.Failure(e.localizedMessage ?: "Geocoding failed"))
            }
        }
    }

    sealed class GeocoderResult {
        data class Success(val addresses: List<Address>) : GeocoderResult()
        data class Failure(val message: String) : GeocoderResult()
    }

    // 清理资源
    fun dispose() {
        stopLocationUpdates()
        executor.shutdown()
    }
}