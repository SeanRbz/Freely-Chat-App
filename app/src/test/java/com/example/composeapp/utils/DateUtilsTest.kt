package com.example.composeapp.utils

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar

class DateUtilsTest {

    // Oct 20, 3:30 PM local time.
    private val now = Calendar.getInstance().apply { set(2026, Calendar.OCTOBER, 20, 15, 30, 0) }.timeInMillis

    private fun hoursAgo(hours: Int) = now - hours * 3_600_000L

    @Test
    fun todayShowsTheClockTime() {
        assertEquals(DateUtils.displayTime(hoursAgo(2)), DateUtils.inboxTimeLabel(hoursAgo(2), now))
    }

    @Test
    fun previousDayShowsYesterday() {
        assertEquals("Yesterday", DateUtils.inboxTimeLabel(hoursAgo(20), now))
    }
}
