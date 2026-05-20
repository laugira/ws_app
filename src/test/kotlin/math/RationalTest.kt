package math

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RationalTest {

    @Test
    fun `invoke normalizes fraction`() {
        val r = Rational(4, 8).getOrThrow()
        assertEquals(1, r.numerator)
        assertEquals(2, r.denominator)
    }

    @Test
    fun `invoke with negative denominator normalizes sign`() {
        val r = Rational(1, -2).getOrThrow()
        assertEquals(-1, r.numerator)
        assertEquals(2, r.denominator)
    }

    @Test
    fun `invoke rejects zero denominator`() {
        val result = Rational(1, 0)
        assertTrue(result.isFailure)
    }

    @Test
    fun `plus reduces result`() {
        val half = Rational(1, 2).getOrThrow()
        val sum = half + half
        assertEquals(Rational(1), sum)
    }

    @Test
    fun `minus and times`() {
        val threeQuarters = Rational(3, 4).getOrThrow()
        val oneQuarter = Rational(1, 4).getOrThrow()
        assertEquals(Rational(1, 2).getOrThrow(), threeQuarters - oneQuarter)
        assertEquals(Rational(3, 16).getOrThrow(), threeQuarters * oneQuarter)
    }

    @Test
    fun `div`() {
        val half = Rational(1, 2).getOrThrow()
        assertEquals(Rational(1), half / half)
    }

    @Test
    fun `compareTo orders fractions`() {
        assertTrue(Rational(1, 3).getOrThrow() < Rational(1, 2).getOrThrow())
        assertEquals(0, Rational(2, 4).getOrThrow().compareTo(Rational(1, 2).getOrThrow()))
    }
}
