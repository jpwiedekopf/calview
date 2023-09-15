package net.wiedekopf.calview.mobile.ui

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import net.wiedekopf.calview.mobile.theme.CalViewTheme
import net.wiedekopf.calview.mobile.ui.calendarselector.CalendarSelectorBottomBar
import net.wiedekopf.calview.mobile.ui.permissions.MissingPermissionUi
import net.wiedekopf.wear.calviewlib.calendars.CalendarClient
import net.wiedekopf.wear.calviewlib.calendars.CalendarContractClient
import net.wiedekopf.wear.calviewlib.calendars.CalendarItem
import net.wiedekopf.calview.calviewlib.R as sharedR


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AppScaffold() {
    val calendarReadPermissionState =
        rememberPermissionState(permission = Manifest.permission.READ_CALENDAR)
    val context = LocalContext.current
    val calendarClient: CalendarClient by remember {
        mutableStateOf(CalendarContractClient(contentResolver = context.contentResolver))
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

@Composable
fun CalendarItemList(calendarClient: CalendarClient) {
    val calendarItems by remember {
        mutableStateOf(calendarClient.getCalendars())
    }
    LazyColumn {
        items(calendarItems, { it.id }) { cal ->
            CalendarCard(cal)
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