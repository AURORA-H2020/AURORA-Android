package eu.inscico.aurora_app.ui.components.forms

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.ui.components.recurringConsumptions.getSelectedEntries

@Composable
fun MultiSelectSpinner(
    title: String,
    selectedEntries: List<MultiSelectEntry<*>>? = null,
    allEntries: List<MultiSelectEntry<*>>? = null,
    isRoundedDesign: Boolean = true,
    isReadOnly: Boolean = false,
    callback: (List<MultiSelectEntry<*>>?) -> Unit

) {

    val openDropDown = remember {
        mutableStateOf(false)
    }

    val selectedItems = remember {
        mutableStateOf(selectedEntries)
    }

    val allItems = remember {
        mutableStateOf(allEntries)
    }

    val modifier = if (isRoundedDesign) {
        Modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(16.dp))
    } else {
        Modifier
            .fillMaxWidth()
    }

    val colors: ListItemColors =
        if (isReadOnly) {
            ListItemDefaults.colors(
                containerColor = MaterialTheme.colorScheme.secondary
            )
        } else {
            ListItemDefaults.colors()
        }

    ListItem(
        colors = colors,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (!isReadOnly) {
                    openDropDown.value = !openDropDown.value
                }
            },
        headlineContent = {
            Text(
                text = title,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )
        },
        trailingContent = {

            Row(
                horizontalArrangement = Arrangement.End,
            ) {

                val selectedItemsSeparated = selectedItems.value?.joinToString(", ") {
                    it.name
                }

                Text(
                    text = selectedItemsSeparated
                        ?: stringResource(id = R.string.form_spinner_item_please_chose),
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.onSecondary,
                        fontSize = 17.sp,
                        lineHeight = 16.sp,
                        textAlign = TextAlign.End
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                Box {

                    Image(
                        painterResource(id = R.drawable.outline_arrow_drop_down_24),
                        contentDescription = "",
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSecondary),
                        alignment = Alignment.CenterEnd
                    )

                    DropdownMenu(
                        expanded = openDropDown.value,
                        onDismissRequest = { openDropDown.value = false },
                    ) {

                        allItems.value?.forEach {
                            DropdownMenuItem(
                                onClick = {
                                    it.isSelected = !it.isSelected
                                    selectedItems.value = getSelectedEntries(allItems.value)
                                    callback.invoke(allItems.value)
                                },
                                leadingIcon = {
                                    if (it.isSelected) {
                                        Image(
                                            painter = painterResource(id = R.drawable.round_circle_24),
                                            modifier = Modifier
                                                .size(40.dp)
                                                .padding(horizontal = 7.dp),
                                            contentDescription = "",
                                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                                        )
                                    } else {
                                        Image(
                                            painter = painterResource(id = R.drawable.outline_circle_24),
                                            modifier = Modifier
                                                .size(40.dp)
                                                .padding(horizontal = 7.dp),
                                            contentDescription = "",
                                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                                        )
                                    }
                                },
                                text = {
                                    Text(
                                        text = it.name,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            )
                        }
                    }
                }
            }
        })
}

data class MultiSelectEntry<T>(
    val name: String,
    val data: T,
    var isSelected: Boolean
)