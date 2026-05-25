package common.math

import kotlin.math.abs

class Rational private constructor(val numerator: Int, val denominator: Int) {
    fun toDouble() = numerator * 1.0 / denominator
    fun toFloat() = numerator * 1.0f / denominator

    override fun toString() = if (denominator != 1) "$numerator/$denominator" else "$numerator"

    operator fun plus(that: Rational): Rational {
        val numerator = this.numerator * that.denominator + that.numerator * this.denominator
        val denominator = that.denominator * this.denominator
        return invoke(numerator, denominator).getOrThrow()
    }

    operator fun minus(that: Rational): Rational =
        plus(invoke(that.numerator * -1, that.denominator).getOrThrow())

    operator fun times(that: Rational): Rational {
        val numerator = this.numerator * that.numerator
        val denominator = that.denominator * this.denominator
        return invoke(numerator, denominator).getOrThrow()
    }

    operator fun div(that: Rational): Rational = times(invoke(denominator, numerator).getOrThrow())

    operator fun compareTo(that: Rational): Int = (this - that).numerator

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Rational

        if (numerator != other.numerator) return false
        if (denominator != other.denominator) return false

        return true
    }

    override fun hashCode(): Int {
        var result = numerator
        result = 31 * result + denominator
        return result
    }

    companion object {
        operator fun invoke(numerator: Int, denominator: Int): Result<Rational> = if (denominator == 0) {
            Result.failure(
                IllegalArgumentException("denominator argument can't be equal to zero")
            )
        } else {
            if (denominator < 0) {
                invoke(numerator * -1, denominator * -1)
            } else {
                val gcd = gcd(abs(numerator), abs(denominator))
                Result.success(Rational(numerator / gcd, denominator / gcd))
            }
        }


        operator fun invoke(numerator: Int): Rational = Rational(numerator, 1)

        private tailrec fun gcd(x: Int, y: Int): Int = if (y == 0) x else gcd(y, x % y)

    }
}