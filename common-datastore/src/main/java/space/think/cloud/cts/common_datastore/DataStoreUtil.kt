package space.think.cloud.cts.common_datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "CloudThinkSpace")

class DataStoreUtil(private val context: Context) {

    // 存储数据
    suspend fun <T> saveData(key: Preferences.Key<T>, value: T) {
        context.dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    // 读取数据
    fun <T> getData(key: Preferences.Key<T>, defaultValue: T): Flow<T> {
        return context.dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[key] ?: defaultValue
            }
    }

    // 删除数据
    suspend fun <T> removeData(key: Preferences.Key<T>) {
        context.dataStore.edit { preferences ->
            preferences.remove(key)
        }
    }

    // 清除所有数据
    suspend fun clearAll() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}

// 定义常用的 Preferences Key
object PreferencesKeys {

    // 经度信息
    val LON_KEY = doublePreferencesKey("cloud_think_space_lon_key")
    // 纬度信息
    val LAT_KEY = doublePreferencesKey("cloud_think_space_lat_key")
    // 高程信息
    val ALTITUDE_KEY = doublePreferencesKey("cloud_think_space_altitude_key")
    // 方位角信息
    val BEARING_KEY = floatPreferencesKey("cloud_think_space_bearing_key")
    // token信息
    val TOKEN_KEY = stringPreferencesKey("cloud_think_space_token_key")
    // 昵称信息
    val NICKNAME_KEY = stringPreferencesKey("cloud_think_space_nickname_key")
    // 用户信息
    val USERNAME_KEY = stringPreferencesKey("cloud_think_space_username_key")
    // 电话信息
    val PHONE_KEY = stringPreferencesKey("cloud_think_space_phone_key")
    // 邮箱信息
    val EMAIL_KEY = stringPreferencesKey("cloud_think_space_email_key")

}