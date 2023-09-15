package net.wiedekopf.calview.mobile.ui.calendarselector

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import net.wiedekopf.calview.mobile.theme.CalViewTheme
import net.wiedekopf.wear.calviewlib.calendars.CalendarClient
import net.wiedekopf.wear.calviewlib.calendars.DummyCalendarClient


@Composable
fun CalendarSelectorBottomBar(calendarClient: CalendarClient) {
    BottomAppBar(actions = {
        Row(
            Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            //.padding(2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            calendarClient.getCalendars().forEach { item ->
                val itemColors = when (item.visible) {
                    true -> {
                        val itemColor = item.color.copy(alpha = 0.2f)
                        ButtonDefaults.outlinedButtonColors(
                            containerColor = itemColor,
                            contentColor = contentColorFor(backgroundColor = itemColor)
                        )
                    }

                    else -> ButtonDefaults.outlinedButtonColors()
                }
                OutlinedButton(
                    onClick = { /*TODO*/ },
                    shape = RectangleShape,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    colors = itemColors
                ) {
                    Text(
                        text = item.name,
                        textAlign = TextAlign.Center,
                        fontSize = 8.sp
                    )
                }
            }
        }
    })
}

@Preview
@Composable
fun BottomCalendarSelectorPreview() {
    CalViewTheme {
        val context = LocalContext.current
        val calendarClient = remember {
            DummyCalendarClient(contentResolver = context.contentResolver)
        }
        CalendarSelectorBottomBar(calendarClient = calendarClient)
    }
}