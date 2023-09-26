package net.wiedekopf.calview.mobile.ui

import android.Manifest
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import net.wiedekopf.calview.mobile.theme.CalViewTheme
import net.wiedekopf.calview.mobile.ui.calendarselector.CalendarSelectorBottomBar
import net.wiedekopf.calview.mobile.ui.permissions.MissingPermissionUi
import net.wiedekopf.wear.calviewlib.calendars.CalendarClient
import net.wiedekopf.wear.calviewlib.calendars.EventItem
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import net.wiedekopf.calview.calviewlib.R as sharedR


private const val TAG = "Scaffold"

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AppScaffold() {
    val calendarReadPermissionState =
        rememberPermissionState(permission = Manifest.permission.READ_CALENDAR)
    val context = LocalContext.current
    val calendarClient: CalendarClient by remember {
        mutableStateOf(CalendarClient(contentResolver = context.contentResolver))
    }
    Scaffold(topBar = {
        AppTopBar(onPrefsClick = { })
    }, bottomBar = {
        if (calendarReadPermissionState.status.isGranted) {
            CalendarSelectorBottomBar(calendarClient)
        }
    }) { scaffoldPadding ->
        Column(Modifier.padding(scaffoldPadding)) {
            when (calendarReadPermissionState.status.isGranted) {
                false -> MissingPermissionUi(calendarReadPermissionState)
                else -> CalendarItemList(calendarClient)
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CalendarItemList(calendarClient: CalendarClient) {
    var refreshCounter by remember {
        mutableIntStateOf(0)
    }
    val events = remember {
        mutableStateListOf<EventItem>()
    }
    var refreshing by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(key1 = refreshCounter) {
        Log.d(TAG, "refreshing with $refreshCounter")
        refreshing = true
        events.clear()
        calendarClient.eventClient.getCalendarItems().let {
            events.addAll(it)
            refreshing = false
        }
    }
    val lazyListState = rememberLazyListState()
    val pullRefreshState = rememberPullRefreshState(refreshing = refreshing, onRefresh = {
        refreshCounter++
    })
    Box(Modifier.pullRefresh(pullRefreshState)) {
        LazyColumn(
            state = lazyListState
        ) {
            item {

            }
            items(events, { it.id }) { calItem ->
                CalendarEventCard(calItem)
            }
        }
        PullRefreshIndicator(
            refreshing = refreshing, state = pullRefreshState, modifier = Modifier.align(
                Alignment.TopCenter
            )
        )
    }

}

@Composable
fun CalendarEventCard(calItem: EventItem) {
    val cardBorderColor = remember {
        when (calItem.displayColor) {
            null -> calItem.calendar.color
            else -> calItem.displayColor!!
        }
    }
    OutlinedCard(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        border = BorderStroke(4.dp, cardBorderColor),
        colors = CardDefaults.outlinedCardColors(
            containerColor = colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            listOf(
                calItem.title to typography.titleSmall,
                when (calItem.eventLocation?.isBlank()) {
                    null, true -> null
                    else -> "@ ${calItem.eventLocation}"
                } to typography.bodyMedium,
//                stringResource(id = calItem.status) to null,
//                stringResource(id = calItem.availability) to null,
                calItem.dtStart.atZone(ZoneId.systemDefault())
                    .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) to null,
                calItem.dtEnd.atZone(ZoneId.systemDefault())
                    .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) to null,
                calItem.allDay.let {
                    "all-day:" + when (it) {
                        null -> "unknown"
                        false -> "no"
                        true -> "yes"
                    }
                } to null
            ).map { (text, chosenTextStyle) ->
                val textStyle = chosenTextStyle ?: typography.bodySmall
                if (text != null) {
                    Text(text = text, style = textStyle, color = colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Preview
@Composable
fun AppTopBarPreview() {
    CalViewTheme {
        AppTopBar(onPrefsClick = {})
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(onPrefsClick: () -> Unit) {
    CenterAlignedTopAppBar(title = {
        Text(stringResource(id = sharedR.string.app_name))
    },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = colorScheme.primaryContainer,
            titleContentColor = colorScheme.onPrimaryContainer,
            actionIconContentColor = colorScheme.onPrimaryContainer
        ),
        actions = {
            IconButton(onClick = onPrefsClick) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = stringResource(id = sharedR.string.settings)
                )
            }
        })
}