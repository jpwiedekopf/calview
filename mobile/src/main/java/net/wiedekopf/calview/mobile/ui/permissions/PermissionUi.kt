package net.wiedekopf.calview.mobile.ui.permissions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.shouldShowRationale
import net.wiedekopf.calview.calviewlib.R


@Composable
@OptIn(ExperimentalPermissionsApi::class)
fun MissingPermissionUi(calendarReadPermissionState: PermissionState) {
    if (calendarReadPermissionState.status.isGranted) {
        return
    }
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
    ) {
        val textToShow = when (calendarReadPermissionState.status.shouldShowRationale) {
            true -> stringResource(id = R.string.calendar_access_required_rationale)
            else -> stringResource(id = R.string.calendar_access_required)
        }
        Text(
            text = textToShow,
            modifier = Modifier.fillMaxWidth(0.8f),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineSmall
        )
        Button(onClick = {
            calendarReadPermissionState.launchPermissionRequest()
        }) {
            Text(stringResource(id = R.string.request_permissions))
        }
    }
}