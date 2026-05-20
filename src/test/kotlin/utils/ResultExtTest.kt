package utils

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ResultExtTest {

    @Test
    fun `mapChain propagates success`() {
        val result = Result.success(2).mapChain { value ->
            Result.success(value * 3)
        }
        assertEquals(6, result.getOrThrow())
    }

    @Test
    fun `mapChain propagates first failure`() {
        val error = IllegalStateException("boom")
        val result = Result.success(1).mapChain {
            Result.failure<Int>(error)
        }.mapChain {
            Result.success(99)
        }
        assertTrue(result.isFailure)
        assertEquals(error, result.exceptionOrNull())
    }

    @Test
    fun `mapChain skips transform after failure`() {
        var called = false
        val result = Result.failure<Int>(IllegalArgumentException("nope")).mapChain {
            called = true
            Result.success(1)
        }
        assertTrue(result.isFailure)
        assertEquals(false, called)
    }
}
