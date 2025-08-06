package space.think.cloud.cts.common_datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.dataStoreForm: DataStore<Preferences> by preferencesDataStore(name = "CloudThinkSpace-dataStore-form")


class FormFieldDataStore(private val context: Context, private val prefix: String) {
    // 存储数据
    suspend fun save(key: String, value: String) {
        context.dataStoreForm.edit { preferences ->
            preferences[stringPreferencesKey(prefix + key)] = value
        }
    }

    // 读取数据
    fun get(key: String, defaultValue: String): Flow<String> {
        return context.dataStoreForm.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[stringPreferencesKey(prefix + key)] ?: defaultValue
            }
    }

    // 删除数据
    suspend fun removeData(key: String) {
        context.dataStoreForm.edit { preferences ->
            preferences.remove(stringPreferencesKey(prefix + key))
        }
    }

    // 清除所有数据
    suspend fun clearAll() {
        context.dataStoreForm.edit { preferences ->
            preferences.clear()
        }
    }
}