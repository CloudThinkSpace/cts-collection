package space.think.cloud.cts.common.gis.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.net.toUri

object MapNavigationUtil {

    /**
     * <h2>高德导航</h2>
     * @param context activity对象
     * @param latitude 经度
     * @param longitude 纬度
     */
    fun gaodeIntent(context: Context, latitude: Double, longitude: Double) {
        try {
            val naviIntent = Intent(
                "android.intent.action.VIEW",
                "androidamap://route?sourceApplication=appName&slat=&slon=&sname=我的位置&dlat=$latitude&dlon=$longitude&dev=0&t=2".toUri()
            )
            context.startActivity(naviIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "请安装高德地图后重试", Toast.LENGTH_SHORT).show()
        }
    }

    fun baiduIntent(context: Context, latitude: Double, longitude: Double) {
        try {
            val intent = Intent(
                "android.intent.action.VIEW",
                "baidumap://map/geocoder?location=$latitude,$longitude".toUri()
            )
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "请安装百度地图后重试", Toast.LENGTH_SHORT).show()
        }
    }
}