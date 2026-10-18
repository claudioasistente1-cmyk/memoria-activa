package com.claudio.memoria.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

object TimeUtils {

    fun today(zone: ZoneId = ZoneId.systemDefault()): LocalDate = LocalDate.now(zone)

    fun todayKey(zone: ZoneId = ZoneId.systemDefault()): String = today(zone).toString()

    /**
     * Milliseconds until the next occurrence of [hour]:00 local time.
     * Returns at least one minute so the worker is not scheduled in the past.
     */
    fun millisUntilNext(hour: Int, minute: Int, now: LocalDateTime = LocalDateTime.now()): Long {
        var target = LocalDateTime.of(now.toLocalDate(), LocalTime.of(hour.coerceIn(0, 23), minute.coerceIn(0, 59)))
        if (!target.isAfter(now)) target = target.plusDays(1)
        val millis = target.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() -
            now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        return millis.coerceAtLeast(60_000L)
    }

    fun epochMillis(date: LocalDate): Long =
        date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

    fun dayKey(date: LocalDate): String = date.toString()
}
