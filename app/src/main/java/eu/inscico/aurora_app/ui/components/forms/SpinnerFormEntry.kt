package eu.inscico.aurora_app.ui.components.forms

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
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
import eu.inscico.aurora_app.model.consumptions.TransportationType

@Composable
fun SpinnerFormEntry(
    title: String,
    selectedEntry: SpinnerItem.Entry<*>? = null,
    allEntries: List<SpinnerItem>,
    isRoundedDesign: Boolean = true,
    callback: (SpinnerItem.Entry<*>, SpinnerItem.Section<*>?) -> Unit
) {

    val openDropDown = remember {
        mutableStateOf(false)
    }

    val selectedItem = remember {
        mutableStateOf(selectedEntry)
    }

    val allItems = remember {
        mutableStateOf(allEntries)
    }

    fun <T> getSectionForSelectedItem(item: SpinnerItem.Entry<T>): SpinnerItem.Section<*>? {
        val allSections = allEntries.filterIsInstance<SpinnerItem.Section<T>>()

        allSections.forEach { section ->
            section.entries?.forEach { entry ->
                if (entry as? T == item.data) {
                    return section
                }

            }
        }
        return null
    }

    val modifier = if(isRoundedDesign){
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(shape = RoundedCornerShape(16.dp))
    } else {
        Modifier
            .fillMaxWidth()
    }

    Column(
        modifier = modifier
    ) {
        ListItem(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    openDropDown.value = !openDropDown.value
                },
            headlineContent = {
                Text(
                    text = title,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            },
            trailingContent = {

                Row(
                    horizontalArrangement = Arrangement.End,
                ) {

                    Text(
                        text = selectedItem.value?.name
                            ?: stringResource(id = R.string.form_spinner_item_please_chose),
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.onSecondary,
                            fontSize = 17.sp,
                            lineHeight = 16.sp,
                            textAlign = TextAlign.End
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

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

                        allItems.value.forEach {
                            when (it) {
                                is SpinnerItem.Entry<*> -> {
                                    DropdownMenuItem(
                                        onClick = {
                                            selectedItem.value = it
                                            openDropDown.value = false

                                            val itemSection = getSectionForSelectedItem(it)
                                            callback.invoke(it, itemSection)
                                        },
                                        text = {
                                            Text(
                                                text = it.name,
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }
                                    )
                                }
                                is SpinnerItem.Section<*> -> {
                                    Column(modifier = Modifier.padding(start = 4.dp)) {
                                        Text(
                                            text = it.name,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSecondary
                                        )
                                        Divider()
                                    }
                                }
                            }
                        }
                    }
                }
            })
    }
}

sealed class SpinnerItem {
    data class Section<T>(
        val section: Any,
        val name: String,
        val entries: List<T>?
    ) : SpinnerItem()

    data class Entry<T>(
        val name: String,
        val data: T,

        ) : SpinnerItem()
}