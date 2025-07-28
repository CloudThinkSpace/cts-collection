package space.think.cloud.cts.lib.network

import android.content.Context
import kotlinx.coroutines.flow.Flow
import space.think.cloud.cts.common_datastore.DataStoreUtil
import space.think.cloud.cts.common_datastore.PreferencesKeys

class TokenManager(private val context: Context) {

    private var dataStoreUtil: DataStoreUtil = DataStoreUtil(context)

    // 获取存储的 token
    val tokenFlow: Flow<String?> = dataStoreUtil.getData(PreferencesKeys.TOKEN_KEY,"")
}