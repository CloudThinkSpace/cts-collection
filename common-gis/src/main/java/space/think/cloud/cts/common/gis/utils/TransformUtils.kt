package space.think.cloud.cts.common.gis.utils

import kotlin.math.*

object TransformUtils {

    private const val x_pi:Double = 3.141592653589793 * 3000.0 / 180.0

    // π
    private const val pi:Double = 3.141592653589793

    // 长半轴
    private const val a = 6378245.0

    // 扁率
    private const val ee = 0.006693421622965943

    private fun outOfChina(lon: Double, lat: Double): Boolean {
        if (lon < 72.004 || lon > 137.8347) {
            return true
        } else if (lat < 0.8293 || lat > 55.8271) {
            return true
        }
        return false
    }

    private fun transformLat(lon: Double, lat: Double): Double {
        var ret =
            -100.0 + 2.0 * lon + 3.0 * lat + 0.2 * lat * lat + 0.1 * lon * lat + 0.2 * sqrt(
                abs(lon)
            )
        ret += (20.0 * sin(6.0 * lon * pi) + 20.0 * sin(2.0 * lon * pi)) * 2.0 / 3.0
        ret += (20.0 * sin(lat * pi) + 40.0 * sin(lat / 3.0 * pi)) * 2.0 / 3.0
        ret += (160.0 * sin(lat / 12.0 * pi) + 320 * sin(lat * pi / 30.0)) * 2.0 / 3.0
        return ret
    }

    private fun transformLng(lon: Double, lat: Double): Double {
        var ret = 300.0 + lon + 2.0 * lat + 0.1 * lon * lon + 0.1 * lon * lat + 0.1 * sqrt(
            abs(lon)
        )
        ret += (20.0 * sin(6.0 * lon * pi) + 20.0 * sin(2.0 * lon * pi)) * 2.0 / 3.0
        ret += (20.0 * sin(lon * pi) + 40.0 * sin(lon / 3.0 * pi)) * 2.0 / 3.0
        ret += (150.0 * sin(lon / 12.0 * pi) + 300.0 * sin(lon / 30.0 * pi)) * 2.0 / 3.0
        return ret
    }

    /**
     * WGS84转GCJ02(火星坐标系)
     *
     * @param wgs_lon WGS84坐标系的经度
     * @param wgs_lat WGS84坐标系的纬度
     * @return 火星坐标数组
     */
    fun wgs84ToGcj02(wgs_lon: Double, wgs_lat: Double): DoubleArray {
        if (outOfChina(wgs_lon, wgs_lat)) {
            return doubleArrayOf(wgs_lon, wgs_lat)
        }
        var dlat = transformLat(wgs_lon - 105.0, wgs_lat - 35.0)
        var dlng = transformLng(wgs_lon - 105.0, wgs_lat - 35.0)
        val radLat = wgs_lat / 180.0 * pi
        var magic = sin(radLat)
        magic = 1 - ee * magic * magic
        val sqrtMagic = sqrt(magic)
        dlat = dlat * 180.0 / (a * (1 - ee) / (magic * sqrtMagic) * pi)
        dlng = dlng * 180.0 / (a / sqrtMagic * cos(radLat) * pi)
        val mgLat = wgs_lat + dlat
        val mgLng = wgs_lon + dlng
        return doubleArrayOf(mgLng, mgLat)
    }

    /**
     * GCJ02(火星坐标系)转GPS84
     *
     * @param gcj_lon 火星坐标系的经度
     * @param gcj_lat 火星坐标系纬度
     * @return WGS84坐标数组
     */
    fun gcj02ToWgs84(gcj_lon: Double, gcj_lat: Double): DoubleArray {
        if (outOfChina(gcj_lon, gcj_lat)) {
            return doubleArrayOf(gcj_lon, gcj_lat)
        }
        var dlat = transformLat(gcj_lon - 105.0, gcj_lat - 35.0)
        var dlng = transformLng(gcj_lon - 105.0, gcj_lat - 35.0)
        val radLat = gcj_lat / 180.0 * pi
        var magic = sin(radLat)
        magic = 1 - ee * magic * magic
        val sqrtMagic = sqrt(magic)
        dlat = dlat * 180.0 / (a * (1 - ee) / (magic * sqrtMagic) * pi)
        dlng = dlng * 180.0 / (a / sqrtMagic * cos(radLat) * pi)
        val mgLat = gcj_lat + dlat
        val mgLng = gcj_lon + dlng
        return doubleArrayOf(gcj_lon * 2 - mgLng, gcj_lat * 2 - mgLat)
    }

    /**
     * 火星坐标系(GCJ-02)转百度坐标系(BD-09)
     *
     * 谷歌、高德——>百度
     * @param gcj_lon 火星坐标经度
     * @param gcj_lat 火星坐标纬度
     * @return 百度坐标数组
     */
    fun gcj02ToBd09(gcj_lon: Double, gcj_lat: Double): DoubleArray {
        val z =
            sqrt(gcj_lon * gcj_lon + gcj_lat * gcj_lat) + 0.00002 * sin(gcj_lat * x_pi)
        val theta = atan2(gcj_lat, gcj_lon) + 0.000003 * cos(gcj_lon * x_pi)
        val bdLng = z * cos(theta) + 0.0065
        val bdLat = z * sin(theta) + 0.006
        return doubleArrayOf(bdLng, bdLat)
    }

    /**
     * 百度坐标系(BD-09)转火星坐标系(GCJ-02)
     *
     * 百度——>谷歌、高德
     * @param bd_lon 百度坐标纬度
     * @param bd_lat 百度坐标经度
     * @return 火星坐标数组
     */
    fun bd09ToGcj02(bd_lon: Double, bd_lat: Double): DoubleArray {
        val x = bd_lon - 0.0065
        val y = bd_lat - 0.006
        val z = sqrt(x * x + y * y) - 0.00002 * sin(y * x_pi)
        val theta = atan2(y, x) - 0.000003 * cos(x * x_pi)
        val ggLng = z * cos(theta)
        val ggLat = z * sin(theta)
        return doubleArrayOf(ggLng, ggLat)
    }

    /**
     * WGS坐标转百度坐标系(BD-09)
     *
     * @param wgs_lng WGS84坐标系的经度
     * @param wgs_lat WGS84坐标系的纬度
     * @return 百度坐标数组
     */
    fun wgs84ToBd09(wgs_lng: Double, wgs_lat: Double): DoubleArray {
        val gcj =
            wgs84ToGcj02(wgs_lng, wgs_lat)
        return gcj02ToBd09(gcj[0], gcj[1])
    }

    /**
     * 百度坐标系(BD-09)转WGS坐标
     *
     * @param bd_lng 百度坐标纬度
     * @param bd_lat 百度坐标经度
     * @return WGS84坐标数组
     */
    fun bd09ToWgs84(bd_lng: Double, bd_lat: Double): DoubleArray {
        val gcj =
            bd09ToGcj02(bd_lng, bd_lat)
        return gcj02ToWgs84(gcj[0], gcj[1])
    }
}