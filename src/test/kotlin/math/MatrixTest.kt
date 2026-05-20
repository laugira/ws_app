package math

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MatrixTest {

    @Test
    fun `invoke fills null cells with default value`() {
        val matrix = Matrix(
            listOf(
                listOf(Rational(1), null),
                listOf(null, Rational(2)),
            ),
        ).getOrThrow()
        assertEquals(Rational(1), matrix[0, 0].getOrThrow())
        assertEquals(Rational(0), matrix[0, 1].getOrThrow())
        assertEquals(Rational(0), matrix[1, 0].getOrThrow())
        assertEquals(Rational(2), matrix[1, 1].getOrThrow())
    }

    @Test
    fun `invoke accepts empty matrix`() {
        val matrix = Matrix(emptyList()).getOrThrow()
        assertEquals(0, matrix.rowCount())
        assertEquals(0, matrix.colCount())
        assertTrue(matrix.isSquared())
    }

    @Test
    fun `invoke fails when rows have different lengths`() {
        val result = Matrix(
            listOf(
                listOf(Rational(1)),
                listOf(Rational(2), Rational(3)),
            ),
        )
        assertTrue(result.isFailure)
    }

    @Test
    fun `get fails when index is out of bounds`() {
        val matrix = Matrix(listOf(listOf(Rational(1)))).getOrThrow()
        assertTrue(matrix[1, 0].isFailure)
        assertTrue(matrix[0, 1].isFailure)
    }

    @Test
    fun `rowElements and colElements succeed for valid index`() {
        val matrix = Matrix(
            listOf(
                listOf(Rational(1), Rational(2)),
                listOf(Rational(3), Rational(4)),
            ),
        ).getOrThrow()
        assertEquals(listOf(Rational(1), Rational(2)), matrix.rowElements(0).getOrThrow())
        assertEquals(listOf(Rational(1), Rational(3)), matrix.colElements(0).getOrThrow())
    }
}
