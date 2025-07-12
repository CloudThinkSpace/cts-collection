package space.think.cloud.cts.collection

import android.app.Application
import space.think.cloud.cts.lib.network.RetrofitClient

class CtsApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        // 初始化网络请求对象
        RetrofitClient.initialize(this)
    }
}