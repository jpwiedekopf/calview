package net.wiedekopf.wear.calviewlib.calendars

import android.content.ContentResolver
import android.provider.CalendarContract
import androidx.compose.ui.graphics.Color
import androidx.core.database.getStringOrNull

class CalendarClient(
    val contentResolver: ContentResolver
) {

    val eventClient = CalendarEventClient(this)
    fun getCalendars(): List<CalendarItem> {
        return contentResolver.query(
            /* uri = */ contentUri,
            /* projection = */ EVENT_PROJECTION,
            /* selection = */ "",
            /* selectionArgs = */ emptyArray<String>(),
            /* sortOrder = */ null
        )?.use { cursor ->
            val resultList = mutableListOf<CalendarItem>()
            while (cursor.moveToNext()) {
                CalendarItem(
                    id = cursor.getLong(PROJECTION_ID_INDEX),
                    name = cursor.getString(PROJECTION_NAME_INDEX),
                    displayName = cursor.getStringOrNull(PROJECTION_DISPLAY_NAME_INDEX),
                    color = Color(cursor.getInt(PROJECTION_CALENDAR_COLOR_INDEX)),
                    visible = cursor.getInt(PROJECTION_VISIBLE_INDEX) == 1,
                    syncEvents = cursor.getInt(PROJECTION_SYNC_EVENTS_INDEX) == 1,
                    accountName = cursor.getString(PROJECTION_ACCOUNT_NAME_INDEX),
                    accountType = cursor.getString(PROJECTION_ACCOUNT_TYPE_INDEX)
                ).let {
                    resultList.add(it)
                }
            }
            resultList.sortedBy {
                it.id
            }
        } ?: listOf()
    }

    companion object {
        private val contentUri = CalendarContract.Calendars.CONTENT_URI
        private val EVENT_PROJECTION = arrayOf(
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.NAME,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
            CalendarContract.Calendars.CALENDAR_COLOR,
            CalendarContract.Calendars.VISIBLE,
            CalendarContract.Calendars.SYNC_EVENTS,
            CalendarContract.Calendars.ACCOUNT_NAME,
            CalendarContract.Calendars.ACCOUNT_TYPE,
        )
        private const val PROJECTION_ID_INDEX = 0
        private const val PROJECTION_NAME_INDEX = 1
        private const val PROJECTION_DISPLAY_NAME_INDEX = 2
        private const val PROJECTION_CALENDAR_COLOR_INDEX = 3
        private const val PROJECTION_VISIBLE_INDEX = 4
        private const val PROJECTION_SYNC_EVENTS_INDEX = 5
        private const val PROJECTION_ACCOUNT_NAME_INDEX = 6
        private const val PROJECTION_ACCOUNT_TYPE_INDEX = 7
    }
}

data class CalendarItem(
    val id: Long,
    val name: String,
    val displayName: String?,
    val color: Color,
    val visible: Boolean,
    val syncEvents: Boolean,
    val accountName: String,
    val accountType: String
)