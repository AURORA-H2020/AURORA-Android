package eu.inscico.aurora_app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.*

@Composable
@ExperimentalMaterial3Api
fun FormEntry(
    title: String,
    formEntryType: FormEntryType,
    initialItem: String,
    items: List<String>? = null,
    callback: ((name: String, index: Int?) -> Unit)? = null,
    readOnly: Boolean = false
) {

    val selectedItem = remember {
        mutableStateOf(initialItem)
    }

    val isExpanded = remember {
        mutableStateOf(false)
    }

    ListItem(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .fillMaxWidth(),
        headlineText = {Text(text = title)},
        trailingContent = {
            when (formEntryType) {
                FormEntryType.TEXT_INPUT -> {
                    Row(
                        horizontalArrangement = Arrangement.End,
                    ) {
                        BasicTextField(
                            textStyle = TextStyle(
                                color = MaterialTheme.colorScheme.onSecondary,
                                fontSize = 17.sp,
                                lineHeight = 16.sp,
                                textAlign = TextAlign.End
                            ),
                            readOnly = readOnly,
                            value = selectedItem.value,
                            onValueChange = {
                                selectedItem.value = it
                                callback?.invoke(it, null)
                            })
                    }
                }
                FormEntryType.SPINNER -> {
                    Row(
                        modifier = Modifier.clickable(
                            onClick = {
                                if(!readOnly){
                                    isExpanded.value = !isExpanded.value
                                }
                            },
                        ),
                        horizontalArrangement = Arrangement.End,
                    ) {

                        Text(
                            text = selectedItem.value,
                            style = TextStyle(
                                color = MaterialTheme.colorScheme.onSecondary,
                                fontSize = 17.sp,
                                lineHeight = 16.sp,
                                textAlign = TextAlign.End
                            )
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        if(!readOnly) {
                            Image(
                                Icons.Outlined.ArrowDropDown,
                                contentDescription = "",
                                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSecondary),
                                alignment = Alignment.CenterEnd
                            )
                        }

                        DropdownMenu(
                            expanded = isExpanded.value,
                            onDismissRequest = { isExpanded.value = false },
                        ) {
                            items?.forEach {
                                DropdownMenuItem(onClick = {
                                    selectedItem.value = it
                                    isExpanded.value = false
                                    callback?.invoke(it, items.indexOf(it))
                                },
                                    text = { Text(text = it) }
                                )
                            }
                        }
                    }
                }
            }
        }
    )
    Divider()
}

enum class FormEntryType {
    TEXT_INPUT,
    SPINNER
}