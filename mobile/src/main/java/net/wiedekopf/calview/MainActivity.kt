package net.wiedekopf.calview

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.wiedekopf.calview.calviewlib.R
import net.wiedekopf.calview.calviewlib.calendars.CalendarClient
import net.wiedekopf.calview.calviewlib.calendars.CalendarItem
import net.wiedekopf.calview.calviewlib.settings.CounterPreferences
import net.wiedekopf.calview.ui.theme.CalViewTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalViewTheme(dynamicColor = false) {
                Scaffold { paddingValues ->
                    CalendarPermissionRequester(paddingValues)
                }
            }
        }
    }
}

@Composable
fun CalendarList() {
    val context = LocalContext.current
    val calendarClient by remember {
        mutableStateOf(CalendarClient(contentResolver = context.contentResolver))
    }
    val calendars by remember {
        mutableStateOf(calendarClient.getCalendars())
    }
    LazyColumn() {
        items(calendars, { it.id }) { cal ->
            CalendarCard(cal)
        }
    }
}

@Composable
fun CalendarCard(cal: CalendarItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .alpha(
                    when (cal.visible) {
                        true -> 1.0f
                        false -> 0.5f
                    }
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.Start),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .aspectRatio(1f, true)
                        .clip(CircleShape)
                        .background(cal.color)
                )
                Text("# ${cal.id}", fontStyle = FontStyle.Italic)
                Text(cal.name, fontWeight = FontWeight.Bold)
            }
            Text(text = cal.displayName ?: "no display name")
        }
    }
}

@Composable
@OptIn(ExperimentalPermissionsApi::class)
fun CalendarPermissionRequester(paddingValues: PaddingValues) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)) {
        PreferencesTester()
        val calendarReadPermissionState =
            rememberPermissionState(permission = android.Manifest.permission.READ_CALENDAR)
        when (calendarReadPermissionState.status.isGranted) {
            true -> CalendarList()
            else -> {
                val textToShow = when (calendarReadPermissionState.status.shouldShowRationale) {
                    true -> stringResource(id = R.string.calendar_access_required_rationale)
                    else -> stringResource(id = R.string.calendar_access_required)
                }
                Text(textToShow)
                Button(onClick = {
                    calendarReadPermissionState.launchPermissionRequest()
                }) {
                    Text(stringResource(id = R.string.request_permissions))
                }
            }
        }
    }
}

@Composable
fun PreferencesTester() {
    val context = LocalContext.current
    val store = remember {
        CounterPreferences(context)
    }
    val counterValue by store.getCounter().collectAsState(initial = 0)

    Column(Modifier.fillMaxWidth()) {
        Button(onClick = {
            CoroutineScope(Dispatchers.IO).launch {
                store.incrementCounter()
            }
        }) {
            Text("Counter: $counterValue")
        }
        Button(onClick = {}, colors = ButtonDefaults.buttonColors(
            containerColor = colorScheme.secondaryContainer,
            contentColor = colorScheme.onSecondaryContainer
        )) {
            Text("Secondary")
        }
    }
}
