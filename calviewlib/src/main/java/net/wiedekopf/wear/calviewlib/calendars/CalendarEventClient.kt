package net.wiedekopf.wear.calviewlib.calendars

import android.provider.CalendarContract
import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull
import net.wiedekopf.calview.calviewlib.R
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

private const val TAG = "CalendarEventClient"
class CalendarEventClient(private val calendarClient: CalendarClient) {

    fun getCalendarItems(): List<EventItem> {
        val calendars = calendarClient.getCalendars()
        // we only look forward (well, we don't really look back)
        val oneMonthAgoMillis =
            Instant.now().atOffset(ZoneOffset.UTC).minusDays(2L).also {
                Log.d(TAG, "querying events starting at ${it.format(DateTimeFormatter.ISO_DATE_TIME)}")
            }.toInstant().toEpochMilli()
        return calendarClient.contentResolver.query(
            contentUri,
            EVENT_PROJECTION,
            "${CalendarContract.Events.DTSTART} >= ?",
            arrayOf(oneMonthAgoMillis.toString()),
            null
        )?.use { cursor ->
            val resultItems = mutableListOf<EventItem>()
            while (cursor.moveToNext()) {
                val calendarId = cursor.getLong(PROJECTION_CALENDAR_ID_INDEX)
                val calendar = calendars.find { it.id == calendarId }
                    ?: throw IllegalStateException("No calendar matching the ID $calendarId was found?!")
                resultItems += EventItem(
                    id = cursor.getLong(PROJECTION_ID_INDEX),
                    calendar = calendar,
                    title = cursor.getStringOrNull(PROJECTION_TITLE_INDEX),
                    eventLocation = cursor.getStringOrNull(PROJECTION_EVENT_LOCATION_INDEX),
                    status = mapStatusToStringResource(
                        cursor.getIntOrNull(
                            PROJECTION_STATUS_INDEX
                        )
                    ),
                    dtStart = cursor.getLong(PROJECTION_DTSTART_INDEX).let {
                        Instant.ofEpochMilli(it)
                    },
                    dtEnd = cursor.getLong(PROJECTION_DTEND_INDEX).let {
                        Instant.ofEpochMilli(it)
                    },
                    duration = cursor.getStringOrNull(PROJECTION_DURATION_INDEX),
                    allDay = cursor.getIntOrNull(PROJECTION_ALL_DAY_INDEX) == 1,
                    availability = mapAvailabilityStringResource(
                        cursor.getIntOrNull(
                            PROJECTION_AVAILABILITY_INDEX
                        )
                    ),
                    rRule = cursor.getStringOrNull(PROJECTION_RRULE_INDEX),
                    displayColor = cursor.getIntOrNull(PROJECTION_DISPLAY_COLOR_INDEX)?.let {
                        Color(it)
                    },
                    visible = cursor.getIntOrNull(PROJECTION_VISIBLE_INDEX) == 1
                )
            }
            val now = Instant.now()
            // TODO still relevant?
            val sorted = resultItems.sortedBy {
                it.dtStart
//            }.filter {
//                if (now >= it.dtStart) {
//                    return@filter now <= it.dtEnd
//                } else {
//                    return@filter true
//                }
            }
            return sorted
        } ?: emptyList()
    }

    companion object {
        private val contentUri = CalendarContract.Events.CONTENT_URI
        private val EVENT_PROJECTION = arrayOf(
            CalendarContract.Events._ID,
            CalendarContract.Events.TITLE,
            CalendarContract.Events.EVENT_LOCATION,
            CalendarContract.Events.STATUS,
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.DTEND,
            CalendarContract.Events.DURATION,
            CalendarContract.Events.ALL_DAY,
            CalendarContract.Events.AVAILABILITY,
            CalendarContract.Events.RRULE,
            CalendarContract.Events.DISPLAY_COLOR,
            CalendarContract.Events.VISIBLE,
            CalendarContract.Events.CALENDAR_ID,
        )
        private const val PROJECTION_ID_INDEX = 0
        private const val PROJECTION_TITLE_INDEX = 1
        private const val PROJECTION_EVENT_LOCATION_INDEX = 2
        private const val PROJECTION_STATUS_INDEX = 3
        private const val PROJECTION_DTSTART_INDEX = 4
        private const val PROJECTION_DTEND_INDEX = 5
        private const val PROJECTION_DURATION_INDEX = 6
        private const val PROJECTION_ALL_DAY_INDEX = 7
        private const val PROJECTION_AVAILABILITY_INDEX = 8
        private const val PROJECTION_RRULE_INDEX = 9
        private const val PROJECTION_DISPLAY_COLOR_INDEX = 10
        private const val PROJECTION_VISIBLE_INDEX = 11
        private const val PROJECTION_CALENDAR_ID_INDEX = 12

        fun mapStatusToStringResource(status: Int?): Int = when (status) {
            CalendarContract.Events.STATUS_CANCELED -> R.string.canceled
            CalendarContract.Events.STATUS_CONFIRMED -> R.string.confirmed
            CalendarContract.Events.STATUS_TENTATIVE -> R.string.tentative
            else -> R.string.unknown
        }

        fun mapAvailabilityStringResource(availability: Int?): Int {
            return when (availability) {
                CalendarContract.Events.AVAILABILITY_BUSY -> R.string.busy
                CalendarContract.Events.AVAILABILITY_FREE -> R.string.free
                CalendarContract.Events.AVAILABILITY_TENTATIVE -> R.string.tentative
                else -> R.string.unknown
            }
        }
    }

}

data class EventItem(
    val id: Long,
    val calendar: CalendarItem,
    val title: String?,
    val eventLocation: String?,
    @StringRes val status: Int,
    val dtStart: Instant,
    val dtEnd: Instant,
    val duration: String?,
    val allDay: Boolean?,
    @StringRes val availability: Int,
    val rRule: String?,
    val displayColor: Color?,
    val visible: Boolean?,
)