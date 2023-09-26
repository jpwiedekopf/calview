package net.wiedekopf.calview.mobile.ui.calendarselector

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import net.wiedekopf.wear.calviewlib.calendars.CalendarClient


@Composable
fun CalendarSelectorBottomBar(calendarClient: CalendarClient) {
    val calendars = remember {
        calendarClient.getCalendars()
    }
    val calendarGroups = remember {
        calendars.chunked(4)
    }
    val rowHeight = 64.dp
    BottomAppBar(
        modifier = Modifier
            .height(rowHeight * calendarGroups.size)
            .padding(0.dp),
        actions = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(calendarGroups.size * rowHeight)
            ) {
                for (calendarGroup in calendarGroups) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(rowHeight)
                            .padding(horizontal = 0.5.dp, vertical = 0.2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        calendarGroup.forEach { item ->
                            val itemColors = when (item.visible) {
                                true -> {
                                    val itemColor = item.color//.copy(alpha = 0.2f)
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
                                    overflow = TextOverflow.Ellipsis,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        })
}