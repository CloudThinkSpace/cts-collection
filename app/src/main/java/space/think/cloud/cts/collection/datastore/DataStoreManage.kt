package space.think.cloud.cts.collection.datastore

import android.content.Context
import kotlinx.coroutines.flow.first
import space.think.cloud.cts.common_datastore.DataStoreUtil
import space.think.cloud.cts.common_datastore.PreferencesKeys
import space.think.cloud.cts.lib.network.model.response.ResponseAuth

class DataStoreManage(context: Context) {

    var dataStoreUtil: DataStoreUtil = DataStoreUtil(context)

    suspend fun saveUser(auth: ResponseAuth) {
        dataStoreUtil.saveData(
            PreferencesKeys.NICKNAME_KEY,
            auth.userInfo.nickname
        )
        dataStoreUtil.saveData(
            PreferencesKeys.USERNAME_KEY,
            auth.userInfo.username
        )
        dataStoreUtil.saveData(
            PreferencesKeys.TOKEN_KEY,
            auth.token.accessToken
        )
        dataStoreUtil.saveData(
            PreferencesKeys.PHONE_KEY,
            auth.userInfo.phone
        )
    }

    suspend fun removeUser() {
        dataStoreUtil.removeData(PreferencesKeys.NICKNAME_KEY)
        dataStoreUtil.removeData(PreferencesKeys.USERNAME_KEY)
        dataStoreUtil.removeData(PreferencesKeys.PHONE_KEY)
        dataStoreUtil.removeData(PreferencesKeys.TOKEN_KEY)
    }

    suspend fun getNickname(): String {
        return dataStoreUtil.getData(PreferencesKeys.NICKNAME_KEY, "").first()
    }

    suspend fun getPhone(): String {
        return dataStoreUtil.getData(PreferencesKeys.PHONE_KEY, "").first()
    }

    suspend fun getToken(): String {
        return dataStoreUtil.getData(PreferencesKeys.TOKEN_KEY, "").first()
    }
}