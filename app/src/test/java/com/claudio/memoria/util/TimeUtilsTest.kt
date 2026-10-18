package com.claudio.memoria.util

import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime

class TimeUtilsTest {

    @Test
    fun `next reminder is in the future and within one day`() {
        val now = LocalDateTime.of(2026, 10, 18, 11, 0)
        val millis = TimeUtils.millisUntilNext(10, 0, now)
        assertTrue(millis >= 60_000L)
        assertTrue(millis <= 24 * 60 * 60 * 1000L + 60_000L)
    }

    @Test
    fun `same day reminder is about one hour away`() {
        val now = LocalDateTime.of(2026, 10, 18, 9, 0)
        val millis = TimeUtils.millisUntilNext(10, 0, now)
        val hour = 60 * 60 * 1000L
        assertTrue(millis in (hour - 60_000L)..(hour + 60_000L))
    }
}
