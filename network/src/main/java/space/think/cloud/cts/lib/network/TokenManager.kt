package space.think.cloud.cts.lib.network

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import space.think.cloud.cts.common_datastore.DataStoreUtil
import space.think.cloud.cts.common_datastore.PreferencesKeys

class TokenManager(private val dataStoreUtil: DataStoreUtil) {
    private var cachedToken: String? = null
    private val mutex = Mutex()

    suspend fun getToken(): String = mutex.withLock {
        cachedToken ?: dataStoreUtil.getData(PreferencesKeys.TOKEN_KEY, "").first().also { cachedToken = it }
    }

    suspend fun updateToken(newToken: String) {
        mutex.withLock {
            cachedToken = newToken
            dataStoreUtil.saveData(PreferencesKeys.TOKEN_KEY, newToken)
        }
    }

    suspend fun clearToken() {
        mutex.withLock {
            cachedToken = null
            dataStoreUtil.removeData(PreferencesKeys.TOKEN_KEY)
        }
    }
}