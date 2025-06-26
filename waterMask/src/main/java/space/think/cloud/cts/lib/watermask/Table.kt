package space.think.cloud.cts.lib.watermask

data class Table(
    val rows: FloatArray,
    val columns: FloatArray,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Table

        if (!rows.contentEquals(other.rows)) return false
        if (!columns.contentEquals(other.columns)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = rows.contentHashCode()
        result = 31 * result + columns.contentHashCode()
        return result
    }

    fun width(): Float {
        return columns.sum()
    }

    fun height(): Float {
        return rows.sum()
    }
}