package space.think.cloud.cts.common.gis.location

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import space.think.cloud.cts.common_datastore.DataStoreUtil
import space.think.cloud.cts.common_datastore.PreferencesKeys


class CtsLocationManager(private val context: ComponentActivity) {

    private val locationHelper = EnhancedNativeLocationHelper(context)
    private val dataStoreUtil = DataStoreUtil(context)
    private val syncScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // 定义权限请求launcher
    private val requestPermissionLauncher = context.registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                // 精确位置权限已授予
                startLocationUpdates()
            }

            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // 粗略位置权限已授予
                startLocationUpdates()
            }

            else -> {
                checkAndRequestPermissions()
            }
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun showEnableLocationDialog() {
        AlertDialog.Builder(context)
            .setTitle("启用位置服务")
            .setMessage("请开启位置服务以使用此功能")
            .setPositiveButton("去设置") { _, _ ->
                context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .setNegativeButton("取消", null)
            .show()
    }

    // 检查并请求权限
    fun checkAndRequestPermissions() {
        when {
            // 检查是否已有精确位置权限
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                startLocationUpdates()
            }

            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                // 有粗略位置权限
                startLocationUpdates()
            }
            // 检查是否应该显示权限说明
            ActivityCompat.shouldShowRequestPermissionRationale(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) -> {
                showPermissionRationaleDialog()
            }

            // 直接请求权限
            else -> {
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }

    fun startLocationUpdates() {
        locationHelper.startLocationUpdates(
            config = EnhancedNativeLocationHelper.LocationConfig(
                minTime = 10000,
                minDistance = 10f,
            )
        ) { state ->
            when (state) {
                is EnhancedNativeLocationHelper.LocationState.Located -> {
                    syncScope.launch {
                        saveLocation(state.location)
                    }
                }

                is EnhancedNativeLocationHelper.LocationState.Timeout,
                is EnhancedNativeLocationHelper.LocationState.Error -> {
                    Toast.makeText(
                        context,
                        "位置获取超时",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else -> {}
            }
        }
    }

    fun stopLocationUpdates() {
        locationHelper.stopLocationUpdates()
    }

    private fun showPermissionDeniedMessage() {
        AlertDialog.Builder(context)
            .setTitle("需要位置权限")
            .setMessage("此功能需要位置权限才能正常工作。请前往设置授予权限。")
            .setPositiveButton("去设置") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                }
                context.startActivity(intent)
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun showPermissionRationaleDialog() {
        AlertDialog.Builder(context)
            .setTitle("需要位置权限")
            .setMessage(
                "此功能需要访问您的位置信息，用于:\n\n" +
                        "• 提供基于位置的服务\n" +
                        "• 显示附近的相关内容\n" +
                        "• 改善用户体验\n\n" +
                        "我们承诺保护您的隐私，位置数据仅用于上述用途。"
            )
            .setPositiveButton("同意") { _, _ ->
                // 用户理解后再次请求权限
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
            .setNegativeButton("拒绝", null)
            .setCancelable(false)
            .show()
    }

    private suspend fun saveLocation(location: Location) {
        dataStoreUtil.saveData(PreferencesKeys.LON_KEY, location.longitude)
        dataStoreUtil.saveData(PreferencesKeys.LAT_KEY, location.latitude)
        // 是否有方位信息
        if (location.hasBearing()){
            dataStoreUtil.saveData(PreferencesKeys.BEARING_KEY, location.bearing)
        }
        // 是否有高程信息
        if (location.hasAltitude()){
            dataStoreUtil.saveData(PreferencesKeys.ALTITUDE_KEY, location.altitude)
        }
    }


}