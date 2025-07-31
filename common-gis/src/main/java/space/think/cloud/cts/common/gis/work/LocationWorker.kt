package space.think.cloud.cts.common.gis.work

import android.content.Context
import android.location.Location
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import space.think.cloud.cts.common.gis.location.EnhancedNativeLocationHelper
import space.think.cloud.cts.common_datastore.DataStoreUtil
import space.think.cloud.cts.common_datastore.PreferencesKeys
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class LocationWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val dataStoreUtil = DataStoreUtil(context)

    override suspend fun doWork(): Result {
        return try {
            // 获取位置
            val location = fetchLocation()

            // 处理位置数据
            location?.let {
                saveLocation(it)
                Result.success()
            } ?: Result.retry()
        } catch (_: Exception) {
            Result.failure()
        }
    }

    private suspend fun fetchLocation(): Location? = withContext(Dispatchers.IO) {
        val locationHelper = EnhancedNativeLocationHelper(applicationContext)
        val latch = CountDownLatch(1)
        var resultLocation: Location? = null

        locationHelper.startLocationUpdates(
            config = EnhancedNativeLocationHelper.LocationConfig(
                minTime = 5000,
                minDistance = 0f,
                timeout = 30000 // 30秒超时
            )
        ) { state ->
            when (state) {
                is EnhancedNativeLocationHelper.LocationState.Located -> {
                    resultLocation = state.location
                    latch.countDown()
                }

                is EnhancedNativeLocationHelper.LocationState.Timeout,
                is EnhancedNativeLocationHelper.LocationState.Error -> {
                    latch.countDown()
                }

                else -> {}
            }
        }

        latch.await(30, TimeUnit.SECONDS)
        locationHelper.stopLocationUpdates()

        return@withContext resultLocation
    }

    private suspend fun saveLocation(location: Location) {
        dataStoreUtil.saveData(PreferencesKeys.LON_KEY, location.longitude)
        dataStoreUtil.saveData(PreferencesKeys.LAT_KEY, location.latitude)
    }
}