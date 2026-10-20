package com.example.composeapp.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {
    /** Parses "MM/dd/yyyy HH:mm"; returns null instead of throwing on malformed input. */
    fun String.parseDate(): Calendar? {
        val formatter = SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.ROOT)
        val date = runCatching { formatter.parse(this@parseDate) }.getOrNull() ?: return null
        return Calendar.getInstance().apply { time = date }
    }

    /** Formats a "MM/dd/yyyy HH:mm" timestamp as a short clock time, e.g. "9:15 PM". */
    fun String.toDisplayTime(): String? = parseDate()?.let {
        SimpleDateFormat("h:mm a", Locale.getDefault()).format(it.time)
    }

    /** Formats an epoch-millis timestamp as a short clock time, e.g. "9:15 PM". */
    fun displayTime(epochMillis: Long): String =
        SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(epochMillis))

    /** Inbox-style label: the clock time for today, "Yesterday", or a short date such as "Oct 18". */
    fun inboxTimeLabel(epochMillis: Long, now: Long = System.currentTimeMillis()): String {
        val then = Calendar.getInstance().apply { timeInMillis = epochMillis }
        val day = Calendar.getInstance().apply { timeInMillis = now }
        if (then.isSameDayAs(day)) return displayTime(epochMillis)
        day.add(Calendar.DAY_OF_YEAR, -1)
        if (then.isSameDayAs(day)) return "Yesterday"
        return SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(epochMillis))
    }

    private fun Calendar.isSameDayAs(other: Calendar): Boolean =
        get(Calendar.YEAR) == other.get(Calendar.YEAR) && get(Calendar.DAY_OF_YEAR) == other.get(Calendar.DAY_OF_YEAR)
}
