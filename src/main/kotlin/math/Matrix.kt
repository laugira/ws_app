package math

class Matrix private constructor(
    private val data: List<List<Rational>>, val defaultValue: Rational
) {
    fun rowCount() = data.size

    fun colCount() = data.getOrNull(0)?.size ?: 0

    operator fun get(row: Int, col: Int): Result<Rational> = if (row in 0..<rowCount() && col in 0..<colCount()) {
        Result.success(data[row][col])
    } else {
        Result.failure(IllegalArgumentException("row index (${row}) is out of row bound (${rowCount() - 1}), or col index (${col}) is out of col bound (${colCount() - 1})"))
    }

    fun isSquared(): Boolean = rowCount() == colCount()

    fun rowElements(row: Int): Result<List<Rational>> = if (row in 0..<rowCount()) {
        Result.success(data[row].map { row -> row })
    } else {
        Result.failure(IllegalArgumentException("row index (${row}) is out of row bound (${rowCount() - 1})"))
    }

    fun colElements(col: Int): Result<List<Rational>> = if (col in 0..<rowCount()) {
        Result.success(data.map { it[col] })
    } else {
        Result.failure(IllegalArgumentException("col index (${col}) is out of col bound (${colCount() - 1})"))
    }

    override fun toString(): String {
        var maxLength = 0
        data.forEach { row ->
            row.forEach { value ->
                if (value.toString().length > maxLength) maxLength = value.toString().length
            }
        }
        maxLength += 2

        val sb = StringBuilder()
        data.forEach { row ->
            row.forEach { value ->
                sb.append(value.toString().padStart(maxLength))
            }
            sb.appendLine()
        }

        return sb.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Matrix

        return data == other.data
    }

    override fun hashCode(): Int {
        return data.hashCode()
    }

    companion object {
        operator fun invoke(data: List<List<Rational?>>, defaultValue: Rational = Rational(0)): Result<Matrix> =
            if (data.isEmpty() || data.all { it.size == data[0].size }) {
                val dataNotNull: List<List<Rational>> = data.map { it.map { row -> row ?: defaultValue } }
                Result.success(Matrix(dataNotNull, defaultValue))
            } else {
                Result.failure(IllegalArgumentException("at least two different rows have different size"))
            }
    }
}