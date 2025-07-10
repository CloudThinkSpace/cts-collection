package space.think.cloud.cts.lib.watermark.utils

object StringUtil {

    // 计算字符串被分割多少行
    fun lines(input: String, chunkSize: Int): List<String> {
        require(chunkSize > 0)
        return input.chunked(chunkSize)
    }
}