package space.think.cloud.cts.lib.network.model.response

/**
 * ClassName: 基础类
 * Description:
 * @date: 2022/11/1 17:24
 * @author: tanghy
 */
open class BaseData(
    val data: Map<String, Any?>
) {
    val id: String =
        if (data.containsKey(BaseEnum.ID.value)) data[BaseEnum.ID.value].toString() else ""
    val code: String =
        if (data.containsKey(BaseEnum.CODE.value)) data[BaseEnum.CODE.value].toString() else ""
    val lon: Double =
        if (data.containsKey(BaseEnum.LON.value)) (data[BaseEnum.LON.value].toString()).toDouble() else 0.0
    val lat: Double =
        if (data.containsKey(BaseEnum.LAT.value)) (data[BaseEnum.LAT.value].toString()).toDouble() else 0.0
    var status: Int =
        if (data.containsKey(BaseEnum.STATUS.value)) (data[BaseEnum.STATUS.value].toString()).toDouble()
            .toInt() else 0
}

enum class BaseEnum(val value: String) {

    ID("id"),
    CODE("code"),
    LON("lon"),
    LAT("lat"),
    STATUS("status"),

}
