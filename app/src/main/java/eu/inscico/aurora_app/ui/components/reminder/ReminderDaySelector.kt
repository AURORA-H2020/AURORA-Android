package eu.inscico.aurora_app.ui.components.reminder

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.model.Frequency.Companion.toDisplayName
import eu.inscico.aurora_app.model.reminder.CalendarDayItem
import eu.inscico.aurora_app.model.reminder.CalendarMonthItem
import eu.inscico.aurora_app.utils.CalendarUtils
import java.text.SimpleDateFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderDaySelector(
    initialValue: CalendarDayItem,
    month: CalendarMonthItem,
    callback: (CalendarDayItem) -> Unit
) {

    val days = CalendarUtils.getDaysOfMonth(month.number)

    val context = LocalContext.current

    val interactionSource = remember { MutableInteractionSource() }

    val selectedItem = remember {
        mutableStateOf(initialValue)
    }

    val isExpanded = remember {
        mutableStateOf(false)
    }

    ListItem(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                // This is for removing ripple when Row is clicked
                indication = null,
                role = Role.Switch,
                onClick = {
                    isExpanded.value = !isExpanded.value
                }
            ),
        headlineContent = { Text(text = stringResource(id = R.string.settings_notifications_day_title)) },
        trailingContent = {
            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.End,
            ) {
                Row(
                    horizontalArrangement = Arrangement.End,
                ) {

                    Text(
                        text = selectedItem.value.day.toString(),
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.onSecondary,
                            fontSize = 17.sp,
                            lineHeight = 16.sp,
                            textAlign = TextAlign.End
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Image(
                        Icons.Outlined.ArrowDropDown,
                        contentDescription = "",
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSecondary),
                        alignment = Alignment.CenterEnd
                    )

                    DropdownMenu(
                        expanded = isExpanded.value,
                        onDismissRequest = { isExpanded.value = false },
                    ) {
                        days.forEach {
                            DropdownMenuItem(
                                onClick = {
                                    selectedItem.value = it
                                    isExpanded.value = false
                                    callback.invoke(selectedItem.value)
                                },
                                text = { Text(text = it.day.toString()) }
                            )
                        }
                    }
                }
            }
        }
    )
}